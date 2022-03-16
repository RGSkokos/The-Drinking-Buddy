package com.example.drinkingbuddy.Views;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Controllers.SharedPreferencesHelper;
import com.example.drinkingbuddy.Models.Breathalyzer;
import com.example.drinkingbuddy.R;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {
    protected BluetoothAdapter bluetoothAdapter;
    protected Button newBreath;
    protected TextView response;
    protected Toolbar toolbar;
    protected DBHelper myDB;
    protected List<Breathalyzer> breathalyzer_values;
    protected DecimalFormat decimalFormat = new DecimalFormat("0.0000");
    private SharedPreferencesHelper sharedPreferencesHelper;
    protected int profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB = new DBHelper(this);
        setContentView(R.layout.activity_home);
        setTitle("Drinking Buddy Home");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initializeComponents();
        sharedPreferencesHelper = new SharedPreferencesHelper(HomePage.this);

        // Set up the toolbar
        setSupportActionBar(toolbar);
     //   ((BluetoothHelper)this.getApplicationContext()).BT.connectToBreathalyzer();
    }

    @Override
    public void onBackPressed() { } // Don't allow back button to lead to login page

    // Link Variables to Components in .XML file
    protected void initializeComponents() {
        newBreath = (Button) findViewById(R.id.newBreath);
        response = (TextView) findViewById(R.id.response);
        newBreath.setOnClickListener(onClickBreathButton);
        toolbar = findViewById(R.id.toolbarHome);
    }

    // Display the Database
    public void displayResults () {
        breathalyzer_values = myDB.getAllResults();
        ArrayList<String> sampledResults = new ArrayList<>();
        for (int i = breathalyzer_values.size() - 1; i >= 0; i--) {
            double temp = Double.parseDouble(breathalyzer_values.get(i).getResult());
            String timeStamp = breathalyzer_values.get(i).getTimeStamp();
            temp = (((temp - 1500) / 5000));
            temp = (temp<0) ? 0 : temp; //this is to avoid negative values and are now considered absolute zero for contraint purposes

            sampledResults.add(String.valueOf(decimalFormat.format(temp) + "%, Time:" + timeStamp));
        }
        response.setText("Your Blood Alchool Level is: " + sampledResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayResults(); //will display nothing if never entered data or most recent value of breathelizer

        // Checks if a user is logged in by getting profile ID
        if (sharedPreferencesHelper.getLoginId() == 0) {
            goToLogin();
        } else {
            profileId = sharedPreferencesHelper.getLoginId();
        }
    }
    private final View.OnClickListener onClickBreathButton= new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            openLoading();
        }
    };
    protected void openLoading(){        //open settings class on click
        Intent i = new Intent(this, LoadActivity.class);
        startActivity(i);
    }

    protected void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}


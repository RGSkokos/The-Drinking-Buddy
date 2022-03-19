package com.example.drinkingbuddy.Views;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Controllers.SharedPreferencesHelper;
import com.example.drinkingbuddy.Models.Breathalyzer;
import com.example.drinkingbuddy.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.List;

public class HomePage extends AppCompatActivity {

    //instance variables
    protected BluetoothAdapter bluetoothAdapter;
    protected Button newBreath;
    protected FloatingActionButton SpecifyDrinkButton;
    protected TextView response;
    protected TextView TimeStampTextview;
    protected TextView CurrentDrinkTextView;
    protected Toolbar toolbar;
    protected DBHelper myDB;
    protected List<Breathalyzer> breathalyzer_values;
    protected DecimalFormat decimalFormat = new DecimalFormat("0.0000");
    private SharedPreferencesHelper sharedPreferencesHelper;
    protected int profileId;
    private String type_of_drink;

//region Activity Life Cycle
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

        SpecifyDrinkButton.setOnClickListener(view -> OpenFragment());
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayResults(); //will display nothing if never entered data or most recent value of breathalyzer

        // Checks if a user is logged in by getting profile ID
        if (sharedPreferencesHelper.getLoginId() == 0) {
            goToLogin();
        } else {
            profileId = sharedPreferencesHelper.getLoginId();
        }
    }
//endregion

//region Menu Methods

    //REFERENCE:https://guides.codepath.com/android/using-the-app-toolbar#:~:text=Toolbar%20was%20introduced%20in%20Android,to%20API%2021%20and%20above.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.trendsMenuItem) {
            if (breathalyzer_values.size() > 0) {
                goToTrends();
            } else {
                Toast.makeText(getApplicationContext(), "Must have at least 1 measurement to see trend", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
//endregion

//region Helper Methods

    @Override
    public void onBackPressed() { } // Don't allow back button to lead to login page

    // Link Variables to Components in .XML file
    protected void initializeComponents() {
        newBreath = findViewById(R.id.newBreath);
        response =  findViewById(R.id.response);
        CurrentDrinkTextView = findViewById(R.id.CurrentDrinktextView);
        newBreath.setOnClickListener(onClickBreathButton);
        toolbar = findViewById(R.id.toolbarHome);
        TimeStampTextview = findViewById(R.id.TimeStampTextView);
        SpecifyDrinkButton = findViewById(R.id.SpecifyDrink);

        type_of_drink = "Unknown";
    }

    //fragment open for type of drink
    private void OpenFragment() {
        TypeOfDrinkFragment dialog = new TypeOfDrinkFragment();
        dialog.show(getSupportFragmentManager(), "TypeOfDrink");
    }


    // Display the Database
    @SuppressLint("SetTextI18n")
    public void displayResults () {
        breathalyzer_values = myDB.getAllResults();
        String drink = "";
        double temp = 0;
        String timeStamp = "";
        if(myDB.ReturnDrinkTypes().size() > 0) {
            drink = myDB.ReturnDrinkTypes().get(myDB.ReturnDrinkTypes().size() - 1);
        }
        if(breathalyzer_values.size() > 0)
        {
            temp = Double.parseDouble(breathalyzer_values.get(breathalyzer_values.size()-1).getResult());
            timeStamp = breathalyzer_values.get(breathalyzer_values.size() - 1).getTimeStamp();
            temp = (((temp) / 5000)); //second value in numerator needs to be based on calibration
            temp = (temp<0) ? 0 : temp; //this is to avoid negative values and are now considered absolute zero for constraint purposes
        }



        response.setText("Your Blood Alcohol Level is: " + decimalFormat.format(temp) + "%");
        TimeStampTextview.setText("Measurement Taken: " + timeStamp);
        CurrentDrinkTextView.setText("Last Drink: " + drink);

    }

    private final View.OnClickListener onClickBreathButton= new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            openLoading();
            myDB.SaveDrinkType(type_of_drink);
            type_of_drink = "Unknown";
        }
    };

    @SuppressLint("SetTextI18n")
    public void setTypeOfDrink(String type) {
        CurrentDrinkTextView.setText("Current Drink: " + type);
        type_of_drink = type;
    }
//endregion

//region Intent Methods
    protected void openLoading(){        //open settings class on click
        Intent i = new Intent(this, LoadActivity.class);
        startActivity(i);
    }

    protected void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void goToTrends() {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }
    //endregion
}


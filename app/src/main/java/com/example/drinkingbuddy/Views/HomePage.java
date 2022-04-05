package com.example.drinkingbuddy.Views;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Controllers.FirebaseHelper;
import com.example.drinkingbuddy.Models.Breathalyzer;
import com.example.drinkingbuddy.Models.Profile;
import com.example.drinkingbuddy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.text.DecimalFormat;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private static String MODULE_MAC;
    //instance variables
    protected BluetoothAdapter bluetoothAdapter;
    protected Button newBreath;
    protected FloatingActionButton specifyDrinkButton;
    protected TextView response;
    protected TextView TimeStampTextview;
    protected TextView CurrentDrinkTextView;
    protected Toolbar toolbar;
    protected BottomNavigationView bottomNav;
    protected DBHelper myDB;
    protected List<Breathalyzer> breathalyzer_values;
    protected DecimalFormat decimalFormat = new DecimalFormat("0.0000");
    private FirebaseHelper firebaseHelper;
    protected SharedPreferences cannotConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB = new DBHelper(this);
        firebaseHelper = new FirebaseHelper(this);
        setContentView(R.layout.activity_home);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initializeComponents();
        SharedPreferences cannotConnect = getSharedPreferences("noConnection", Context.MODE_PRIVATE);
        try {
            if(!cannotConnect.getString("noConnection", null).equals("")) {
                Toast toast = Toast.makeText(getApplicationContext(), cannotConnect.getString("noConnection", null), Toast.LENGTH_LONG);
                toast.show();
            }
        }

        catch(Exception e) { }
        SharedPreferences.Editor connectionEditor = cannotConnect.edit();
        connectionEditor.putString("noConnection", "");
        connectionEditor.apply();

        setSupportActionBar(toolbar);

        specifyDrinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToDrinkInputActivity();
            }
        });

        // Set Home selected
        bottomNav.setSelectedItemId(R.id.homeBottomMenuItem);

        // Perform item selected listener
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.homeBottomMenuItem:
                        return true;
                    case R.id.graphsBottomMenuItem:
                        startActivity(new Intent(getApplicationContext(), GraphsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.drinksBottomMenuItem:
                        startActivity(new Intent(getApplicationContext(), DrinkInputActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayResults(); //will display nothing if never entered data or most recent value of breathalyzer
        Bundle extras = getIntent().getExtras();
        boolean loggedin = false;
        if(extras != null)
        {
            loggedin = extras.getBoolean("Success");

        }
        if(firebaseHelper.ifUserLoggedIn() || loggedin){
            firebaseHelper.getCurrentUID();
            firebaseHelper.addProfileListener();
        }
        else
        {
            goToLogin();
        }

        // Checks if a user is logged in by checking current firebase user

    }

    // Don't allow back button to lead to login page (or anywhere)
    @Override
    public void onBackPressed() { }

    // Link Variables to Components in .XML file
    protected void initializeComponents() {
        newBreath = findViewById(R.id.newBreath);
        response = findViewById(R.id.response);
        CurrentDrinkTextView = findViewById(R.id.CurrentDrinktextView);
        newBreath.setOnClickListener(onClickBreathButton);
        toolbar = findViewById(R.id.toolbarHome);
        bottomNav = findViewById(R.id.bottomNavigation);
        //TimeStampTextview = findViewById(R.id.TimeStampTextView);
        specifyDrinkButton = findViewById(R.id.SpecifyDrink);
    }


    // Sets up the menu option bar to show profile and logout options
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    // Goes to edit mode if menu option selected
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.profileMenuItem:
                goToProfile();
                return true;
            case R.id.logoutMenuItem:
                firebaseHelper.logout();
                goToLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Display the Database
    @SuppressLint("SetTextI18n")
    public void displayResults () {
        breathalyzer_values = myDB.getAllResults();
        String drink = "";
        double temp = 0;
        String timeStamp = "";
        /*if(myDB.ReturnDrinkTypes().size() > 0) {
            drink = myDB.ReturnDrinkTypes().get(myDB.ReturnDrinkTypes().size() - 1).getDrinkName();
        }*/
        for (int i = 0; i < myDB.ReturnDrinkTypes().size(); i++) {
            if(firebaseHelper.getUser() != null)
            {
                if(myDB.ReturnDrinkTypes().get(i).getUID().equals(firebaseHelper.getCurrentUID()))
                {
                    drink = myDB.ReturnDrinkTypes().get(i).getDrinkName();
                }
            }
        }
        /*for (int i = 0; i < breathalyzer_values.size() - 1; i++) {
            if(breathalyzer_values.get(i).getUID().equals(firebaseHelper.getCurrentUID())) {
                temp = Double.parseDouble(breathalyzer_values.get(breathalyzer_values.size()-1).getResult());
                timeStamp = breathalyzer_values.get(breathalyzer_values.size() - 1).getTimeStamp();

                temp = (((temp - 150) / 1050)); //second value in numerator needs to be based on calibration
                temp = (temp<0) ? 0 : temp; //this is to avoid negative values and are now considered absolute zero for constraint purposes

            }
        }*/
        /*if(breathalyzer_values.size() > 0)
        {
            temp = Double.parseDouble(breathalyzer_values.get(breathalyzer_values.size()-1).getResult());
            timeStamp = breathalyzer_values.get(breathalyzer_values.size() - 1).getTimeStamp();

            temp = (((temp - 150) / 1050)); //second value in numerator needs to be based on calibration
            temp = (temp<0) ? 0 : temp; //this is to avoid negative values and are now considered absolute zero for constraint purposes
        }*/

        //response.setText("Your Blood Alcohol Level is: " + decimalFormat.format(temp) + "%");
        //TimeStampTextview.setText("Measurement Taken: " + timeStamp);
        CurrentDrinkTextView.setText("Last Drink: " + drink);
        Log.d("Changing", "Changing Display " + drink);
    }

    private final View.OnClickListener onClickBreathButton= new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            openLoading();
            // TODO: check all instances of type_of_drink and remove after drink input page is complete
        }
    };

    protected void goToDrinkInputActivity(){
        Intent i = new Intent(this, DrinkInputActivity.class);
        startActivity(i);
    }

    protected void openLoading(){        //open settings class on click

        Intent i = new Intent(this, LoadActivity.class);
        Profile profile = firebaseHelper.getProfile();
        i.putExtra("MAC", profile.getDeviceCode());
        startActivity(i);
    }

    protected void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    protected void goToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        Profile profile = firebaseHelper.getProfile();
        intent.putExtra("profile", profile.getUsername() + " " + profile.getDeviceCode() + " " + profile.getDeviceName() + " " + profile.getPassword());
        startActivity(intent);
    }


}


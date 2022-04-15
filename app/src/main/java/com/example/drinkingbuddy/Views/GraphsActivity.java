package com.example.drinkingbuddy.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Controllers.FirebaseHelper;
import com.example.drinkingbuddy.Models.Breathalyzer;
import com.example.drinkingbuddy.Models.Drink;
import com.example.drinkingbuddy.Models.Profile;
import com.example.drinkingbuddy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

//REFERENCE: https://medium.com/@leelaprasad4648/creating-linechart-using-mpandroidchart-33632324886d
// The code within each graph activity is heavily adapted from the reference above which makes use of
// MPAndroidChart library
// The library was pulled from the following github: https://github.com/PhilJay/MPAndroidChart

public class GraphsActivity extends AppCompatActivity {

    protected Button sensorResults;
    protected Button drinksConsumed;
    protected Button weeklyTrends;
    protected Toolbar toolbar;
    protected BottomNavigationView bottomNav;
    private DBHelper myDB;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);
        initializeComponents();
        setUpListeners();
        myDB = new DBHelper(this);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseHelper.ifUserLoggedIn()){
            firebaseHelper.addProfileListener();
        }
        else
        {
            goToLogin();
        }
    }

    protected void initializeComponents() {
        firebaseHelper = new FirebaseHelper(this);
        sensorResults = findViewById(R.id.sensorResultsButton);
        drinksConsumed = findViewById(R.id.drinksConsumedButton);
        weeklyTrends = findViewById(R.id.weeklyDrinksButton);
        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.graphsBottomMenuItem);
        toolbar = findViewById(R.id.toolbarGraphs);
    }

    protected void setUpListeners() {
        sensorResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLineGraph();
            }
        });
        drinksConsumed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPieChart();
            }
        });
        weeklyTrends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBarGraph();
            }
        });
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.graphsBottomMenuItem:
                        return true;
                    case R.id.homeBottomMenuItem:
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
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

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.profileMenuItem:
                goToProfile();
                return true;
            case R.id.logoutMenuItem:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void goToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        Profile profile = firebaseHelper.getProfile();
        intent.putExtra("profile", profile.getUsername() + " " + profile.getDeviceCode() + " " + profile.getDeviceName() + " " + profile.getPassword());
        startActivity(intent);
    }

    protected void logout() {
        firebaseHelper.logout();
        goToLogin();
    }

    protected void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    protected void goToLineGraph() {
        if(checkIfMeasurementsExist())
            startActivity(new Intent(this, LineGraphActivity.class));
        else
            Toast.makeText(getApplicationContext(), "Must have at least one measurement to see trends", Toast.LENGTH_LONG).show();
    }

    protected void goToPieChart() {
        if(checkIfDrinkValuesExist())
            startActivity(new Intent(this, PieChartActivity.class));
        else
            Toast.makeText(getApplicationContext(), "Must have at least one drink input to see trends", Toast.LENGTH_LONG).show();
    }

    protected void goToBarGraph() {
        if(checkIfDrinkValuesExist())
            startActivity(new Intent(this, BarGraphActivity.class));
        else
            Toast.makeText(getApplicationContext(), "Must have at least one drink input to see trends", Toast.LENGTH_LONG).show();
    }

    protected boolean checkIfDrinkValuesExist() {
        ArrayList<Drink> drinks = myDB.ReturnDrinkTypes();
        for (Drink drink :
                drinks) {
            if(drink.getUID().equals(firebaseHelper.getCurrentUID()))
            {
                return true;
            }
        }
        return false;
    }

    protected boolean checkIfMeasurementsExist() {
        List<Breathalyzer> measurements = myDB.getAllResults();
        for (Breathalyzer measurement :
                measurements) {
            if(measurement.getUID().equals(firebaseHelper.getCurrentUID()))
            {
                return true;
            }
        }
        return false;
    }
}
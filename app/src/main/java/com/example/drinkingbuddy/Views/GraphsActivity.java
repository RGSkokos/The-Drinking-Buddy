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
import com.example.drinkingbuddy.Controllers.SharedPreferencesHelper;
import com.example.drinkingbuddy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class GraphsActivity extends AppCompatActivity {

    protected Button sensorResults;
    protected Button drinksConsumed;
    protected Button weeklyTrends;
    protected Toolbar toolbar;
    protected BottomNavigationView bottomNav;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private DBHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);
        initializeComponents();
        setUpListeners();
        sharedPreferencesHelper = new SharedPreferencesHelper(GraphsActivity.this);
        myDB = new DBHelper(this);
        setSupportActionBar(toolbar);
    }

    protected void initializeComponents() {
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
        startActivity(intent);
    }

    protected void logout() {
        sharedPreferencesHelper.saveLoginId(0);
        goToLogin();
    }

    protected void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    protected void goToLineGraph() {
        if(myDB.getAllResults().size() > 0)
            startActivity(new Intent(this, LineGraphActivity.class));
        else
            Toast.makeText(getApplicationContext(), "Must have at least one measurement to see trends", Toast.LENGTH_LONG).show();
    }

    protected void goToPieChart() {
        if(myDB.getAllResults().size() > 0)
            startActivity(new Intent(this, PieChartActivity.class));
        else
            Toast.makeText(getApplicationContext(), "Must have at least one measurement to see trends", Toast.LENGTH_LONG).show();
    }

    protected void goToBarGraph() {
        if(myDB.getAllResults().size() > 0)
            startActivity(new Intent(this, BarGraphActivity.class));
        else
            Toast.makeText(getApplicationContext(), "Must have at least one measurement to see trends", Toast.LENGTH_LONG).show();
    }
}
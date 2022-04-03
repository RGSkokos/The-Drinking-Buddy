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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Controllers.FirebaseHelper;
import com.example.drinkingbuddy.Models.Profile;
import com.example.drinkingbuddy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DrinkInputActivity extends AppCompatActivity {

    protected EditText beerNumber;
    protected EditText wineNumber;
    protected EditText liquorNumber;
    protected EditText ciderNumber;
    protected ImageButton beerIncrement;
    protected ImageButton wineIncrement;
    protected ImageButton liquorIncrement;
    protected ImageButton ciderIncrement;
    protected ImageButton beerDecrement;
    protected ImageButton wineDecrement;
    protected ImageButton liquorDecrement;
    protected ImageButton ciderDecrement;
    protected Button saveDrinkButton;
    protected Toolbar toolbar;
    protected BottomNavigationView bottomNav;
    private DBHelper dbHelper;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_input);

        dbHelper = new DBHelper(this);
        initializeComponents();
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

    protected void initializeComponents(){
        firebaseHelper = new FirebaseHelper(this);
        beerNumber = findViewById(R.id.beerNumber);
        wineNumber = findViewById(R.id.wineNumber);
        liquorNumber = findViewById(R.id.liquorNumber);
        ciderNumber = findViewById(R.id.ciderNumber);
        saveDrinkButton = findViewById(R.id.saveDrinkButton);
        saveDrinkButton.setOnClickListener(saveDrinks);
        toolbar = findViewById(R.id.drinkInputToolbar);
        bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.drinksBottomMenuItem);
        bottomNav.setOnItemSelectedListener(navBarOnClick);

        beerIncrement = findViewById(R.id.beerIncrementButton);
        beerIncrement.setOnClickListener(incrementBeerCount);
        wineIncrement = findViewById(R.id.wineIncrementButton);
        wineIncrement.setOnClickListener(incrementWineCount);
        liquorIncrement = findViewById(R.id.liquorIncrementButton);
        liquorIncrement.setOnClickListener(incrementLiquorCount);
        ciderIncrement = findViewById(R.id.ciderIncrementButton);
        ciderIncrement.setOnClickListener(incrementCiderCount);

        beerDecrement = findViewById(R.id.beerDecrementButton);
        beerDecrement.setOnClickListener(decrementBeerCount);
        wineDecrement = findViewById(R.id.wineDecrementButton);
        wineDecrement.setOnClickListener(decrementWineCount);
        liquorDecrement = findViewById(R.id.liquorDecrementButton);
        liquorDecrement.setOnClickListener(decrementLiquorCount);
        ciderDecrement = findViewById(R.id.ciderDecrementButton);
        ciderDecrement.setOnClickListener(decrementCiderCount);

        // Set up the toolbar
        setSupportActionBar(toolbar);
    }

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
                firebaseHelper.logout();
                goToLogin();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final NavigationBarView.OnItemSelectedListener navBarOnClick = new NavigationBarView.OnItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId())
            {
                case R.id.drinksBottomMenuItem:
                    return true;
                case R.id.homeBottomMenuItem:
                    startActivity(new Intent(getApplicationContext(), HomePage.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.graphsBottomMenuItem:
                    startActivity(new Intent(getApplicationContext(), GraphsActivity.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        }
    };

    private final View.OnClickListener incrementBeerCount = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String beer = beerNumber.getText().toString();
            if (!"".equals(beer)) {
                if (Integer.parseInt(beer) < 999)
                    beerNumber.setText(String.valueOf(Integer.parseInt(beer) + 1));
                else
                    Toast.makeText(getApplicationContext(), "Max input reached", Toast.LENGTH_LONG).show();
            } else
                beerNumber.setText("1");
        }
    };

    private final View.OnClickListener incrementWineCount = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String wine = wineNumber.getText().toString();
            if (!"".equals(wine)) {
                if (Integer.parseInt(wine) < 999)
                    wineNumber.setText(String.valueOf(Integer.parseInt(wine) + 1));
                else
                    Toast.makeText(getApplicationContext(), "Max input reached", Toast.LENGTH_LONG).show();
            } else
                wineNumber.setText("1");
        }
    };

    private final View.OnClickListener incrementLiquorCount = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String liquor = liquorNumber.getText().toString();
            if(!"".equals(liquor)){
                if (Integer.parseInt(liquor) < 999)
                    liquorNumber.setText(String.valueOf(Integer.parseInt(liquor) + 1));
                else
                    Toast.makeText(getApplicationContext(), "Max input reached", Toast.LENGTH_LONG).show();
            } else
                liquorNumber.setText("1");
        }
    };

    private final View.OnClickListener incrementCiderCount = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String cider = ciderNumber.getText().toString();
            if(!"".equals(cider)){
                if (Integer.parseInt(cider) < 999)
                    ciderNumber.setText(String.valueOf(Integer.parseInt(cider) + 1));
                else
                    Toast.makeText(getApplicationContext(), "Max input reached", Toast.LENGTH_LONG).show();
            } else
                ciderNumber.setText("1");
        }
    };

    private final View.OnClickListener decrementBeerCount = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String beerTemp = beerNumber.getText().toString();
            int beer = 0;
            if(!"".equals(beerTemp)){
                beer = Integer.parseInt(beerTemp);
                if(beer >= 1){
                    beer--;
                }
            }
            else beer = 0;
            beerNumber.setText(String.valueOf(beer));
        }
    };

    private final View.OnClickListener decrementWineCount = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String wineTemp = wineNumber.getText().toString();
            int wine = 0;
            if(!"".equals(wineTemp)){
                wine = Integer.parseInt(wineTemp);
                if(wine >= 1){
                    wine--;
                }
            }
            else wine = 0;
            wineNumber.setText(String.valueOf(wine));
        }
    };

    private final View.OnClickListener decrementLiquorCount = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String liquorTemp = liquorNumber.getText().toString();
            int liquor = 0;
            if(!"".equals(liquorTemp)){
                liquor = Integer.parseInt(liquorTemp);
                if(liquor >= 1){
                    liquor--;
                }
            }
            else liquor = 0;
            liquorNumber.setText(String.valueOf(liquor));
        }
    };

    private final View.OnClickListener decrementCiderCount = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String ciderTemp = ciderNumber.getText().toString();
            int cider = 0;
            if(!"".equals(ciderTemp)){
                cider = Integer.parseInt(ciderTemp);
                if(cider >= 1){
                    cider--;
                }
            }
            else cider = 0;
            ciderNumber.setText(String.valueOf(cider));
        }
    };

    private final View.OnClickListener saveDrinks = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            saveDrinkButton.getBackground().setAlpha(128);

            int beer = 0;
            int wine = 0;
            int liquor = 0;
            int cider = 0;
            boolean storedInDB = false;

            String beerTemp = beerNumber.getText().toString();
            String wineTemp = wineNumber.getText().toString();
            String liquorTemp = liquorNumber.getText().toString();
            String ciderTemp = ciderNumber.getText().toString();

            if(!"".equals(beerTemp) && Integer.parseInt(beerTemp) != 0){
                beer = Integer.parseInt(beerTemp);
                dbHelper.saveDrinkType("beer", beer);
                storedInDB = true;
            }
            if(!"".equals(wineTemp) && Integer.parseInt(wineTemp) != 0){
                wine = Integer.parseInt(wineTemp);
                dbHelper.saveDrinkType("wine", wine);
                storedInDB = true;
            }
            if(!"".equals(liquorTemp) && Integer.parseInt(liquorTemp) != 0){
                liquor = Integer.parseInt(liquorTemp);
                dbHelper.saveDrinkType("liquor", liquor);
                storedInDB = true;
            }
            if(!"".equals(ciderTemp) && Integer.parseInt(ciderTemp) != 0){
                cider = Integer.parseInt(ciderTemp);
                dbHelper.saveDrinkType("cider", cider);
                storedInDB = true;
            }
            if (storedInDB) {
                Toast.makeText(getApplicationContext(), "Input Saved!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), HomePage.class));
            } else
                Toast.makeText(getApplicationContext(), "No input provided", Toast.LENGTH_LONG).show();
        }
    };

    protected void goToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        Profile profile = firebaseHelper.getProfile();
        intent.putExtra("profile", profile.getUsername() + " " + profile.getDeviceCode() + " " + profile.getDeviceName() + " " + profile.getPassword());
        startActivity(intent);
    }


    protected void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
package com.example.drinkingbuddy.Views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.drinkingbuddy.R;

public class DrinkInputActivity extends AppCompatActivity {

    protected EditText beerNumber;
    protected EditText wineNumber;
    protected EditText liquorNumber;
    protected EditText ciderNumber;
    protected Button saveDrinkButton;
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_input);
    }

    protected void initializeComponents(){
        beerNumber = findViewById(R.id.beerNumber);
        wineNumber = findViewById(R.id.wineNumber);
        liquorNumber = findViewById(R.id.liquorNumber);
        ciderNumber = findViewById(R.id.ciderNumber);
        saveDrinkButton = findViewById(R.id.saveDrinkButton);
        saveDrinkButton.setOnClickListener(saveDrinks);
        toolbar = findViewById(R.id.drinkInputToolbar);
        // Set up the toolbar
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private final View.OnClickListener saveDrinks = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };
}
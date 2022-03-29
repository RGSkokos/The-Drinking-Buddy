package com.example.drinkingbuddy.Views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.R;

public class DrinkInputActivity extends AppCompatActivity {

    protected TextView beerTextView;
    protected TextView wineTextView;
    protected TextView liquorTextView;
    protected TextView ciderTextView;
    protected EditText beerNumber;
    protected EditText wineNumber;
    protected EditText liquorNumber;
    protected EditText ciderNumber;
    protected Button beerIncrement;
    protected Button wineIncrement;
    protected Button liquorIncrement;
    protected Button ciderIncrement;
    protected Button beerDecrement;
    protected Button wineDecrement;
    protected Button liquorDecrement;
    protected Button ciderDecrement;
    protected Button saveDrinkButton;
    protected Toolbar toolbar;
    protected DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_input);

        dbHelper = new DBHelper(this);
        initializeComponents();
    }

    protected void initializeComponents(){
        // TODO: initialize all components
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

    // TODO: implement listeners and assign to buttons
//    private final View.OnClickListener incrementBeerCount = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//        }
//    }
//
//    private final View.OnClickListener incrementWineCount = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//        }
//    }
//
//    private final View.OnClickListener incrementLiquorCount = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//        }
//    }
//
//    private final View.OnClickListener incrementCiderCount = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//        }
//    }
//
//    private final View.OnClickListener decrementBeerCount = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//        }
//    }
//
//    private final View.OnClickListener decrementWineCount = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//        }
//    }
//
//    private final View.OnClickListener decrementLiquorCount = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//        }
//    }
//
//    private final View.OnClickListener decrementCiderCount = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//        }
//    }

    private final View.OnClickListener saveDrinks = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int beer = 0;
            int wine = 0;
            int liquor = 0;
            int cider = 0;

            String beerTemp = beerNumber.getText().toString();
            String wineTemp = wineNumber.getText().toString();
            String liquorTemp = liquorNumber.getText().toString();
            String ciderTemp = ciderNumber.getText().toString();

            if(!"".equals(beerTemp)){
                beer = Integer.parseInt(beerTemp);
                dbHelper.saveDrinkType("beer", beer);
            }
            if(!"".equals(wineTemp)){
                wine = Integer.parseInt(wineTemp);
                dbHelper.saveDrinkType("wine", wine);
            }
            if(!"".equals(liquorTemp)){
                liquor = Integer.parseInt(liquorTemp);
                dbHelper.saveDrinkType("liquor", liquor);
            }
            if(!"".equals(ciderTemp)){
                cider = Integer.parseInt(ciderTemp);
                dbHelper.saveDrinkType("cider", cider);
            }

        }
    };
}
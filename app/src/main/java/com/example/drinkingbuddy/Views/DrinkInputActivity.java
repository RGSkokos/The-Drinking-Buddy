package com.example.drinkingbuddy.Views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private final View.OnClickListener incrementBeerCount = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String beerTemp = beerNumber.getText().toString();
            int beer = 0;
            if(!"".equals(beerTemp)){
                beer = Integer.parseInt(beerTemp);
                beer++;
            }
            else beer = 1;
            beerNumber.setText(String.valueOf(beer));
        }
    };

    private final View.OnClickListener incrementWineCount = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String wineTemp = wineNumber.getText().toString();
            int wine = 0;
            if(!"".equals(wineTemp)){
                wine = Integer.parseInt(wineTemp);
                wine++;
            }
            else wine = 1;
            wineNumber.setText(String.valueOf(wine));
        }
    };

    private final View.OnClickListener incrementLiquorCount = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String liquorTemp = liquorNumber.getText().toString();
            int liquor = 0;
            if(!"".equals(liquorTemp)){
                liquor = Integer.parseInt(liquorTemp);
                liquor++;
            }
            else liquor = 1;
            liquorNumber.setText(String.valueOf(liquor));
        }
    };

    private final View.OnClickListener incrementCiderCount = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String ciderTemp = ciderNumber.getText().toString();
            int cider = 0;
            if(!"".equals(ciderTemp)){
                cider = Integer.parseInt(ciderTemp);
                cider++;
            }
            else cider = 1;
            ciderNumber.setText(String.valueOf(cider));
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

            Toast.makeText(getApplicationContext(), "Input Saved!", Toast.LENGTH_LONG).show();
        }
    };
}
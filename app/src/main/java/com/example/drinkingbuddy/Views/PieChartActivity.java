package com.example.drinkingbuddy.Views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Models.Breathalyzer;
import com.example.drinkingbuddy.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

//REFERENCE: https://medium.com/@leelaprasad4648/creating-linechart-using-mpandroidchart-33632324886d
// This code is heavily adapted from the reference above which makes use of MPAndroidChart library
// The library was pulled from the following github: https://github.com/PhilJay/MPAndroidChart
// Only the line graph was implemented thus far, the library files can be found within models

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PieChartActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    private DBHelper database;
    PieChart pieChart;
    List<Breathalyzer> breathalyzer_values;
    Map<String, Integer> DrinkType = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        initializeComponents();
    }

    @Override
    protected void onStart(){
        super.onStart();

        database = new DBHelper(this);
        breathalyzer_values = database.getAllResults();

        insertPieChartValues();
        displayPieChart();
    }

    protected void initializeComponents(){
        toolbar = findViewById(R.id.PieChartToolbar);
        // Set up the toolbar
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        pieChart = findViewById(R.id.pieGraph);
    }

    //region Pie Chart
    private void insertPieChartValues()
    {
        DBHelper db = new DBHelper(this);
        ArrayList<String> Drink_Types = db.ReturnDrinkTypes();

        int[] drinks = {0, 0 , 0, 0};

        for (String drink :
                Drink_Types) {
            switch (drink) {
                case "liquor":
                    drinks[0]++;
                    break;
                case "wine":
                    drinks[1]++;
                    break;
                case "beer":
                    drinks[2]++;
                    break;
                default:
                    drinks[3]++; //if other type of value (unknown)
                    break;
            }
        }

        DrinkType.put("Liquor",drinks[0]);
        DrinkType.put("Beer",drinks[2]);
        DrinkType.put("Wine",drinks[1]);
        DrinkType.put("Unknown", drinks[3]);
    }

    private void displayPieChart(){

        List<PieEntry> pieGraphValues = new ArrayList<>();
        //initializing colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.CYAN);

        //input data and fit data into pie chart entry
        for(String type: DrinkType.keySet()){
            pieGraphValues.add(new PieEntry(DrinkType.get(type), type));
        }

        PieDataSet pieDataSet = new PieDataSet(pieGraphValues, "");

        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.WHITE);

        PieData pieData = new PieData(pieDataSet);

        pieChart.getDescription().setEnabled(false);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
//endregion
}
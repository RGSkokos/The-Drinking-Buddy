package com.example.drinkingbuddy.Views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Models.Breathalyzer;
import com.example.drinkingbuddy.Models.Drink;
import com.example.drinkingbuddy.R;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

//REFERENCE: https://medium.com/@leelaprasad4648/creating-linechart-using-mpandroidchart-33632324886d
// This code is heavily adapted from the reference above which makes use of MPAndroidChart library
// The library was pulled from the following github: https://github.com/PhilJay/MPAndroidChart
// Only the line graph was implemented thus far, the library files can be found within models

import java.util.ArrayList;
import java.util.List;

public class BarGraphActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected Menu menu;
    private DBHelper database;
    protected BarChart barChart;
    protected ArrayList<Drink> drinks;
    protected ArrayList<BarEntry> barGraphValues = new ArrayList<>();
    protected TextView statsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_graph);

        initializeComponents();
    }

    @Override
    protected void onStart(){
        super.onStart();

        database = new DBHelper(this);
        drinks = database.ReturnDrinkTypes();


        insertBarChartValues();
        displayBarChart();
    }

    protected void initializeComponents(){

        toolbar = findViewById(R.id.BarGraphToolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        // Set up the toolbar
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        barChart = findViewById(R.id.barGraph);

        statsTextView = findViewById(R.id.statsTextView);
    }

    //region Bar Chart
    private void insertBarChartValues() {

        ArrayList<Double> valueList = new ArrayList<>();

        //input data
        double[] dayOfWeekCounter = {0, 0, 0, 0, 0, 0, 0, 0};
        for(int i = 0; i < drinks.size(); i++){
            String day = drinks.get(i).getDayOfWeek();
            if(day != null)
            {
                switch (day) {
                    case "Sunday":
                        dayOfWeekCounter[0] += drinks.get(i).getQuantity();
                        break;
                    case "Monday":
                        dayOfWeekCounter[1] += drinks.get(i).getQuantity();
                        break;
                    case "Tuesday":
                        dayOfWeekCounter[2] += drinks.get(i).getQuantity();
                        break;
                    case "Wednesday":
                        dayOfWeekCounter[3] += drinks.get(i).getQuantity();
                        break;
                    case "Thursday":
                        dayOfWeekCounter[4] += drinks.get(i).getQuantity();
                        break;
                    case "Friday":
                        dayOfWeekCounter[5] += drinks.get(i).getQuantity();
                        break;
                    case "Saturday":
                        dayOfWeekCounter[6] += drinks.get(i).getQuantity();
                        break;
                }
            }
        }

        for (int i = 0; i < 7; i++)
        {
            valueList.add(dayOfWeekCounter[i]);
        }


        //fit the data into a bar
        for (int i = 0; i < valueList.size(); i++) {
            BarEntry barEntry = new BarEntry(i, valueList.get(i).floatValue());
            barGraphValues.add(barEntry);
        }
    }

    private void displayBarChart(){
        BarDataSet barDataSet = new BarDataSet(barGraphValues, "# of samples");
        String[] xAxisLabels = new String[]{"Sun", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun"};
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));


        barDataSet.setBarBorderColor(Color.WHITE);
        barDataSet.setLabel("Day of Week");
        barDataSet.setBarShadowColor(Color.BLACK);
        barDataSet.setValueTextColor(Color.WHITE);
        BarData data = new BarData(barDataSet);
        barChart.setData(data);
        barChart.setFitBars(true);
        barChart.animateY(2000);
        barChart.animateX(2000);
        barChart.getDescription().setText("Number of drinks by day of week");
        barChart.getDescription().setTextColor(Color.WHITE);
        barChart.invalidate();

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        YAxis leftAxis = barChart.getAxisLeft();
        YAxis rightAxis = barChart.getAxisRight();
        leftAxis.setTextColor(Color.WHITE);
        rightAxis.setTextColor(Color.WHITE);

        Legend legend = barChart.getLegend();
        legend.setTextColor(Color.WHITE);
    }
//endregion
}
package com.example.drinkingbuddy.Views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Models.Breathalyzer;
import com.example.drinkingbuddy.R;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

//REFERENCE: https://medium.com/@leelaprasad4648/creating-linechart-using-mpandroidchart-33632324886d
// This code is heavily adapted from the reference above which makes use of MPAndroidChart library
// The library was pulled from the following github: https://github.com/PhilJay/MPAndroidChart
// Only the line graph was implemented thus far, the library files can be found within models

import java.util.ArrayList;
import java.util.List;

public class LineGraphActivity extends AppCompatActivity {

    //Variables
    protected LineChart lineChart;
    protected Toolbar toolbar;
    protected Menu menu;
    private DBHelper database;
    protected List<Breathalyzer> breathalyzerValues;
    protected ArrayList<Entry> lineGraphValues = new ArrayList<>(); //holds points in line graph
    protected String SpanOfData;
    protected ListView sensorReadingsListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        initializeComponents();
    }

    @Override
    protected void onStart(){
        super.onStart();

        database = new DBHelper(this);
        breathalyzerValues = database.getAllResults();

        insertLineChartValues();
        displayLineChart();
        loadListView();
    }

    protected void initializeComponents(){
        toolbar = findViewById(R.id.LineGraphToolbar);
        // Set up the toolbar
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        lineChart = findViewById(R.id.lineGraph);

        sensorReadingsListview = findViewById(R.id.sensorReadingsListview);
    }

    protected void loadListView(){
        //List<Breathalyzer> readings = database.getAllResults();

        ArrayList<String> readingsText = new ArrayList<>();

        for(Breathalyzer result: breathalyzerValues){
            String temp = "";
            temp += result.getTimeStamp() + ": " + result.getResult();

            readingsText.add(temp);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, readingsText);
        sensorReadingsListview.setAdapter(arrayAdapter);
    }

    //region Line Graph
    //Set up Line Graph
    protected void insertLineChartValues()
    {

        String startTime = ""; //first value time and date
        String EndTime = ""; //last value time and date

        //iterate through results from breathalyzer to set datapoint values
        //set start and end time for easy reference of how far the data spans
        if(breathalyzerValues.size() > 0) {
            for (int i = 0; i < breathalyzerValues.size(); i++) {
                if (i == (breathalyzerValues.size() - 1)) {
                    EndTime = breathalyzerValues.get(i).getTimeStamp();
                }
                if (i == 0) {
                    startTime = breathalyzerValues.get(i).getTimeStamp();
                }

                float temp = Float.parseFloat(breathalyzerValues.get(i).getResult());
                temp = (((temp - 150) / 1050)); //second value in numerator needs to be based on calibration
                temp = (temp < 0) ? 0 : temp; //this is to avoid negative values and are now considered absolute zero for constraint purposes

                lineGraphValues.add(new Entry((i + 1), temp));
            }

            SpanOfData = "from " + startTime + " to " + EndTime;
        }
    }

    protected void displayLineChart()
    {
        //Library methods make values easier to read
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.animateY(1000);
        lineChart.animateX(1000);
        lineChart.getDescription().setEnabled(false); //REFERENCE:https://stackoverflow.com/questions/27566916/how-to-remove-description-from-chart-in-mpandroidchart

        //Define dataset with entries
        LineDataSet overallConsumption = new LineDataSet(lineGraphValues, "Overall Consumption");
        overallConsumption.setAxisDependency(YAxis.AxisDependency.RIGHT);

        //Styling for overallConsumption
        overallConsumption.setColor(Color.RED);

        //Add to LineData object
        LineData lineData = new LineData(overallConsumption);
        lineChart.setData(lineData);
        lineChart.invalidate();

    }
//endregion
}
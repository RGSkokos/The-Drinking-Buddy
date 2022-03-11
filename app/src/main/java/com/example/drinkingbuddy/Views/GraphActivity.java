package com.example.drinkingbuddy.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Bundle;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Models.Breathalyzer;
import com.example.drinkingbuddy.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

//REFERENCE: https://medium.com/@leelaprasad4648/creating-linechart-using-mpandroidchart-33632324886d
// This code is heavily adapted from the reference above which makes use of MPAndroidChart library
// The library was pulled from the following github: https://github.com/PhilJay/MPAndroidChart
// Only the line graph was implemented thus far, the library files can be found within models

public class GraphActivity extends AppCompatActivity {

    //Global Variables
    LineChart MyChart;
    List<Breathalyzer> breathalyzer_values;
    ArrayList<Entry> vals = new ArrayList<>(); //holds points in line graph
    String SpanOfData;

    //-------------------------Activity Life Cycle--------------------------------------------------

   //onCreate only used to initialize components
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        initializeComponents();

    }

    @Override
    protected void onStart() {
        super.onStart();
        AddGraphValues();
        DisplayValues();

    }

    //---------------------------------Helper Methods-----------------------------------------------
    // Link Variables to Components in .XML file
    protected void initializeComponents() {
        MyChart = (LineChart) findViewById(R.id.reportingChart);
    }

    //Set up Line Graph
    protected void AddGraphValues()
    {
        //Library methods make values easier to read
        MyChart.setTouchEnabled(true);
        MyChart.setPinchZoom(true);

        //Open database and grab all values
        DBHelper db = new DBHelper(this);
        breathalyzer_values = db.getAllResults();




        String startTime = ""; //first value time and date
        String EndTime = ""; //last value time and date

        //iterate through results from breathalyzer to set datapoint values
        //set start and end time for easy reference of how far the data spans
        for (int i = 0; i < breathalyzer_values.size(); i++) {
            if(i == (breathalyzer_values.size()-1))
            {
                EndTime = breathalyzer_values.get(i).getTimeStamp();
            }
            if(i == 0)
            {
                startTime = breathalyzer_values.get(i).getTimeStamp();
            }


            vals.add(new Entry((i + 1), (float) Double.parseDouble(breathalyzer_values.get(i).getResult())));
        }

        SpanOfData = "from " + startTime + " to " + EndTime;

    }

    protected void DisplayValues()
    {
        //Displaying values in dataset
        LineDataSet set;
        if (MyChart.getData() != null && MyChart.getData().getDataSetCount() > 0)
        {

            set = (LineDataSet) MyChart.getData().getDataSetByIndex(0);
            set.setValues(vals);
            MyChart.getData().notifyDataChanged();
            MyChart.notifyDataSetChanged();
        }
        else {
            set = new LineDataSet(vals, SpanOfData);
            set.setColor(Color.RED);
            set.setCircleColor(Color.BLACK);
            set.setLineWidth(2f);
            set.setDrawFilled(true);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set);
            LineData data = new LineData(dataSets);
            MyChart.setData(data);
        }
    }



}
package com.example.drinkingbuddy.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuInflater;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Models.Breathalyzer;
import com.example.drinkingbuddy.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    BarChart chart;
    PieChart pieChart;


//region Activity Life Cycle
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
        insertLineGraphValues();
        displayLineGraph();
        showPieChart();
        showBarChart();

    }
//endregion

//region Pie Chart
    private Map insertPieGraphValues()
    {
        Map<String, Integer> DrinkType = new HashMap<>();
        DBHelper db = new DBHelper(this);
        ArrayList Drink_Types = db.ReturnDrinkTypes();

        int[] drinks = {0, 0 , 0, 0};

        for (int i = 0; i < Drink_Types.size(); i++) {
            if(Drink_Types.get(i).equals("liquor"))
            {
                drinks[0]++;
            } else if(Drink_Types.get(i).equals("wine"))
            {
                drinks[1]++;
            }else if(Drink_Types.get(i).equals("beer"))
            {
                drinks[2]++;
            }
            else
            {
                drinks[3]++; //if other type of value (unknown)
            }
        }

        DrinkType.put("Liquor",drinks[0]);
        DrinkType.put("Beer",drinks[2]);
        DrinkType.put("Wine",drinks[1]);
        DrinkType.put("Unknown", drinks[3]);
        return DrinkType;
    }

    private void showPieChart(){

        ArrayList<PieEntry> pieEntries = new ArrayList<>();


        //initializing data
        Map<String, Integer> DrinkType = insertPieGraphValues();

        //initializing colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.CYAN);

        //input data and fit data into pie chart entry
        for(String type: DrinkType.keySet()){
            pieEntries.add(new PieEntry(DrinkType.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.WHITE);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        pieChart.getDescription().setEnabled(false);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
//endregion

//region Bar Chart
    private void showBarChart(){
        ArrayList<Double> valueList = new ArrayList<Double>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        String title = "# of samples";

        //input data
        double[] dayOfWeekCounter = {0, 0, 0, 0, 0, 0, 0};
        for(int i = 0; i < breathalyzer_values.size(); i++){
            String day = breathalyzer_values.get(i).getDayOfWeek();
            if(day.equals("Monday"))
            {
                dayOfWeekCounter[0]++;
            }else if(day.equals("Tuesday"))
            {
                dayOfWeekCounter[1]++;
            }
            else if(day.equals("Wednesday"))
            {
                dayOfWeekCounter[2]++;
            }else if(day.equals("Thursday"))
            {
                dayOfWeekCounter[3]++;
            }else if(day.equals("Friday"))
            {
                dayOfWeekCounter[4]++;
            }else if(day.equals("Saturday"))
            {
                dayOfWeekCounter[5]++;
            }else if(day.equals("Sunday"))
            {
                dayOfWeekCounter[6]++;
            }
        }

        for (int i = 0; i < 7; i++)
        {
            valueList.add(dayOfWeekCounter[i]);
        }


        //fit the data into a bar
        for (int i = 1; i < valueList.size(); i++) {
            BarEntry barEntry = new BarEntry(i, valueList.get(i).floatValue());
            entries.add(barEntry);
        }

        BarDataSet barDataSet = new BarDataSet(entries, title);
        String[] xAxisLabels = new String[]{"", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun"};
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        BarData data = new BarData(barDataSet);
        chart.setData(data);
        chart.animateY(2000);
        chart.animateX(2000);
        chart.getDescription().setEnabled(false);
        chart.invalidate();
    }
//endregion

//region Helper Methods
    // Link Variables to Components in .XML file
    protected void initializeComponents() {
        chart = (BarChart) findViewById(R.id.chart);
        MyChart = (LineChart) findViewById(R.id.reportingChart);
        pieChart = findViewById(R.id.pieChart_view);

        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.GraphToolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }
//endregion

//region Line Graph
    //Set up Line Graph
    protected void insertLineGraphValues()
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
        if(breathalyzer_values.size() > 0) {
            for (int i = 0; i < breathalyzer_values.size(); i++) {
                if (i == (breathalyzer_values.size() - 1)) {
                    EndTime = breathalyzer_values.get(i).getTimeStamp();
                }
                if (i == 0) {
                    startTime = breathalyzer_values.get(i).getTimeStamp();
                }

                double temp = Double.parseDouble(breathalyzer_values.get(i).getResult());
                temp = (((temp - 1200) / 5000)); //second value in numerator needs to be based on calibration
                temp = (temp < 0) ? 0 : temp; //this is to avoid negative values and are now considered absolute zero for constraint purposes

                vals.add(new Entry((i + 1), (float) temp));
            }

            SpanOfData = "from " + startTime + " to " + EndTime;
        }
    }

    protected void displayLineGraph()
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
            MyChart.animateY(1000);
            MyChart.animateX(1000);
            MyChart.getDescription().setEnabled(false);
            //REFERENCE:https://stackoverflow.com/questions/27566916/how-to-remove-description-from-chart-in-mpandroidchart
        }
    }
//endregion


}
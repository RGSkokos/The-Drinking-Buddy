package com.example.drinkingbuddy.Views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.graphics.Color;
import android.os.Bundle;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Models.Breathalyzer;
import com.example.drinkingbuddy.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//REFERENCE: https://medium.com/@leelaprasad4648/creating-linechart-using-mpandroidchart-33632324886d
// This code is heavily adapted from the reference above which makes use of MPAndroidChart library
// The library was pulled from the following github: https://github.com/PhilJay/MPAndroidChart
// Only the line graph was implemented thus far, the library files can be found within models

public class GraphActivity extends AppCompatActivity {

    //Global Variables
    LineChart lineChart;
    BarChart barChart;
    PieChart pieChart;
    List<Breathalyzer> breathalyzer_values;
    ArrayList<Entry> lineGraphValues = new ArrayList<>(); //holds points in line graph
    ArrayList<BarEntry> barGraphValues = new ArrayList<>();
    Map<String, Integer> DrinkType = new HashMap<>();
    String SpanOfData;



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
        grabDataBase();
        insertLineChartValues();
        displayLineChart();
        insertPieChartValues();
        displayPieChart();
        insertBarChartValues();
        displayBarChart();

    }

//endregion

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

//region Bar Chart
    private void insertBarChartValues() {

        ArrayList<Double> valueList = new ArrayList<>();

        //input data
        double[] dayOfWeekCounter = {0, 0, 0, 0, 0, 0, 0, 0};
        for(int i = 0; i < breathalyzer_values.size(); i++){
            String day = breathalyzer_values.get(i).getDayOfWeek();
            if(day != null)
            {
                switch (day) {
                    case "Sunday":
                        dayOfWeekCounter[0]++;
                        break;
                    case "Monday":
                        dayOfWeekCounter[1]++;
                        break;
                    case "Tuesday":
                        dayOfWeekCounter[2]++;
                        break;
                    case "Wednesday":
                        dayOfWeekCounter[3]++;
                        break;
                    case "Thursday":
                        dayOfWeekCounter[4]++;
                        break;
                    case "Friday":
                        dayOfWeekCounter[5]++;
                        break;
                    case "Saturday":
                        dayOfWeekCounter[6]++;
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
        BarData data = new BarData(barDataSet);
        barChart.setData(data);
        barChart.animateY(2000);
        barChart.animateX(2000);
        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
    }
//endregion

//region Helper Methods
    // Link Variables to Components in .XML file
    protected void initializeComponents() {
        barChart = findViewById(R.id.BarChart);
        lineChart = findViewById(R.id.LineChart);
        pieChart = findViewById(R.id.PieChart);
        Toolbar toolbar = findViewById(R.id.GraphToolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
    }
//endregion

//region Line Graph
    //Set up Line Graph
    protected void insertLineChartValues()
    {

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

                float temp = Float.parseFloat(breathalyzer_values.get(i).getResult());
                temp = (((temp - 1500) / 5000)); //second value in numerator needs to be based on calibration
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

    private void grabDataBase()
    {
        //Open database and grab all values
        DBHelper db = new DBHelper(this);
        breathalyzer_values = db.getAllResults();
    }

}
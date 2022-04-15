package com.example.drinkingbuddy.Views;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Controllers.FirebaseHelper;
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
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;

// REFERENCE: https://medium.com/@leelaprasad4648/creating-linechart-using-mpandroidchart-33632324886d
// This code is heavily adapted from the reference above which makes use of MPAndroidChart library
// The library was pulled from the following github: https://github.com/PhilJay/MPAndroidChart
// statistical data taken from https://www.canada.ca/en/health-canada/services/substance-use/alcohol/low-risk-alcohol-drinking-guidelines.html

public class BarGraphActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected Menu menu;
    protected ArrayList<Drink> drinks;
    protected ArrayList<BarEntry> UserGraphValues = new ArrayList<>();
    protected ArrayList<BarEntry> WomenGraphValues = new ArrayList<>();
    protected ArrayList<BarEntry> MenGraphValues = new ArrayList<>();
    protected ArrayList<BarEntry> TypeOfDrinkGraphValues = new ArrayList<>();
    protected BarDataSet data;
    protected BarDataSet data2;
    protected BarDataSet data3;

    protected FirebaseHelper firebaseHelper;
    protected boolean gender;
    private BarChart barChart;
    private BarChart typeOfDrinkBarChart;
    private ArrayList<IBarDataSet> dataSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_graph);

        gender = true;

        initializeComponents();
    }

    @Override
    protected void onStart(){
        super.onStart();

        firebaseHelper = new FirebaseHelper(this);
        DBHelper database = new DBHelper(this);
        drinks = database.ReturnDrinkTypes();

        insertTypeOfDrinkBarChartValues();
        insertBarChartValues();
        displayBarCharts();
    }

    protected void initializeComponents(){
        typeOfDrinkBarChart = findViewById(R.id.barGraphTypeofDrink);
        barChart = findViewById(R.id.barGraphWeeklyConsumption);
        toolbar = findViewById(R.id.BarGraphToolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        // Set up the toolbar
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar_graph, menu);
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        this.menu = menu;
        return true;
    }

    //on switch of values to display, simply hide the one set and make the other visible
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.changeGender) {
            gender = !gender;
            if (gender) {
                dataSets.remove(data3);
                dataSets.add(data2);
                item.setTitle("Men Averages");
            } else {
                item.setTitle("Women Averages");
                dataSets.remove(data2);
                dataSets.add(data3);
            }
            BarData AllData = new BarData(dataSets);
            //Formatting based on source:https://www.codeplayon.com/2021/02/how-to-create-a-bar-chart-with-group-bar-with-mpandroidchart/
            float groupSpace = 0.4f;
            float barSpace = 0f;
            float barWidth = 0.3f;
            AllData.setBarWidth(barWidth);
            barChart.setData(AllData);
            barChart.groupBars(-0.5f, groupSpace, barSpace);
            barChart.setFitBars(true);
            barChart.invalidate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //region Pie Chart
    private void insertTypeOfDrinkBarChartValues() { //insertion of values in second bar chart (types of drinks)
        DBHelper db = new DBHelper(this);
        ArrayList<Drink> drinks = db.ReturnDrinkTypes();

        int[] drinkNumber = {0, 0 , 0, 0};

        for (Drink drink: drinks) {
            if(drink.getUID().equals(firebaseHelper.getCurrentUID())) {
                switch (drink.getDrinkName()) {
                    case "liquor":
                        drinkNumber[0] += drink.getQuantity();
                        break;
                    case "wine":
                        drinkNumber[1] += drink.getQuantity();
                        break;
                    case "beer":
                        drinkNumber[2] += drink.getQuantity();
                        break;
                    default:
                        drinkNumber[3] += drink.getQuantity();
                        break;
                }
            }
        }

        ArrayList<BarEntry> currentEntries = new ArrayList<>();
        BarEntry barEntryAverage = new BarEntry(0, drinkNumber[0]);
        currentEntries.add(barEntryAverage);
        barEntryAverage = new BarEntry(1, drinkNumber[1]);
        currentEntries.add(barEntryAverage);
        barEntryAverage = new BarEntry(2, drinkNumber[2]);
        currentEntries.add(barEntryAverage);
        barEntryAverage = new BarEntry(3, drinkNumber[3]);
        currentEntries.add(barEntryAverage);
        TypeOfDrinkGraphValues = currentEntries;
    }

    //region Bar Chart
    private void insertBarChartValues() {
        ArrayList<Double> valueList = new ArrayList<>();

        //input data for days of the week from user
        double[] dayOfWeekCounter = {0, 0, 0, 0, 0, 0, 0, 0};
        for(int i = 0; i < drinks.size(); i++){
            if(drinks.get(i).getUID().equals(firebaseHelper.getCurrentUID())) {
                String day = drinks.get(i).getDayOfWeek();
                if (day != null) {
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
        }

        //Arraylist to populate entries in national average values
        ArrayList<BarEntry> currentEntries = new ArrayList<>();
        //populate entries for women national averages
            BarEntry barEntryAverage = new BarEntry(0, 1);
            currentEntries.add(barEntryAverage);
            barEntryAverage = new BarEntry(1, 1);
            currentEntries.add(barEntryAverage);
            barEntryAverage = new BarEntry(2, 1);
            currentEntries.add(barEntryAverage);
            barEntryAverage = new BarEntry(3, 1);
            currentEntries.add(barEntryAverage);
            barEntryAverage = new BarEntry(4, 2);
            currentEntries.add(barEntryAverage);
            barEntryAverage = new BarEntry(5, 3);
            currentEntries.add(barEntryAverage);
            barEntryAverage = new BarEntry(6, 1);
            currentEntries.add(barEntryAverage);
            WomenGraphValues = currentEntries;

            //populate values for men national averages
            currentEntries = new ArrayList<>();
            barEntryAverage = new BarEntry(0, 1);
            currentEntries.add(barEntryAverage);
            barEntryAverage = new BarEntry(1, 1);
            currentEntries.add(barEntryAverage);
            barEntryAverage = new BarEntry(2, 2);
            currentEntries.add(barEntryAverage);
            barEntryAverage = new BarEntry(3, 2);
            currentEntries.add(barEntryAverage);
            barEntryAverage = new BarEntry(4, 3);
            currentEntries.add(barEntryAverage);
            barEntryAverage = new BarEntry(5, 4);
            currentEntries.add(barEntryAverage);
            barEntryAverage = new BarEntry(6, 2);
            currentEntries.add(barEntryAverage);
            MenGraphValues = currentEntries;

        //input all values for user entries
        for (int i = 0; i < 7; i++) {
            valueList.add(dayOfWeekCounter[i]);
        }

        //fit the data into a bar
        for (int i = 0; i < valueList.size(); i++) {
            BarEntry barEntry = new BarEntry(i, valueList.get(i).floatValue());
            UserGraphValues.add(barEntry);
        }
    }

    private void displayBarCharts(){
        //the following code was manually inputted through reference of the library functions that were available
        //references for all these functions can be found on the MP Android Chart webpage and the references listed in documentation
        //3 sets of data are attached to one bar graph and a single set on the second bar graph

        BarDataSet barDataSet = new BarDataSet(UserGraphValues, "# of samples");
        String[] xAxisLabels = new String[]{"Sun", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun"};
        XAxis xAxisWeekly = barChart.getXAxis();
        xAxisWeekly.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxisWeekly.setGranularity(1f);
        xAxisWeekly.setGranularityEnabled(true);


        //Type of drink bar chart formatting
        xAxisLabels = new String[]{"Liquor", "Wine", "Beer", "Cider"};
        XAxis xAxisTypeOfDrink = typeOfDrinkBarChart.getXAxis();
        xAxisTypeOfDrink.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxisTypeOfDrink.setGranularity(1f);
        xAxisTypeOfDrink.setGranularityEnabled(true);
        typeOfDrinkBarChart.getXAxis().setTextColor(Color.WHITE);
        typeOfDrinkBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        typeOfDrinkBarChart.setFitBars(true);
        typeOfDrinkBarChart.getAxisLeft().setTextColor(Color.WHITE);
        typeOfDrinkBarChart.getAxisRight().setTextColor(Color.WHITE);
        BarDataSet typeOfDrinkDataSet = new BarDataSet(TypeOfDrinkGraphValues, "Type of Drink");
        typeOfDrinkDataSet.setValueTextColor(Color.WHITE);
        BarData typeofDrinkData = new BarData(typeOfDrinkDataSet);
        typeOfDrinkDataSet.setColor(Color.WHITE);
        typeofDrinkData.setBarWidth(0.5f);
        typeOfDrinkBarChart.setData(typeofDrinkData);
        typeOfDrinkBarChart.getDescription().setEnabled(false);
        typeOfDrinkBarChart.invalidate();

        barDataSet.setBarBorderColor(Color.WHITE);
        barDataSet.setLabel("Day of Week");
        barDataSet.setValueTextColor(Color.WHITE);
        //User entered values
        data = new BarDataSet(UserGraphValues, "User Values");
        data.setValueTextColor(Color.WHITE);
        //Women Averages
        data2 = new BarDataSet(WomenGraphValues, "Women National Average");
        data2.setValueTextColor(Color.WHITE);
        data2.setColor(Color.RED);
        //Men Averages
        data3 = new BarDataSet(MenGraphValues, "Men National Average");
        data3.setValueTextColor(Color.WHITE);
        data3.setColor(Color.RED);
        //Set up initial data to be shown
        dataSets = new ArrayList<>();
        dataSets.add(data);
        dataSets.add(data2);
        BarData AllData = new BarData(dataSets);

        //Formatting based on source:https://www.codeplayon.com/2021/02/how-to-create-a-bar-chart-with-group-bar-with-mpandroidchart/
        float groupSpace = 0.4f;
        float barSpace = 0f;
        float barWidth = 0.3f;
        AllData.setBarWidth(barWidth);
        barChart.setData(AllData);
        barChart.groupBars(-0.5f, groupSpace, barSpace);
        barChart.setFitBars(true);
        barChart.animateY(2000);
        barChart.animateX(2000);
        barChart.getDescription().setEnabled(false);
        barChart.getDescription().setTextColor(Color.WHITE);
        barChart.invalidate();

        //format bar chart to be visible with black background
        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        YAxis leftAxis = barChart.getAxisLeft();
        YAxis rightAxis = barChart.getAxisRight();
        leftAxis.setTextColor(Color.WHITE);
        rightAxis.setTextColor(Color.WHITE);

        //set color of legends
        Legend secondLegend = barChart.getLegend();
        secondLegend.setTextColor(Color.WHITE);
        Legend legend = typeOfDrinkBarChart.getLegend();
        legend.setTextColor(Color.WHITE);
    }
//endregion
}
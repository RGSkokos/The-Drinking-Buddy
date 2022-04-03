package com.example.drinkingbuddy.Models;

public class Config {

    public static final String DATABASE_NAME = "BreathalyzerResult";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME_SENSOR = "ResultsTable";
    public static final String SENSOR_RESULT = "ResultOfMeasurement";
    public static final String TIME_STAMP_SENSOR = "TimeStampSensor";

    public static final String DAY_OF_WEEK = "dayOfWeek";
    public static final String DAY_OF_WEEK_DRINK = "dayOfWeek";

    public static final String TABLE_NAME_DRINK_TYPE = "drinkTypeTable";
    public static final String DRINK_ID = "drinkId";
    public static final String TIME_STAMP_DRINK = "TimeStampDrink";
    public static final String TYPE_OF_DRINK = "typeOfDrink";
    public static final String DRINK_QUANTITY = "drinkQuantity";
}

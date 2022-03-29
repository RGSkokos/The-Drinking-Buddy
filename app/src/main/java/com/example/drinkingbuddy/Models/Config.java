package com.example.drinkingbuddy.Models;

public class Config {

    public static final String DATABASE_NAME = "BreathalyzerResult";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME_SENSOR = "ResultsTable";
    public static final String SENSOR_RESULT = "_ResultOfMeasurement";
    public static final String TIME_STAMP_SENSOR = "TimeStampSensor";

    public static final String TABLE_NAME_PROFILE = "profileTable";
    public static final String PROFILE_ID = "_profileId";
    public static final String USERNAME = "profileUser";
    public static final String PASSWORD = "profilePassword";
    public static final String DEVICE_NAME = "profileDevice";
    public static final String DEVICE_CODE = "profileCode";
    public static final String DAY_OF_WEEK = "dayOfWeek";

    public static final String TABLE_NAME_DRINK_TYPE = "drinkTypeTable";
    public static final String DRINK_ID = "drinkId";
    public static final String TIME_STAMP_DRINK = "TimeStampDrink";
    public static final String TYPE_OF_DRINK = "typeOfDrink";
    public static final String DRINK_QUANTITY = "drinkQuantity";
}

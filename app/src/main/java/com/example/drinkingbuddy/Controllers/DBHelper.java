package com.example.drinkingbuddy.Controllers;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;
import android.widget.Toast;

import com.example.drinkingbuddy.Models.Breathalyzer;
import com.example.drinkingbuddy.Models.Config;
import com.example.drinkingbuddy.Models.Drink;
import com.example.drinkingbuddy.Models.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private final Context context;
    String CREATE_TABLE_TYPE_OF_DRINK;
    String CREATE_TABLE_RESULTS;
    SQLiteDatabase db;


    public DBHelper(Context context) {
        super(context, Config.DATABASE_NAME, null, Config.DATABASE_VERSION );
        this.context = context;
        db = getWritableDatabase();
    }

    //simply creates database to hold breathalyzer entries
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {// might need id autoincrement (?)
       CREATE_TABLE_RESULTS = "CREATE TABLE " + Config.TABLE_NAME_SENSOR
                + " (" + Config.SENSOR_RESULT + " TEXT NOT NULL,"
                + Config.TIME_STAMP_SENSOR + " TEXT NOT NULL,"
                + Config.DAY_OF_WEEK + " TEXT NOT NULL,"
                + Config.USER_UID + " TEXT NOT NULL)";


       CREATE_TABLE_TYPE_OF_DRINK = "CREATE TABLE " + Config.TABLE_NAME_DRINK_TYPE
               + " (" + Config.DRINK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
               + Config.TIME_STAMP_DRINK + " TEXT NOT NULL,"
               + Config.TYPE_OF_DRINK + " TEXT NOT NULL,"
               + Config.DRINK_QUANTITY + " INTEGER NOT NULL,"
               + Config.USER_UID + " TEXT NOT NULL,"
               + Config.DAY_OF_WEEK_DRINK + " TEXT NOT NULL)";

        Log.d(TAG, "db created");

        sqLiteDatabase.execSQL(CREATE_TABLE_RESULTS);
        sqLiteDatabase.execSQL(CREATE_TABLE_TYPE_OF_DRINK);
    }

    //not currently needed but can be implemented in the future
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public String DayOfWeek()
    {
        @SuppressLint("SimpleDateFormat") String dayOfWeek = new SimpleDateFormat("EEEE").format(new Date());
        Log.d(TAG, dayOfWeek);
        return dayOfWeek;
    }

    @SuppressLint("SimpleDateFormat")
    public String TimeStamp()
    {
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
        return new SimpleDateFormat("hh:mm MM/dd/yyyy").format(new Date());
        //REFERENCE: https://howtodoinjava.com/java/date-time/convert-date-time-to-est-est5edt/
    }

    //Method to insert a new entry for result from breathalyzer
    public void insertNewResult(String result, String UID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Config.SENSOR_RESULT, result);
        contentValues.put(Config.TIME_STAMP_SENSOR, TimeStamp());
        contentValues.put(Config.DAY_OF_WEEK, DayOfWeek());
        contentValues.put(Config.USER_UID, UID);

        try{
            db.insertOrThrow(Config.TABLE_NAME_SENSOR, null, contentValues);
            Log.d(TAG, "db value added");
        } catch(SQLException e)
        {
            Log.d(TAG, "EXCEPTION: " + e);
            Toast.makeText(context, "Operation Failed!: " + e, Toast.LENGTH_LONG).show();
        }
        finally {
            db.close();
        }
    }

    public List<Breathalyzer> getAllResults() {
        List<Breathalyzer> breathalyzer_values = new ArrayList<>();
        SQLiteDatabase userDatabase = this.getReadableDatabase();

        Cursor userTableCursor = userDatabase.query(Config.TABLE_NAME_SENSOR, null, null, null, null, null, null);
        if(userTableCursor != null) {
            if(userTableCursor.moveToFirst()) {
                do {
                    String bloodAlcohol = userTableCursor.getString(userTableCursor.getColumnIndexOrThrow(Config.SENSOR_RESULT));
                    String timeStamp = userTableCursor.getString(userTableCursor.getColumnIndexOrThrow(Config.TIME_STAMP_SENSOR));
                    String dayOfWeek = userTableCursor.getString(userTableCursor.getColumnIndexOrThrow(Config.DAY_OF_WEEK));
                    String UID = userTableCursor.getString(userTableCursor.getColumnIndexOrThrow(Config.USER_UID));
                    breathalyzer_values.add(new Breathalyzer(bloodAlcohol, String.valueOf(timeStamp), dayOfWeek, UID));

                } while(userTableCursor.moveToNext());
            }
        }
        assert userTableCursor != null;
        userTableCursor.close();
        return breathalyzer_values;
    }

    public void saveDrinkType(String typeOfDrink, int quantity, String UID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Config.TIME_STAMP_DRINK, TimeStamp());
        contentValues.put(Config.TYPE_OF_DRINK, typeOfDrink);
        contentValues.put(Config.DRINK_QUANTITY, quantity);
        contentValues.put(Config.DAY_OF_WEEK_DRINK, DayOfWeek());
        contentValues.put(Config.USER_UID, UID);

        try {
            db.insertOrThrow(Config.TABLE_NAME_DRINK_TYPE, null, contentValues);
            Log.d(TAG, typeOfDrink);
        } catch (SQLException e)
        {
            Log.d(TAG, "EXCEPTION: " + e);
            Toast.makeText(context, "Operation Failed!: " + e, Toast.LENGTH_LONG).show();
        }
        finally {
            db.close();
        }
    }


    public ArrayList<Drink> ReturnDrinkTypes(){
        ArrayList<Drink> drink_types = new ArrayList<>();
        SQLiteDatabase userDatabase = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor userTableCursor = userDatabase.query(Config.TABLE_NAME_DRINK_TYPE, null, null, null, null, null, null);
        if(userTableCursor != null) {
            if(userTableCursor.moveToFirst()) {
                do {
                    String drink_type = userTableCursor.getString(userTableCursor.getColumnIndexOrThrow(Config.TYPE_OF_DRINK));
                    Integer quantity = userTableCursor.getInt(userTableCursor.getColumnIndexOrThrow(Config.DRINK_QUANTITY));
                    String timestamp = userTableCursor.getString(userTableCursor.getColumnIndexOrThrow(Config.TIME_STAMP_DRINK));
                    String dayOfWeek = userTableCursor.getString(userTableCursor.getColumnIndexOrThrow(Config.DAY_OF_WEEK_DRINK));
                    String UID = userTableCursor.getString(userTableCursor.getColumnIndexOrThrow(Config.USER_UID));
                    drink_types.add(new Drink(drink_type, quantity, timestamp, dayOfWeek, UID));

                } while(userTableCursor.moveToNext());
            }
        }

        return drink_types;
    }
}

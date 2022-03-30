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
    String CREATE_TABLE_PROFILE;
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
                + Config.DAY_OF_WEEK + " TEXT NOT NULL)";

       CREATE_TABLE_PROFILE = "CREATE TABLE " + Config.TABLE_NAME_PROFILE
                + " (" + Config.PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.USERNAME + " TEXT NOT NULL,"
                + Config.PASSWORD + " TEXT NOT NULL,"
                + Config.DEVICE_NAME + " TEXT NOT NULL,"
                + Config.DEVICE_CODE + " TEXT NOT NULL)";

       CREATE_TABLE_TYPE_OF_DRINK = "CREATE TABLE " + Config.TABLE_NAME_DRINK_TYPE
               + " (" + Config.DRINK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
               + Config.TIME_STAMP_DRINK + " TEXT NOT NULL,"
               + Config.TYPE_OF_DRINK + " TEXT NOT NULL,"
               + Config.DRINK_QUANTITY + " INTEGER NOT NULL,"
               + Config.DAY_OF_WEEK + " TEXT NOT NULL)";

        Log.d(TAG, "db created");

        sqLiteDatabase.execSQL(CREATE_TABLE_RESULTS);
        sqLiteDatabase.execSQL(CREATE_TABLE_PROFILE);
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

    //Method to add new Profile
    public void insertNewProfile(Profile profile)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.USERNAME, profile.getUsername());
        contentValues.put(Config.PASSWORD, profile.getPassword());
        contentValues.put(Config.DEVICE_NAME, profile.getDeviceName());
        contentValues.put(Config.DEVICE_CODE, profile.getDeviceCode());

        try {
            db.insertOrThrow(Config.TABLE_NAME_PROFILE, null, contentValues);
            Log.d(TAG, "User Profile Added");
        }catch (SQLException e)
        {
            Log.d(TAG, "EXCEPTION" + e);
            Toast.makeText(context, "Operation Failed!: " + e, Toast.LENGTH_LONG).show();
        }
        finally {
            db.close();
        }
    }

    //Method to update a profile
    //REFERENCE: https://www.youtube.com/watch?v=pFktQj69SbU
    public  boolean update(Profile profile, int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.USERNAME, profile.getUsername());
        contentValues.put(Config.PASSWORD, profile.getPassword());
        contentValues.put(Config.DEVICE_NAME, profile.getDeviceName());
        contentValues.put(Config.DEVICE_CODE, profile.getDeviceCode());
        db.update(Config.TABLE_NAME_PROFILE, contentValues, Config.PROFILE_ID + "=?", new String[] {String.valueOf(id)});
        return true;
    }

    //Method to insert a new entry for result from breathalyzer
    public void insertNewResult(String result)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Config.SENSOR_RESULT, result);
        contentValues.put(Config.TIME_STAMP_SENSOR, TimeStamp());
        contentValues.put(Config.DAY_OF_WEEK, DayOfWeek());

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

    public void saveDrinkType(String typeOfDrink, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Config.TIME_STAMP_DRINK, TimeStamp());
        contentValues.put(Config.TYPE_OF_DRINK, typeOfDrink);
        contentValues.put(Config.DRINK_QUANTITY, quantity);
        contentValues.put(Config.DAY_OF_WEEK, DayOfWeek());

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

    //Check if a username and password pair exists
    public int checkProfile(String user, String pass)
    {
        SQLiteDatabase profileDatabase = this.getReadableDatabase();
        Cursor Cursor = null;

        try{
            Cursor = profileDatabase.query(Config.TABLE_NAME_PROFILE, null, null, null, null, null, null);

            if(Cursor != null)
            {
                if(Cursor.moveToFirst())
                {
                    do{
                        String Username = Cursor.getString(Cursor.getColumnIndexOrThrow(Config.USERNAME));
                        String Password = Cursor.getString(Cursor.getColumnIndexOrThrow(Config.PASSWORD));
                        Log.d(TAG, "User " + Username + "Password " + Password);
                        if(user.equals(Username) && pass.equals(Password)) //if the user and password in database matches passed parameters
                        {
                            return Cursor.getInt(Cursor.getColumnIndexOrThrow(Config.PROFILE_ID));
                        }
                    } while(Cursor.moveToNext());
                }
            }
        }catch (SQLException e)
        {
            Log.d(TAG, "EXCEPTION: " + e);
            Toast.makeText(context, "Operation Failed", Toast.LENGTH_LONG).show();
        }
        finally {
            if(Cursor != null)
            {
                Cursor.close();
                profileDatabase.close();
            }
        }

        return 0; //if username and password not found
    }

    @SuppressLint("Range")
    public Profile getProfileById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(Config.TABLE_NAME_PROFILE, null, Config.PROFILE_ID + "= ?", new String[]{Integer.toString(id)}, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String username = cursor.getString(cursor.getColumnIndex(Config.USERNAME));
                    String password = cursor.getString(cursor.getColumnIndex(Config.PASSWORD));
                    String deviceName = cursor.getString(cursor.getColumnIndex(Config.DEVICE_NAME));
                    String deviceCode = cursor.getString(cursor.getColumnIndex(Config.DEVICE_CODE));

                    return new Profile(username, password, deviceName, deviceCode);
                }
            }
        } catch (SQLiteException e){
            Log.d(TAG, "EXCEPTION: " + e);
            Toast.makeText(context, "Operation Failed: " + e, Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    @SuppressLint("Range")
    public String getDeviceCode(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(Config.TABLE_NAME_PROFILE, null, Config.PROFILE_ID + "= ?", new String[]{Integer.toString(id)}, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {

                    return cursor.getString(cursor.getColumnIndex(Config.DEVICE_CODE));
                }
            }
        } catch (SQLiteException e){
            Log.d(TAG, "EXCEPTION: " + e);
            Toast.makeText(context, "Operation Failed: " + e, Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
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
                    breathalyzer_values.add(new Breathalyzer(bloodAlcohol, String.valueOf(timeStamp), dayOfWeek));

                } while(userTableCursor.moveToNext());
            }
        }
        assert userTableCursor != null;
        userTableCursor.close();
        return breathalyzer_values;
    }

    //check if specific variable exists
    //first parameter is actual value while second specifies if it is a username, password, etc.
    public boolean checkIfValExists(String Val, String TypeofValue) {
        SQLiteDatabase profileDatabase = this.getReadableDatabase();
        Cursor Cursor = null;

        try{

            Cursor = profileDatabase.query(Config.TABLE_NAME_PROFILE, null, null, null, null, null, null);

            if(Cursor != null)
            {
                if(Cursor.moveToFirst())
                {
                    String valueFound = "";
                    do{
                        //based on type of value being looked at, grab the variable for the current profile
                        if(TypeofValue == "username")
                        {
                            valueFound = Cursor.getString(Cursor.getColumnIndexOrThrow(Config.USERNAME));
                        }
                        else if(TypeofValue == "password")
                        {
                            valueFound = Cursor.getString(Cursor.getColumnIndexOrThrow(Config.PASSWORD));
                        }
                        else if(TypeofValue == "device_name")
                        {
                            valueFound = Cursor.getString(Cursor.getColumnIndexOrThrow(Config.DEVICE_NAME));
                        }else if(TypeofValue == "device_code")
                        {
                            valueFound = Cursor.getString(Cursor.getColumnIndexOrThrow(Config.DEVICE_CODE));
                        }

                        if(Val.equals(valueFound)) //if this is equivalent to what is being searched for
                        {
                            return true;
                        }
                    } while(Cursor.moveToNext());
                }
            }
        }catch (SQLException e)
        {
            Log.d(TAG, "EXCEPTION: " + e);
            Toast.makeText(context, "Operation Failed", Toast.LENGTH_LONG).show();
        }
        finally {
            if(Cursor != null)
            {
                Cursor.close();
                profileDatabase.close();
            }
        }

        return false;
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
                    drink_types.add(new Drink(drink_type, quantity));

                } while(userTableCursor.moveToNext());
            }
        }

        return drink_types;
    }
}

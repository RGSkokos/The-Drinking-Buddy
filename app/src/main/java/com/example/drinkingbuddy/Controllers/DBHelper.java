package com.example.drinkingbuddy.Controllers;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;
import android.widget.Toast;

import com.example.drinkingbuddy.Models.Breathalyzer;
import com.example.drinkingbuddy.Models.Profile;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    private final Context context;

    public DBHelper(Context context)
    {
        super(context, Config.DATABASE_NAME, null, Config.DATABASE_VERSION );
        this.context = context;
    }


    //simply creates database to hold breathalyzer entries
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE_RESULTS = "CREATE TABLE " + Config.TABLE_NAME
                + " (" + Config.Result + " TEXT NOT NULL,"
                +  Config.TimeStamp + " TEXT NOT NULL,"
                + Config.DAY_OF_WEEK + " TEXT NOT NULL)";

        String CREATE_TABLE_PROFILE = "CREATE TABLE " + Config.TABLE_NAME_PROFILE
                + " (" + Config.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.USERNAME + " TEXT NOT NULL,"
                + Config.PASSWORD + " TEXT NOT NULL,"
                + Config.DEVICE_NAME + " TEXT NOT NULL,"
                + Config.DEVICE_CODE + " TEXT NOT NULL)";

        String CREATE_TABLE_TYPE_OF_DRINK = "CREATE TABLE " + Config.TABLE_NAME_DRINK_TYPE
                + " (" + Config.TYPE_OF_DRINK + " TEXT NOT NULL)";

        Log.d(TAG, "db created");

        sqLiteDatabase.execSQL(CREATE_TABLE_RESULTS);
        sqLiteDatabase.execSQL(CREATE_TABLE_PROFILE);
        sqLiteDatabase.execSQL(CREATE_TABLE_TYPE_OF_DRINK);
    }

    @SuppressLint("SimpleDateFormat")
    public String TimeStamp()
    {
        return new SimpleDateFormat("hh:mm MM/dd/yyyy").format(new Date());
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

    //Method to insert a new entry for result from breathalyzer
    public void insertNewResult(String result)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String currentStamp = TimeStamp();
        String dayOfWeek = DayOfWeek();
        contentValues.put(Config.Result, result);
        contentValues.put(Config.TimeStamp, currentStamp);
        contentValues.put(Config.DAY_OF_WEEK, dayOfWeek);

        try{
            db.insertOrThrow(Config.TABLE_NAME, null, contentValues);
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

    public void SaveDrinkType(String type_of_drink) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Config.TYPE_OF_DRINK, type_of_drink);

        try {
            db.insertOrThrow(Config.TABLE_NAME_DRINK_TYPE, null, contentValues);
            Log.d(TAG, type_of_drink);


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
                            return Cursor.getInt(Cursor.getColumnIndexOrThrow(Config.ID));
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
            cursor = db.query(Config.TABLE_NAME_PROFILE, null, Config.ID + "= ?", new String[]{Integer.toString(id)}, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String username = cursor.getString(cursor.getColumnIndex(Config.USERNAME));
                    String deviceName = cursor.getString(cursor.getColumnIndex(Config.DEVICE_NAME));
                    String deviceCode = cursor.getString(cursor.getColumnIndex(Config.DEVICE_CODE));

                    return new Profile(username, deviceName, deviceCode);
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
            cursor = db.query(Config.TABLE_NAME_PROFILE, null, Config.ID + "= ?", new String[]{Integer.toString(id)}, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String deviceCode = cursor.getString(cursor.getColumnIndex(Config.DEVICE_CODE));

                    return deviceCode;
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

        Cursor userTableCursor = userDatabase.query(Config.TABLE_NAME, null, null, null, null, null, null);
        if(userTableCursor != null) {
            if(userTableCursor.moveToFirst()) {
                do {
                    String bloodAlcohol = userTableCursor.getString(userTableCursor.getColumnIndexOrThrow(Config.Result));
                    String timeStamp = userTableCursor.getString(userTableCursor.getColumnIndexOrThrow(Config.TimeStamp));
                    breathalyzer_values.add(new Breathalyzer(bloodAlcohol, String.valueOf(timeStamp)));

                } while(userTableCursor.moveToNext());
            }
        }
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
                        switch (TypeofValue) {
                            case "username":
                                valueFound = Cursor.getString(Cursor.getColumnIndexOrThrow(Config.USERNAME));
                                break;
                            case "password":
                                valueFound = Cursor.getString(Cursor.getColumnIndexOrThrow(Config.PASSWORD));
                                break;
                            case "device_name":
                                valueFound = Cursor.getString(Cursor.getColumnIndexOrThrow(Config.DEVICE_NAME));
                                break;
                            case "device_code":
                                valueFound = Cursor.getString(Cursor.getColumnIndexOrThrow(Config.DEVICE_CODE));
                                break;
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

    public ArrayList<String> ReturnDrinkTypes(){
        ArrayList<String> drink_types = new ArrayList<>();
        SQLiteDatabase userDatabase = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor userTableCursor = userDatabase.query(Config.TABLE_NAME_DRINK_TYPE, null, null, null, null, null, null);
        if(userTableCursor != null) {
            if(userTableCursor.moveToFirst()) {
                do {
                    String drink_type = userTableCursor.getString(userTableCursor.getColumnIndexOrThrow(Config.TYPE_OF_DRINK));
                    drink_types.add(drink_type);

                } while(userTableCursor.moveToNext());
            }
        }

        return drink_types;
    }
}

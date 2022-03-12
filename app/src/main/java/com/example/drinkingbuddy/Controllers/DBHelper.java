package com.example.drinkingbuddy.Controllers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;
import android.widget.Toast;

import com.example.drinkingbuddy.Models.Breathalyzer;

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

        String CREATE_TABLE_TYPE_OF_DRINK = "CREATE TABLE " + Config.TABLE_NAME_DRINK_TYPE
                + " (" + Config.TYPE_OF_DRINK + " TEXT NOT NULL)";

        sqLiteDatabase.execSQL(CREATE_TABLE_RESULTS);
        sqLiteDatabase.execSQL(CREATE_TABLE_TYPE_OF_DRINK);

        Log.d(TAG, "db created");

    }

    //REFERENCE: https://stackoverflow.com/questions/23068676/how-to-get-current-timestamp-in-string-format-in-java-yyyy-mm-dd-hh-mm-ss
    public String TimeStamp()
    {
        String timestamp = new SimpleDateFormat("hh:mm MM/dd/yyyy").format(new Date());
        return timestamp;
    }

    public String DayOfWeek()
    {
        String dayOfWeek = new SimpleDateFormat("EEEE").format(new Date());
        Log.d(TAG, dayOfWeek);
        return dayOfWeek;
    }


    //not currently needed but can be implemented in the future
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

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

    public List<Breathalyzer> getAllResults() {
        List<Breathalyzer> breathalyzer_values = new ArrayList<>();
        SQLiteDatabase userDatabase = this.getReadableDatabase();
        Cursor userTableCursor = userDatabase.query(Config.TABLE_NAME, null, null, null, null, null, null);
        if(userTableCursor != null) {
            if(userTableCursor.moveToFirst()) {
                do {
                    String bloodAlcohol = userTableCursor.getString(userTableCursor.getColumnIndexOrThrow(Config.Result));
                    String timeStamp = userTableCursor.getString(userTableCursor.getColumnIndexOrThrow(Config.TimeStamp));
                    String dayOfWeek = userTableCursor.getString(userTableCursor.getColumnIndexOrThrow(Config.DAY_OF_WEEK));
                    breathalyzer_values.add(new Breathalyzer(bloodAlcohol, String.valueOf(timeStamp), dayOfWeek));

                } while(userTableCursor.moveToNext());
            }
        }

        return breathalyzer_values;
    }

    public ArrayList ReturnDrinkTypes(){
        ArrayList drink_types = new ArrayList<>();
        SQLiteDatabase userDatabase = this.getReadableDatabase();
        Cursor userTableCursor = userDatabase.query(Config.TABLE_NAME_DRINK_TYPE, null, null, null, null, null, null);
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

    /*
    //function to receive list of all previous results
    public List<String> RetrieveResults()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        List<String> HistoryOfResults = new ArrayList<>();
        try{

            cursor = db.query(Config.TABLE_NAME, null, null, null, null, null, null);

            if(cursor != null)
            {
                if(cursor.moveToFirst())
                {
                    do{
                        @SuppressLint("Range") String resultRetrieved = cursor.getString(cursor.getColumnIndex(Config.Result));
                    } while(cursor.moveToNext());
                }
                return HistoryOfResults;
            }
        }catch (SQLException e)
        {
            Log.d(TAG, "EXCEPTION: " + e);
            Toast.makeText(context, "Operation Failed", Toast.LENGTH_LONG).show();
        }
        finally {
            if(cursor != null)
            {
                cursor.close();
                db.close();
            }
        }

        return HistoryOfResults;
    }*/
}

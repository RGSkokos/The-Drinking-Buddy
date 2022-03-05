package com.example.drinkingbuddy.Controllers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";

    private Context context;

    public DBHelper(Context context)
    {
        super(context, Config.DATABASE_NAME, null, Config.DATABASE_VERSION );
        this.context = context;
    }

    //simply creates database to hold breathalyzer entries
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE_RESULTS = "CREATE TABLE " + Config.TABLE_NAME
                + " (" + Config.Result + " TEXT NOT NULL)";

        sqLiteDatabase.execSQL(CREATE_TABLE_RESULTS);

        Log.d(TAG, "db created");

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

        contentValues.put(Config.Result, result);

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

    //function to receive list of all previous results
    public List<String> RetreiveResults()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try{
            List<String> HistoryOfResults = new ArrayList<>();
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
        return Collections.emptyList();
    }
}

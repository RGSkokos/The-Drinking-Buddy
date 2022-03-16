package com.example.drinkingbuddy.Controllers;
import android.app.Application;     //this allows the bluetooth connection to be maintained throughout all activities.

import com.example.drinkingbuddy.Views.LoadActivity;

public class BluetoothHelper extends Application {

    public LoadActivity BT;

    @Override
    public void onCreate()
    {
        super.onCreate();
        BT = new LoadActivity();
    }
}

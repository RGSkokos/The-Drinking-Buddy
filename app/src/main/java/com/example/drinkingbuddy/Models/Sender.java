package com.example.drinkingbuddy.Models;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Views.LoadActivity;

import java.util.UUID;

import pl.droidsonroids.gif.GifImageView;

public class Sender extends LoadActivity {

    public static String MODULE_MAC = "EC:94:CB:4E:1E:36";    // put your own mac address found with bluetooth serial app
    // This one is for the official esp32 public final static String MODULE_MAC = "EC:94:CB:4E:1E:36"; //

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public GifImageView gifImageView;

    protected BluetoothAdapter bluetoothAdapter;
    protected BluetoothSocket bluetoothSocket;
    protected BluetoothDevice bluetoothDevice;
    protected ConnectedThread newThread = null;
    protected TextView countDown;
    protected TextView done;
    protected Toolbar toolbar;
    public Handler handler;
    private DBHelper myDB;
    private String type_of_drink;
    public LoadActivity BT;






    // When a Button is Pressed, Sampling is Taken and Result Fetched Automatically
    public void sendMessage() {
        String sendMessage = "1"; // "1" = Start Sampling
        newThread.write(sendMessage.getBytes());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new CountDownTimer(5000, 1000) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long millisUntilFinished) {
                        countDown.setText(""+millisUntilFinished / 1000);
                    }
                    @Override
                    public void onFinish() {
                        gifImageView.setVisibility(View.INVISIBLE); //gif should no longer be displayed
                        countDown.setVisibility(View.INVISIBLE);
                        done.setVisibility(View.VISIBLE);
                    }
                }.start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("Problem","THREAD IS SLEEPING...Z...Z...");
                            Thread.sleep(5000);
                        }
                        catch (Exception e) {
                            Toast toast = Toast.makeText(BT.getApplicationContext(), "Something Went Wrong!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String sendMessage1 = "0"; // "0" = Stop Sampling and Get Final Data
                                newThread.write(sendMessage1.getBytes());
                            }
                        });
                    }
                }).start();
            }
        },3000);
    }
}

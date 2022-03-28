package com.example.drinkingbuddy.Models;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import android.os.Handler;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Models.ConnectedThread;
import com.example.drinkingbuddy.Views.LoadActivity;

import pl.droidsonroids.gif.GifImageView;
public class Bluetooth extends LoadActivity {
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
    public LoadActivity L;
    public Bluetooth BT;





    // Request to Turn on the Bluetooth Module of the Android Device
    public void turnOnPhoneBluetooth() {
        Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (ActivityCompat.checkSelfPermission(L.createDeviceProtectedStorageContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(enableBTIntent, 1);
                                                                                                                                }

                                        }


    public void connectToBreathalyzer() {
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(MODULE_MAC);
        if (ActivityCompat.checkSelfPermission(L, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect();
                }
            catch (Exception e) {
                try {
                    bluetoothSocket.close();
                    }
                catch (Exception ex) {
                    ex.printStackTrace();
                                    }
                e.printStackTrace();
                                }
        }
    }

    public Bluetooth initializeBluetoothProcess() {      //this is where the countdown will happen
        connectToBreathalyzer();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message receivedMessage) {
                super.handleMessage(receivedMessage);
                if(receivedMessage.what == ConnectedThread.RESPONSE_MESSAGE){
                    String message = (String) receivedMessage.obj;
                    myDB.insertNewResult(message);

                }
            }

        };
        newThread = new ConnectedThread(bluetoothSocket, handler);
        newThread.start();
        return null;
    }




}

package com.example.drinkingbuddy.Views;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;
import android.os.Handler;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Models.ConnectedThread;
import com.example.drinkingbuddy.R;

public class MainActivity extends AppCompatActivity {
    public final static String MODULE_MAC = "78:E3:6D:0A:87:92";    // put your own mac address found with bluetooth serial app
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;
    ConnectedThread newThread = null;
    TextView homeTextView;
    Button newBreath;
    TextView response;
    public Handler handler;
    DBHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB = new DBHelper(this);
        setContentView(R.layout.activity_main);
        setTitle("Drinking Buddy");
        initializeComponents();
        setupButtonListeners();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            turnOnPhoneBluetooth();
        }

        else {
            initializeBluetoothProcess();
        }
    }

    // Turn on the Bluetooth Module of the Android Device
    protected void turnOnPhoneBluetooth() {
        Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(enableBTIntent, 1);
        }
    }

    // Link Variables to Components in .XML file
    protected void initializeComponents() {
        homeTextView = (TextView) findViewById(R.id.homeTextView);
        newBreath = (Button) findViewById(R.id.newBreath);
        response = (TextView) findViewById(R.id.results);
    }

    // Setup Button Listeners
    protected void setupButtonListeners() {
        newBreath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    turnOnPhoneBluetooth();
                }

                else {
                    String sendMessage = "1";
                    newThread.write(sendMessage.getBytes());
                    newBreath.setEnabled(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(4000);
                            } catch (Exception e) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Something Went Wrong!", Toast.LENGTH_LONG);
                                toast.show();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    newBreath.setEnabled(true);
                                    String sendMessage = "0";
                                    newThread.write(sendMessage.getBytes());
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 1) {
            initializeBluetoothProcess();
        }
    }

    public void initializeBluetoothProcess() {
        if (bluetoothAdapter.isEnabled()) {
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(MODULE_MAC);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
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

            handler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message receivedMessage) {
                    super.handleMessage(receivedMessage);
                    if(receivedMessage.what == ConnectedThread.RESPONSE_MESSAGE){
                        String message = (String)receivedMessage.obj;
                        myDB.insertNewResult(message);
                        response.setText("Your Blood Alcohol Level is: " + message);
                    }
                }
            };

            newThread = new ConnectedThread(bluetoothSocket,handler);
            newThread.start();
        }
    }
}
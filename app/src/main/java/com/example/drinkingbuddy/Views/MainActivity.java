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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import android.os.Handler;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Models.Breathalyzer;
import com.example.drinkingbuddy.Models.ConnectedThread;
import com.example.drinkingbuddy.R;

public class MainActivity extends AppCompatActivity {
    public final static String MODULE_MAC = "EC:94:CB:4C:72:02";    // put your own mac address found with bluetooth serial app
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    BluetoothDevice bluetoothDevice;
    ConnectedThread newThread = null;
    TextView homeTextView;
    Button connectButton;
    Button newBreath;
    TextView response;
    ListView resultsList;
    public Handler handler;
    DBHelper myDB;
    List<Breathalyzer> breathalyzer_values;
    DecimalFormat decimalFormat = new DecimalFormat("0.0000");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//      TODO: redirect to LoginActivity
//        if (user not logged in)
//            goToLogin();

        myDB = new DBHelper(this);
        setContentView(R.layout.activity_main);
        setTitle("Drinking Buddy");
        initializeComponents();
        setupButtonListeners();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        newBreath.setEnabled(false);
        displayResults();

        if (savedInstanceState == null) {
            Log.d("MainActivity", "Test Redirect running");
            goToLogin();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 1) {
            newBreath.setEnabled(true);
            connectButton.setEnabled(false);
            initializeBluetoothProcess();
        }
    }

    // Link Variables to Components in .XML file
    protected void initializeComponents() {
        homeTextView = (TextView) findViewById(R.id.homeTextView);
        connectButton = (Button) findViewById(R.id.connectButton);
        newBreath = (Button) findViewById(R.id.newBreath);
        response = (TextView) findViewById(R.id.response);
        resultsList = (ListView) findViewById(R.id.resultsList);
    }

    // Setup Button Listeners
    protected void setupButtonListeners() {
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    turnOnPhoneBluetooth();
                }

                else {
                    newBreath.setEnabled(true);
                    connectButton.setEnabled(false);
                    initializeBluetoothProcess();
                }
            }
        });

        newBreath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    // Request to Turn on the Bluetooth Module of the Android Device
    protected void turnOnPhoneBluetooth() {
        Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(enableBTIntent, 1);
        }
    }

    // When a Button is Pressed, Sampling is Taken and Result Fetched Automatically
    protected void sendMessage() {
        String sendMessage = "1"; // "1" = Start Sampling
        newThread.write(sendMessage.getBytes());
        newBreath.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                }

                catch (Exception e) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Something Went Wrong!", Toast.LENGTH_LONG);
                    toast.show();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newBreath.setEnabled(true);
                        String sendMessage = "0"; // "0" = Stop Sampling and Get Final Data
                        newThread.write(sendMessage.getBytes());
                    }
                });
            }
        }).start();
    }

    public void connectToBreathalyzer() {
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(MODULE_MAC);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect();
            }

            catch (Exception e) {
                try {
                    bluetoothSocket.close();
                    newBreath.setEnabled(false);
                    connectButton.setEnabled(true);
                }

                catch (Exception ex) {
                    ex.printStackTrace();
                    newBreath.setEnabled(false);
                    connectButton.setEnabled(true);
                }

                e.printStackTrace();
                newBreath.setEnabled(false);
                connectButton.setEnabled(true);
            }
        }
    }

    public void initializeBluetoothProcess() {
        connectToBreathalyzer();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message receivedMessage) {
                super.handleMessage(receivedMessage);
                if(receivedMessage.what == ConnectedThread.RESPONSE_MESSAGE){
                    String message = (String) receivedMessage.obj;
                    myDB.insertNewResult(message);
                    double temp = Double.parseDouble(message);
                    temp = (((temp-1500)/5000));
                    response.setText("Your Blood Alcohol Level is: " + String.valueOf(decimalFormat.format(temp)));
                    displayResults();
                }
            }
        };

        newThread = new ConnectedThread(bluetoothSocket, handler);
        newThread.start();
    }

    // Display the Database
    public void displayResults() {
        breathalyzer_values = myDB.getAllResults();
        ArrayList<String> sampledResults = new ArrayList<>();
        for(int i = breathalyzer_values.size()-1; i >= 0; i--) {
            double temp = Double.parseDouble(breathalyzer_values.get(i).getResult());
            String timeStamp = breathalyzer_values.get(i).getTimeStamp();
            temp = (((temp-1500)/5000));
            sampledResults.add(String.valueOf(decimalFormat.format(temp) + "%, Time:" + timeStamp));
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sampledResults);
        resultsList.setAdapter(arrayAdapter);
    }

    protected void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
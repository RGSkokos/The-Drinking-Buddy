package com.example.drinkingbuddy.Views;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import java.util.UUID;
import static android.view.animation.Animation.RELATIVE_TO_SELF;
import android.os.Handler;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Controllers.SharedPreferencesHelper;
import com.example.drinkingbuddy.Models.ConnectedThread;
import com.example.drinkingbuddy.R;
import pl.droidsonroids.gif.GifImageView;

public class LoadActivity extends AppCompatActivity {
    public static String MODULE_MAC = "7C:9E:BD:45:43:F2";    // put your own mac address found with bluetooth serial app
    // This one is for the official esp32 public final static String MODULE_MAC = "EC:94:CB:4E:1E:36"; //

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public GifImageView gifImageView;
    protected ProgressBar progressBarView;
    protected ImageView static_circle;
    protected GifImageView loading_circle;
    protected BluetoothAdapter bluetoothAdapter;
    protected BluetoothSocket bluetoothSocket;
    protected BluetoothDevice bluetoothDevice;
    protected ConnectedThread newThread = null;
    protected TextView countDown;
    protected TextView done;
    protected TextView sensorResult;
    protected Toolbar toolbar;
    public Handler handler;
    private DBHelper myDB;
    private String type_of_drink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB = new DBHelper(this);
        setContentView(R.layout.activity_load);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(LoadActivity.this);
        MODULE_MAC = myDB.getDeviceCode(sharedPreferencesHelper.getLoginId());
        Log.d("MODULE_MAC", MODULE_MAC);
        initializeComponents();
        sensorResult.setText("");
        loadingTimer();

        // Set up the toolbar
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            type_of_drink = bundle.getString("type_of_drink");
        }
    }

    // Link Variables to Components in .XML file
    protected void initializeComponents() {
        gifImageView = (GifImageView) findViewById(R.id.loading_gif);
        loading_circle = (GifImageView) findViewById(R.id.loading_circle);
        static_circle = (ImageView) findViewById(R.id.static_circle);
        done = (TextView) findViewById(R.id.done);
        countDown = (TextView) findViewById(R.id.ReadingCount);
        toolbar = findViewById(R.id.toolbarLoad);
        countDown.setVisibility(View.INVISIBLE);
        done.setVisibility(View.INVISIBLE);
        type_of_drink = "Unknown";
        sensorResult = (TextView) findViewById(R.id.sensorResult);
        //progressBarView.setVisibility(View.INVISIBLE);
        static_circle.setVisibility(View.INVISIBLE);
        loading_circle.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 1) {
            initializeBluetoothProcess();
        }
    }

    protected void loadingTimer() {      //this will run the gif for 5 seconds to mimic a "loading screen"
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                gifImageView.setVisibility(View.INVISIBLE);        //gif should no longer be displayed
                countDown.setText("READY");
                countDown.setVisibility(View.VISIBLE);
                done.setEnabled(false);
                done.setVisibility(View.INVISIBLE);

                sendMessage();
            }
        }.start();
    }


    protected void setup() {
        if (!bluetoothAdapter.isEnabled()) {
            turnOnPhoneBluetooth(); //checks if bluetooth is enabled on phone and if not turns on user bluetooth
        }
        else {
            initializeBluetoothProcess(); //if bluetooth is indeed enabled, then the bluetooth process must be enabled and established
            //sendMessage();
        }
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
        Log.d("Problem","SEND MESSAGE 1111");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new CountDownTimer(5000, 1000) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long millisUntilFinished) {
                        static_circle.setVisibility(View.VISIBLE);
                        loading_circle.setVisibility(View.VISIBLE);
                        countDown.setText(""+millisUntilFinished / 1000);


                    }
                    @Override
                    public void onFinish() {
                        gifImageView.setVisibility(View.INVISIBLE); //gif should no longer be displayed
                        countDown.setVisibility(View.INVISIBLE);
                        done.setVisibility(View.VISIBLE);
                        static_circle.setVisibility(View.INVISIBLE);
                        loading_circle.setVisibility(View.INVISIBLE);
                    }
                }.start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String sendMessage = "1"; // "1" = Start Sampling
                            newThread.write(sendMessage.getBytes());
                            Log.d("Problem","THREAD IS SLEEPING...Z...Z...");
                            Thread.sleep(5000);
                        }
                        catch (Exception e) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Something Went Wrong!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String sendMessage1 = "0"; // "0" = Stop Sampling and Get Final Data
                                newThread.write(sendMessage1.getBytes());
                                Log.d("Problem","SEND MESSAGE 0000");
                            }
                        });
                    }
                }).start();
            }
        },3000);
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
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }

    public void initializeBluetoothProcess() {      //this is where the countdown will happen
        connectToBreathalyzer();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message receivedMessage) {
                super.handleMessage(receivedMessage);
                if(receivedMessage.what == ConnectedThread.RESPONSE_MESSAGE){
                    String message = (String) receivedMessage.obj;
                    myDB.insertNewResult(message);
                    float temp = Float.parseFloat(message);
                    temp = (((temp - 150) / 1050)); //second value in numerator needs to be based on calibration
                    temp = (temp<0) ? 0 : temp; //this is to avoid negative values and are now considered absolute zero for constraint purposes
                    sensorResult.setText(String.valueOf("Sensor has Measured: " + String.format("%.3f", temp) + "% of Blood Alcohol Level"));

                    //double temp = Double.parseDouble(message);
                    //temp = (((temp-1500)/5000));
                    //response.setText("Your Blood Alcohol Level is: " + String.valueOf(decimalFormat.format(temp)));
                    //displayResults();
                }
            }
        };

        newThread = new ConnectedThread(bluetoothSocket, handler);
        newThread.start();
    }


    @SuppressLint("SetTextI18n")
    public void setTypeOfDrink(String type) {
        type_of_drink = type;
    }


    @Override
    protected void onStart() {
        super.onStart();
        setup();
    }
}
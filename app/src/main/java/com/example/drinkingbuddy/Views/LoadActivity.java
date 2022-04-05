package com.example.drinkingbuddy.Views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import java.util.UUID;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Controllers.FirebaseHelper;
import com.example.drinkingbuddy.Models.ConnectedThread;
import com.example.drinkingbuddy.R;

import pl.droidsonroids.gif.GifImageView;

public class LoadActivity extends AppCompatActivity {

    protected BluetoothAdapter bluetoothAdapter;
    protected BluetoothSocket bluetoothSocket;
    protected BluetoothDevice bluetoothDevice;
    protected ConnectedThread newThread = null;
    protected GifImageView gifImageView;
    protected TextView countDown;
    protected TextView loadInstruction;
    protected ImageView staticCircle;
    protected GifImageView loadingCircle;
    protected ImageView resultCircle;
    protected Button newSampleButton;
    protected TextView resultTextView;
    protected Toolbar toolbar;

    public static String MODULE_MAC = "EC:94:CB:4E:1E:36";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    public Handler handler;
    private DBHelper myDB;
    private float messageResult;
    protected SharedPreferences cannotConnect;
    protected FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        myDB = new DBHelper(this);
        firebaseHelper = new FirebaseHelper(this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Log.d("MODULE_MAC", MODULE_MAC);
        initializeComponents();

        cannotConnect = getSharedPreferences("noConnection", Context.MODE_PRIVATE);
        SharedPreferences.Editor connectionEditor = cannotConnect.edit();
        connectionEditor.putString("noConnection", "");
        connectionEditor.apply();
//        sensorResult.setText("");

        Bundle extras = getIntent().getExtras();
        if(extras != null)
            MODULE_MAC = extras.getString("MAC");

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        setup();
    }

    // Link Variables to Components in .XML file
    protected void initializeComponents() {
        loadingCircle = findViewById(R.id.loadingCircle);
        loadingCircle.setVisibility(View.INVISIBLE);
        staticCircle = findViewById(R.id.staticLoadingCircle);
        staticCircle.setVisibility(View.INVISIBLE);
        resultCircle = findViewById(R.id.resultCircle);
        resultCircle.setVisibility(View.INVISIBLE);
        gifImageView = findViewById(R.id.loadingGif);
        loadInstruction = findViewById(R.id.loadInstruction);
        countDown = findViewById(R.id.readingCount);
        toolbar = findViewById(R.id.toolbarLoad);
        countDown.setVisibility(View.INVISIBLE);
        newSampleButton = findViewById(R.id.newSampleButton);
        newSampleButton.setOnClickListener(onClickNewSample);
        newSampleButton.setVisibility(View.INVISIBLE);
        resultTextView = findViewById(R.id.resultTextView);
        resultTextView.setVisibility(View.INVISIBLE);
        messageResult = -1;
    }

    private final View.OnClickListener onClickNewSample = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            goToHomeActivity();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 1) {
            loadingTimer();
            initializeBluetoothProcess();
        }
        else {
            goToHomeActivity();
        }
    }

    protected void loadingTimer() {      //this will run the gif for 5 seconds to mimic a "loading screen"
        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
//                gifImageView.setVisibility(View.INVISIBLE); //gif should no longer be displayed
                if(!cannotConnect.getString("noConnection", null).equals("")) {
                    countDown.setText("");
                }

                sendMessage();
            }
        }.start();
    }


    protected void setup() {
        if (!bluetoothAdapter.isEnabled()) {
            turnOnPhoneBluetooth(); //checks if bluetooth is enabled on phone and if not turns on user bluetooth
        }
        else {
            loadingTimer();
            initializeBluetoothProcess(); //if bluetooth is indeed enabled, then the bluetooth process must be enabled and established
//            sendMessage();
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
                gifImageView.setVisibility(View.INVISIBLE); //gif should no longer be displayed
                loadInstruction.setVisibility(View.INVISIBLE);
                countDown.setVisibility(View.VISIBLE);
                resultCircle.setVisibility(View.INVISIBLE);
                staticCircle.setVisibility(View.VISIBLE);
                loadingCircle.setVisibility(View.VISIBLE);
                new CountDownTimer(6000, 1000) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long millisUntilFinished) {
                        countDown.setText("" + (millisUntilFinished / 1000));
                    }
                    @Override
                    public void onFinish() {

                    }
                }.start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String sendMessage = "1"; // "1" = Start Sampling
                            newThread.write(sendMessage.getBytes());
                            Log.d("Problem","THREAD IS SLEEPING...Z...Z...");
                            Thread.sleep(6000);
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

    public void initializeBluetoothProcess() {      //this is where the countdown will happen
        connectToBreathalyzer();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message receivedMessage) {
                super.handleMessage(receivedMessage);
                if(receivedMessage.what == ConnectedThread.RESPONSE_MESSAGE){
                    String message = (String) receivedMessage.obj;
                    String UID = firebaseHelper.getCurrentUID();
                    myDB.insertNewResult(message, UID);
                    float temp = Float.parseFloat(message);
                    Log.d("SENSOR VALUE", Float.toString(temp));
                    temp = Math.abs(((temp - 4095) / 4095)); //second value in numerator needs to be based on calibration
                    temp = (temp<0) ? 0 : temp; //this is to avoid negative values and are now considered absolute zero for constraint purposes
                    messageResult = temp;
                    countDown.setText(String.valueOf(String.format("%.2f", messageResult) + ""));
                    staticCircle.setVisibility(View.INVISIBLE);
                    loadingCircle.setVisibility(View.INVISIBLE);
                    resultCircle.setVisibility(View.VISIBLE);
                    resultTextView.setVisibility(View.VISIBLE);
                    newSampleButton.setVisibility(View.VISIBLE);
                    setResultColour();
                }
            }
        };

        newThread = new ConnectedThread(bluetoothSocket, handler);
        newThread.start();
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
                    SharedPreferences cannotConnect = getSharedPreferences("noConnection", Context.MODE_PRIVATE);
                    SharedPreferences.Editor connectionEditor = cannotConnect.edit();
                    connectionEditor.putString("noConnection", "Error! Cannot Connect to Breathalyzer");
                    connectionEditor.apply();
                    goToHomeActivity();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }


    protected void setResultColour() {
        if (messageResult < 0.02) {
            resultCircle.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_res)));
            countDown.setTextColor(getResources().getColor(R.color.green_res));
        } else if (messageResult < 0.07) {
            resultCircle.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange_res)));
            countDown.setTextColor(getResources().getColor(R.color.orange_res));
        } else {
            resultCircle.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_res)));
            countDown.setTextColor(getResources().getColor(R.color.red_res));
        }
    }

    protected void goToHomeActivity() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
}
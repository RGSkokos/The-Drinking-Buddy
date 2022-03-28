package com.example.drinkingbuddy.Views;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.example.drinkingbuddy.Models.Bluetooth;
import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Controllers.SharedPreferencesHelper;
import com.example.drinkingbuddy.Models.Sender;
import com.example.drinkingbuddy.R;
import pl.droidsonroids.gif.GifImageView;

public class LoadActivity extends AppCompatActivity {
    public static String MODULE_MAC = "EC:94:CB:4E:1E:36";    // put your own mac address found with bluetooth serial app
    // This one is for the official esp32 public final static String MODULE_MAC = "EC:94:CB:4E:1E:36"; //
    public GifImageView gifImageView;
    protected BluetoothAdapter bluetoothAdapter;
    protected TextView countDown;
    protected TextView done;
    protected Toolbar toolbar;
    private DBHelper myDB;
    private String type_of_drink;
    public Bluetooth BT;
    public Sender S;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDB = new DBHelper(this);
        BT = new Bluetooth();
        setContentView(R.layout.activity_load);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(LoadActivity.this);
        MODULE_MAC = myDB.getDeviceCode(sharedPreferencesHelper.getLoginId());
        Log.d("MODULE_MAC", MODULE_MAC);
        initializeComponents();
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
        done = (TextView) findViewById(R.id.done);
        countDown = (TextView) findViewById(R.id.ReadingCount);
        toolbar = findViewById(R.id.toolbarLoad);
        countDown.setVisibility(View.INVISIBLE);
        done.setVisibility(View.INVISIBLE);
        type_of_drink = "Unknown";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 1) {
            BT.initializeBluetoothProcess();
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
                                    }
                                                                }.start();
                                 }


    public void setup() {
        if (!bluetoothAdapter.isEnabled()) {
            BT.turnOnPhoneBluetooth(); //checks if bluetooth is enabled on phone and if not turns on user bluetooth
                                            }
        else {
            BT.initializeBluetoothProcess();
             //if bluetooth is indeed enabled, then the bluetooth process must be enabled and established
            S.sendMessage();
            }

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
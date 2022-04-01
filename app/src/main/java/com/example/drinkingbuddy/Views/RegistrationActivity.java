package com.example.drinkingbuddy.Views;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Controllers.FirebaseHelper;
import com.example.drinkingbuddy.Models.Profile;
import com.example.drinkingbuddy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RegistrationActivity extends AppCompatActivity {

    protected EditText usernameRegisterEditText;
    protected EditText passwordRegisterEditText;
    protected EditText deviceNameEditText;
    protected EditText deviceCodeEditText;
    protected EditText emailRegisterEditText;
    protected Button registerButton;
    private String username;
    private String password;
    private String deviceName;
    private String deviceCode;
    private String email;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initializeComponents();
        setupButtonListeners();
    }

    protected void initializeComponents() {
        firebaseHelper = new FirebaseHelper(this);
        usernameRegisterEditText = findViewById(R.id.usernameRegisterEditText);
        passwordRegisterEditText = findViewById(R.id.passwordRegisterEditText);
        deviceNameEditText = findViewById(R.id.deviceNameEditText);
        deviceCodeEditText = findViewById(R.id.deviceCodeEditText);
        registerButton = findViewById(R.id.registerButton);
        emailRegisterEditText = findViewById(R.id.EmailAddressTextView);
    }

    protected void setupButtonListeners() {
        registerButton.setOnClickListener(view -> {

            username = usernameRegisterEditText.getText().toString();
            password = passwordRegisterEditText.getText().toString();
            deviceName = deviceNameEditText.getText().toString();
            deviceCode = deviceCodeEditText.getText().toString();
            email = emailRegisterEditText.getText().toString();



            //need to fix user and device code authentication


             //check if inputs are correct and, if so, finish registration
                if (username.isEmpty() || password.isEmpty() || deviceName.isEmpty() || deviceCode.isEmpty() || email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Empty input", Toast.LENGTH_LONG).show();
                } else if (Character.isDigit(username.charAt(0)) || username.charAt(0) == '-' || username.charAt(username.length()-1) == '-') {
                    Toast.makeText(getApplicationContext(), "Invalid username", Toast.LENGTH_LONG).show();
                } else if (username.length() < 3) {
                    Toast.makeText(getApplicationContext(), "Username too short", Toast.LENGTH_LONG).show();
                } else if (password.length() < 5) {
                    Toast.makeText(getApplicationContext(), "Password too short", Toast.LENGTH_LONG).show();
                } else if (deviceCode.length() != 17 || deviceCode.charAt(2) != ':' || deviceCode.charAt(5) != ':' || deviceCode.charAt(8) != ':' || deviceCode.charAt(11) != ':' || deviceCode.charAt(14) != ':') {
                    Toast.makeText(getApplicationContext(), "Invalid device code", Toast.LENGTH_LONG).show();
                }
                else { //If everything is okay
                    Profile NewProfile = new Profile(username, password, deviceName, deviceCode);
                    firebaseHelper.CreateUser(email, password, NewProfile);
                    //redirectToMain();
                    //create user with email and password
                }

        });
    }

    protected void redirectToMain() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
}
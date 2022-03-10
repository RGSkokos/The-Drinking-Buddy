package com.example.drinkingbuddy.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.drinkingbuddy.R;

public class RegistrationActivity extends AppCompatActivity {

    protected EditText usernameRegisterEditText;
    protected EditText passwordRegisterEditText;
    protected EditText deviceNameEditText;
    protected EditText deviceCodeEditText;
    protected Button registerButton;
    private String username;
    private String password;
    private String deviceName;
    private String deviceCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initializeComponents();
        setupButtonListeners();
    }

    protected void initializeComponents() {
        usernameRegisterEditText = findViewById(R.id.usernameRegisterEditText);
        passwordRegisterEditText = findViewById(R.id.passwordRegisterEditText);
        deviceNameEditText = findViewById(R.id.deviceNameEditText);
        deviceCodeEditText = findViewById(R.id.deviceCodeEditText);
        registerButton = findViewById(R.id.registerButton);
    }

    protected void setupButtonListeners() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: put data obtained in database
                username = usernameRegisterEditText.getText().toString();
                password = passwordRegisterEditText.getText().toString();
                deviceName = deviceNameEditText.getText().toString();
                deviceCode = deviceCodeEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty() || deviceName.isEmpty() || deviceCode.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Empty input", Toast.LENGTH_LONG).show();
                } else if (Character.isDigit(username.charAt(0)) || username.charAt(0) == '-' || username.charAt(username.length()-1) == '-') {
                    Toast.makeText(getApplicationContext(), "Invalid username", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_LONG).show();
                    redirectToMain();
                }
            }
        });
    }

    protected void redirectToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
package com.example.drinkingbuddy.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.R;

public class LoginActivity extends AppCompatActivity {

    protected Button loginButton;
    protected EditText usernameLoginTextEdit;
    protected EditText passwordLoginTextEdit;
    protected Button registrationRedirect;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DBHelper(this);
        initializeComponents();
        setupButtonListeners();
    }

    protected void initializeComponents() {
        loginButton = findViewById(R.id.loginButton);
        usernameLoginTextEdit = findViewById(R.id.usernameLoginEditText);
        passwordLoginTextEdit = findViewById(R.id.passwordLoginEditText);
        registrationRedirect = findViewById(R.id.registrationRedirectButton);
    }

    protected void setupButtonListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameEntered = usernameLoginTextEdit.getText().toString();
                String passwordEntered = passwordLoginTextEdit.getText().toString();
                if (usernameEntered.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Missing input", Toast.LENGTH_LONG).show();
                }


                if (db.CheckProfile(usernameEntered, passwordEntered)) //check if profile with given user and password exists
                {
                    Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
                    redirectToMain(); //if exists redirect to main with extras to stay on MainActivity
                }
                else {
                    Toast.makeText(getApplicationContext(), "Username or password doesn't exist", Toast.LENGTH_LONG).show();
                    //Simply tell the user the inputted username and password is wrong
                }

            }
        });
        registrationRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegistration();
            }
        });
    }

    protected void redirectToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        //the following line was found at this reference: https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android-application
        //its a method to pass a value with the intent
        intent.putExtra("LoggedIn", true);
        startActivity(intent);
    }

    private void goToRegistration() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }
}
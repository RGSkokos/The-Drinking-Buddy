package com.example.drinkingbuddy.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Controllers.SharedPreferencesHelper;
import com.example.drinkingbuddy.R;

public class LoginActivity extends AppCompatActivity {

    protected Button loginButton;
    protected EditText usernameLoginTextEdit;
    protected EditText passwordLoginTextEdit;
    protected Button registrationRedirect;
    private DBHelper db;
    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DBHelper(this);
        sharedPreferencesHelper = new SharedPreferencesHelper(LoginActivity.this);
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
                if (usernameEntered.isEmpty() || passwordEntered.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Missing input", Toast.LENGTH_LONG).show();
                }
                int id = db.checkProfile(usernameEntered, passwordEntered);
                if (id != 0) //check if profile with given user and password exists
                {
                    Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
                    sharedPreferencesHelper.saveLoginId(id);
                    Log.d("LOGIN ID", Integer.toString(id));
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
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

    private void goToRegistration() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }
}
package com.example.drinkingbuddy.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drinkingbuddy.R;

public class LoginActivity extends AppCompatActivity {

    protected Button loginButton;
    protected EditText usernameLoginTextEdit;
    protected EditText passwordLoginTextEdit;
    protected Button registrationRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
                // TODO: get username from DB
                String usernameEntered = usernameLoginTextEdit.getText().toString();
                if (usernameEntered.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Missing input", Toast.LENGTH_LONG).show();
                }

                // TODO: complete this section
                // if (DB doesn't contain username)
                //    toast(username doesn't exist)
                // else
                Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
                redirectToMain();
                // end else
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
        startActivity(intent);
    }

    private void goToRegistration() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }
}
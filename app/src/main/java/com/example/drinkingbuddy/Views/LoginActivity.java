package com.example.drinkingbuddy.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Controllers.FirebaseHelper;
import com.example.drinkingbuddy.R;

import pl.droidsonroids.gif.GifImageView;

public class LoginActivity extends AppCompatActivity {

    protected Button loginButton;
    protected Button resetPasswordButton;
    protected EditText emailLoginTextEdit;
    protected EditText passwordLoginTextEdit;
    protected Button registrationRedirect;
    protected TextView loginTitleTextView;
    protected TextView forgotPasswordTextView;
    protected TextView registerTextView;
    private DBHelper db;
    private FirebaseHelper firebaseHelper;
    public GifImageView gifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DBHelper(this);
        initializeComponents();
        setupButtonListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void initializeComponents() {
        firebaseHelper = new FirebaseHelper(this);
        gifImageView = findViewById(R.id.loading_gif);
        resetPasswordButton = findViewById(R.id.forgotPasswordButton);
        loginTitleTextView = findViewById(R.id.loginTitleTextView);
        forgotPasswordTextView = findViewById(R.id.ForgotPasswordTextView);
        loginButton = findViewById(R.id.loginButton);
        emailLoginTextEdit = findViewById(R.id.emailLoginEditText);
        passwordLoginTextEdit = findViewById(R.id.passwordLoginEditText);
        registrationRedirect = findViewById(R.id.registrationRedirectButton);
        registerTextView = findViewById(R.id.registrationOptionTextView);
    }

    protected void setupButtonListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailEntered = emailLoginTextEdit.getText().toString();
                String passwordEntered = passwordLoginTextEdit.getText().toString();
                if(emailEntered.isEmpty() || passwordEntered.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please enter an email and password", Toast.LENGTH_LONG).show();
                }
                else
                {
                    firebaseHelper.authorizeUser(emailEntered, passwordEntered);
                    loadingTimer();
                }

            }
        });
        registrationRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegistration();
            }
        });
        //REFERENCE: https://riptutorial.com/android/example/13300/send-firebase-password-reset-email
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailEntered = emailLoginTextEdit.getText().toString();
                if(!emailEntered.isEmpty()) {
                   firebaseHelper.sendResetEmail(emailEntered);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please enter your email in the space above", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    protected void redirectToMain() {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra("LogIn", true);
        startActivity(intent);
    }

    private void goToRegistration() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    protected void loadingTimer() {      //this will run the gif for 5 seconds to mimic a "loading screen"
        gifImageView.setVisibility(View.VISIBLE);
        resetPasswordButton.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.INVISIBLE);
        emailLoginTextEdit.setVisibility(View.INVISIBLE);
        passwordLoginTextEdit.setVisibility(View.INVISIBLE);
        registrationRedirect.setVisibility(View.INVISIBLE);
        forgotPasswordTextView.setVisibility(View.INVISIBLE);
        registrationRedirect.setVisibility(View.INVISIBLE);
        loginTitleTextView.setVisibility(View.INVISIBLE);
        registerTextView.setVisibility(View.INVISIBLE);


        new CountDownTimer(1000, 500) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                gifImageView.setVisibility(View.INVISIBLE);        //gif should no longer be displayed
                redirectToMain();
            }
        }.start();
    }
}
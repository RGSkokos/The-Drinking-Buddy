package com.example.drinkingbuddy.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Controllers.SharedPreferencesHelper;
import com.example.drinkingbuddy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    protected Button loginButton;
    protected Button resetPasswordButton;
    protected EditText emailLoginTextEdit;
    protected EditText passwordLoginTextEdit;
    protected Button registrationRedirect;
    private DBHelper db;
    private FirebaseAuth firebaseAuth;
    private SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DBHelper(this);
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferencesHelper = new SharedPreferencesHelper(LoginActivity.this);
        initializeComponents();
        setupButtonListeners();
    }

    protected void initializeComponents() {
        resetPasswordButton = findViewById(R.id.forgotPasswordButton);
        loginButton = findViewById(R.id.loginButton);
        emailLoginTextEdit = findViewById(R.id.emailLoginEditText);
        passwordLoginTextEdit = findViewById(R.id.passwordLoginEditText);
        registrationRedirect = findViewById(R.id.registrationRedirectButton);
    }

    protected void setupButtonListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailEntered = emailLoginTextEdit.getText().toString();
                String passwordEntered = passwordLoginTextEdit.getText().toString();
                /*if (usernameEntered.isEmpty() || passwordEntered.isEmpty()) {
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
                }*/
                if(!emailEntered.isEmpty() || !passwordEntered.isEmpty())
                {
                    authorizeUser(emailEntered, passwordEntered);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please enter an email and password", Toast.LENGTH_LONG).show();
                }


            }
        });
        registrationRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegistration();
            }
        });
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailEntered = emailLoginTextEdit.getText().toString();
                if(!emailEntered.isEmpty()) {
                    firebaseAuth.sendPasswordResetEmail(emailEntered)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Password reset email sent", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "reset password failed, please enter a valid email connected to an account", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please enter your email in the space above", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void authorizeUser(String emailEntered, String passwordEntered)
    {
        firebaseAuth.signInWithEmailAndPassword(emailEntered, passwordEntered)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            firebaseAuth.updateCurrentUser(user);
                            redirectToMain();
                        } else
                        {
                            Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_LONG).show();
                            //Simply tell the user the inputted username and password is wrong
                            //firebaseAuth.updateCurrentUser(null);
                        }

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
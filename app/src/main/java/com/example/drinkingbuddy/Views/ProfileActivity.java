package com.example.drinkingbuddy.Views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drinkingbuddy.Controllers.DBHelper;
import com.example.drinkingbuddy.Controllers.SharedPreferencesHelper;
import com.example.drinkingbuddy.Models.Profile;
import com.example.drinkingbuddy.R;

public class ProfileActivity extends AppCompatActivity {

    protected EditText username;
    protected EditText password;
    protected EditText confirmPassword;
    protected EditText deviceName;
    protected EditText deviceCode;
    protected Button saveButton;
    protected TextView usernameDescription;
    protected TextView passwordDescription;
    protected TextView deviceNameDescription;
    protected TextView deviceCodeDescription;
    protected Toolbar toolbar;
    protected Menu menu;
    private DBHelper database;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private Profile profile;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeComponents();
        database = new DBHelper(this);
        sharedPreferencesHelper = new SharedPreferencesHelper(ProfileActivity.this);
        profile = database.getProfileById(sharedPreferencesHelper.getLoginId());

        // Set up the toolbar
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        flag = true;
        setDisplayMode();
    }

    protected void initializeComponents() {
        username = findViewById(R.id.usernameProfileEditText);
        password = findViewById(R.id.passwordProfileEditText);
        confirmPassword = findViewById(R.id.confirmPasswordEditText);
        deviceName = findViewById(R.id.deviceNameProfileEditText);
        deviceCode = findViewById(R.id.deviceCodeProfileEditText);
        saveButton = findViewById(R.id.saveProfileButton);
        usernameDescription = findViewById(R.id.changeUsernameTextView);
        passwordDescription = findViewById(R.id.changePasswordTextView);
        deviceNameDescription = findViewById(R.id.changeDeviceNameTextView);
        deviceCodeDescription = findViewById(R.id.changeDeviceCodeTextView);
        toolbar = findViewById(R.id.profileToolbar);
        saveButton.setOnClickListener(saveOnClick);
    }

    // ********* COMPLETE SAVE ON CLICK LISTENER ********
    // issue: when to know not to save bc item incomplete? and when to switch to display mode

    private final View.OnClickListener saveOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (username.getText().length() != 0) {
                if (username.getText().length() < 3) {
                    Toast.makeText(getApplicationContext(), "Username too short!", Toast.LENGTH_LONG).show();
                } else {
                    // TODO: update username for user with id = sharedPreferencesHelper.getLoginId() in DB
                }
            }
            if (password.getText().length() != 0) {
                if (password.getText().length() < 5) {
                    Toast.makeText(getApplicationContext(), "Password too short!", Toast.LENGTH_LONG).show();
                } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_LONG).show();
                } else {
                    // TODO: update password
                }
            }
            if (deviceName.getText().length() != 0) {
                // TODO: update device name
            }
            if (deviceCode.getText().length() != 0) {
                // TODO: update device code
            }
            profile = database.getProfileById(sharedPreferencesHelper.getLoginId());
        }
    };

    // Sets up the menu option bar to show profile and logout options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        this.menu = menu;
        return true;
    }

    // Goes to edit mode if menu option selected
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.editProfileMenuItem) {
            if (flag) { // currently on display mode
                setEditMode(); // go to Edit Mode
                menuItemTitleChange(flag); // change to "Display Profile" menu option
            } else {
                setDisplayMode();
                menuItemTitleChange(flag);
            }
            flag = !flag;
            return true;
        } else if (item.getItemId() == R.id.logoutProfileMenuItem) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void goToHome() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

    protected void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    protected void logout() {
        sharedPreferencesHelper.saveLoginId(0);
        goToLogin();
    }

    protected void setDisplayMode() {
        username.setEnabled(false);
        password.setVisibility(View.GONE);
        confirmPassword.setVisibility(View.GONE);
        deviceName.setEnabled(false);
        deviceCode.setEnabled(false);

        username.setHint(profile.getUsername());
        deviceName.setHint(profile.getDeviceName());
        deviceCode.setHint(profile.getDeviceCode());

        usernameDescription.setText(R.string.username_display_description);
        passwordDescription.setVisibility(View.GONE);
        deviceNameDescription.setText(R.string.device_name_display_description);
        deviceCodeDescription.setText(R.string.device_code_display_description);
        saveButton.setVisibility(View.GONE);
    }

    protected void setEditMode() {
        username.setEnabled(true);
        password.setVisibility(View.VISIBLE);
        confirmPassword.setVisibility(View.VISIBLE);
        deviceName.setEnabled(true);
        deviceCode.setEnabled(true);

        username.setHint("Username");
        deviceName.setHint("Device name");
        deviceCode.setHint("Device code");

        usernameDescription.setText(R.string.change_username);
        passwordDescription.setVisibility(View.VISIBLE);
        deviceNameDescription.setText(R.string.change_device_name);
        deviceCodeDescription.setText(R.string.change_device_code);
        saveButton.setVisibility(View.VISIBLE);
    }

    protected void menuItemTitleChange(boolean val) {
        MenuItem item = menu.findItem(R.id.editProfileMenuItem);
        if (val) // edit mode
            item.setTitle("Display Profile");
        else
            item.setTitle("Edit Profile");
    }
}
package com.example.drinkingbuddy.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;

import com.example.drinkingbuddy.Controllers.FirebaseHelper;
import com.example.drinkingbuddy.Models.Config;
import com.example.drinkingbuddy.Models.Profile;
import com.example.drinkingbuddy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private Profile profile;
    private boolean flag;
    protected String currentPassword;
    private FirebaseHelper firebaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeComponents();
        // Set up the toolbar
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            String profileVals = extras.getString("profile");
            String username = profileVals.substring(0, profileVals.indexOf(" "));
            profileVals = profileVals.substring(profileVals.indexOf(" ") + 1);
            String deviceCode = profileVals.substring(0, profileVals.indexOf(" "));
            profileVals = profileVals.substring(profileVals.indexOf(" ") + 1);
            String deviceName = profileVals.substring(0, profileVals.indexOf(" "));
            profileVals = profileVals.substring(profileVals.indexOf(" ") + 1);
            String password = profileVals;
            profile = new Profile(username, password, deviceName, deviceCode);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(profile != null)
            updateProfileValues();
        flag = true;
        setDisplayMode();

    }

    protected void initializeComponents() {
        firebaseHelper = new FirebaseHelper(this);
        firebaseHelper.addProfileListener();
        username = findViewById(R.id.usernameProfileEditText);
        username.setHintTextColor(getResources().getColor(R.color.white));
        password = findViewById(R.id.passwordProfileEditText);
        password.setHintTextColor(getResources().getColor(R.color.white));
        confirmPassword = findViewById(R.id.confirmPasswordEditText);
        confirmPassword.setHintTextColor(getResources().getColor(R.color.white));
        deviceName = findViewById(R.id.deviceNameProfileEditText);
        deviceName.setHintTextColor(getResources().getColor(R.color.white));
        deviceCode = findViewById(R.id.deviceCodeProfileEditText);
        deviceCode.setHintTextColor(getResources().getColor(R.color.white));
        saveButton = findViewById(R.id.saveProfileButton);
        usernameDescription = findViewById(R.id.changeUsernameTextView);
        usernameDescription.setHintTextColor(getResources().getColor(R.color.white));
        passwordDescription = findViewById(R.id.changePasswordTextView);
        passwordDescription.setHintTextColor(getResources().getColor(R.color.white));
        deviceNameDescription = findViewById(R.id.changeDeviceNameTextView);
        deviceNameDescription.setHintTextColor(getResources().getColor(R.color.white));
        deviceCodeDescription = findViewById(R.id.changeDeviceCodeTextView);
        deviceCodeDescription.setHintTextColor(getResources().getColor(R.color.white));
        toolbar = findViewById(R.id.profileToolbar);
        saveButton.setOnClickListener(saveOnClick);
    }

    protected void updateProfileValues()
    {
        username.setHint(profile.getUsername());
        deviceName.setHint(profile.getDeviceName());
        deviceCode.setHint(profile.getDeviceCode());
        currentPassword = profile.getPassword();
    }

    private final View.OnClickListener saveOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String usernameEntered = (username.getText().length() != 0) ? username.getText().toString() : "";
            String passwordEntered = (password.getText().length() != 0) ? password.getText().toString() : "";
            String deviceNameEntered = (deviceName.getText().length() != 0) ? deviceName.getText().toString() : "";
            String deviceCodeEntered = (deviceCode.getText().length() != 0) ? deviceCode.getText().toString() : "";
            boolean error = false;

            if (usernameEntered.length() != 0 && usernameEntered.length() < 3) {
                Toast.makeText(getApplicationContext(), "Username too short", Toast.LENGTH_LONG).show();
                error = true;
            }
            if (passwordEntered.length() != 0) {
                if (passwordEntered.length() < 5) {
                    Toast.makeText(getApplicationContext(), "Password too short", Toast.LENGTH_LONG).show();
                    error = true;
                } else if (confirmPassword.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please confirm password", Toast.LENGTH_LONG).show();
                    error = true;
                } else if (!passwordEntered.equals(confirmPassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_LONG).show();
                    error = true;
                }
            }
            if (deviceCodeEntered.length() != 0) {
                if (deviceCodeEntered.length() != 17 || deviceCodeEntered.charAt(2) != ':' || deviceCodeEntered.charAt(5) != ':' || deviceCodeEntered.charAt(8) != ':' || deviceCodeEntered.charAt(11) != ':' || deviceCodeEntered.charAt(14) != ':' || !checkDeviceCode(deviceCodeEntered)) {
                    Toast.makeText(getApplicationContext(), "Invalid device code", Toast.LENGTH_LONG).show();
                    error = true;
                }
            }

            if (!error) {
                //find and edit old user values


                if(usernameEntered.length() != 0)
                {
                    firebaseHelper.updateProfileDB("username", usernameEntered);
                }
                if(passwordEntered.length() != 0)
                {
                    firebaseHelper.updateProfileDB("password", passwordEntered);
                    firebaseHelper.updateAuthentication(passwordEntered, currentPassword);

                }
                if(deviceCodeEntered.length() != 0)
                {
                    firebaseHelper.updateProfileDB("deviceCode", deviceCodeEntered);
                }
                if(deviceNameEntered.length() != 0)
                {
                    firebaseHelper.updateProfileDB("deviceName", deviceNameEntered);
                }

                setDisplayMode();
                updateProfileValues();
                menuItemTitleChange(false);
                flag = !flag;
            }
        }
    };


    // Sets up the menu option bar to show profile and logout options
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        this.menu = menu;
        return true;
    }

    // Goes to edit mode if menu option selected
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.editProfileMenuItem) {
            if (flag) { // currently on display mode
                setEditMode(); // go to Edit Mode
            } else {
                setDisplayMode();
            }
            menuItemTitleChange(flag); // Toggle menu item title
            flag = !flag;
            return true;
        } else if (item.getItemId() == R.id.logoutProfileMenuItem) {
            firebaseHelper.logout();
            goToLogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    protected void setDisplayMode() {
        username.setEnabled(false);
        password.setVisibility(View.GONE);
        confirmPassword.setVisibility(View.GONE);
        deviceName.setEnabled(false);
        deviceCode.setEnabled(false);

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

    public boolean checkDeviceCode(String addressEntered)
    {
        String[] addresses = new String[]{"EC:94:CB:4C:72:02", "EC:94:CB:4E:1E:36", "7C:9E:DB:45:43:F2", "78:E3:6D:0A:87:92"};
        for (String address :
                addresses) {
            if (address.equals(addressEntered))
            {
                return true;
            }
        }
        return false;
    }



}
package com.example.drinkingbuddy.Views;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;

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
    private String UID;
    protected String currentPassword;
    FirebaseUser user;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeComponents();
        addProfileListener();


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
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Profiles");
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

    private void addProfileListener() {
        user = firebaseAuth.getCurrentUser();
        UID = user.getUid();
        databaseReference.child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profile = snapshot.getValue(Profile.class);
                //Log.d("Firebase", value);
                if(profile == null)
                {
                    return;
                }
                username.setHint(profile.getUsername());
                deviceName.setHint(profile.getDeviceName());
                deviceCode.setHint(profile.getDeviceCode());
                currentPassword = profile.getPassword();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                if (deviceCodeEntered.length() != 17 || deviceCodeEntered.charAt(2) != ':' || deviceCodeEntered.charAt(5) != ':' || deviceCodeEntered.charAt(8) != ':' || deviceCodeEntered.charAt(11) != ':' || deviceCodeEntered.charAt(14) != ':') {
                    Toast.makeText(getApplicationContext(), "Invalid device code", Toast.LENGTH_LONG).show();
                    error = true;
                }
            }

            if (!error) {
                //find and edit old user values


                if(usernameEntered.length() != 0)
                {
                    databaseReference.child(UID).child("username").setValue(usernameEntered);
                }
                if(passwordEntered.length() != 0)
                {
                    databaseReference.child(UID).child("password").setValue(passwordEntered);
                    updateAuthentication(passwordEntered);

                }
                if(deviceCodeEntered.length() != 0)
                {
                    databaseReference.child(UID).child("deviceCode").setValue(deviceCodeEntered);
                }
                if(deviceNameEntered.length() != 0)
                {
                    databaseReference.child(UID).child("deviceName").setValue(deviceNameEntered);
                }

                //database.update(profile, sharedPreferencesHelper.getLoginId());

                setDisplayMode();
                menuItemTitleChange(false);
                flag = !flag;
            }
        }
    };

    //REFERENCE: https://writeach.com/posts/-MAOS1OT_oHIJBKqXVQZ/Firebase-Authentication---9---Change-password
    private void updateAuthentication(String newPass)
    {
        String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            user.updatePassword(newPass);
                            Toast.makeText(getApplicationContext(), "Password change successful", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

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
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    protected void logout() {
        //sharedPreferencesHelper.saveLoginId(0);
        FirebaseAuth.getInstance().signOut();
        goToLogin();
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



}
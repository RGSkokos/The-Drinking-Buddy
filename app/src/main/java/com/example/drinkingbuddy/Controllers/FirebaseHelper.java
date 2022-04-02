package com.example.drinkingbuddy.Controllers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.drinkingbuddy.Models.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//Code for firebase throughout the project was developed using these references
//REFERENCE: https://blog.mindorks.com/firebase-realtime-database-android-tutorial
//REFERENCE: https://blog.mindorks.com/firebase-login-and-authentication-android-tutorial
//REFERENCE: https://www.learnhowtoprogram.com/android/data-persistence/firebase-reading-data-and-event-listeners
//REFERENCE: https://firebase.google.com/docs/auth/android/start

public class FirebaseHelper {

    private static final String TAG = "Firebase";
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Context context;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private Profile profile;


    public FirebaseHelper(Context context) {
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Profiles");
        user = firebaseAuth.getCurrentUser();
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void logout() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null)
        {
            Log.d(TAG, user.getEmail());
        }
        FirebaseAuth.getInstance().signOut();
    }

    public String getCurrentUID()
    {
        user = firebaseAuth.getCurrentUser();
        return user.getUid();
    }

    //REFERENCE: https://writeach.com/posts/-MAOS1OT_oHIJBKqXVQZ/Firebase-Authentication---9---Change-password
    public void updateAuthentication(String newPass, String oldPass)
    {
        String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPass);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            user.updatePassword(newPass);
                            Toast.makeText(context, "Password change successful", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void authorizeUser(String emailEntered, String passwordEntered)
    {
        firebaseAuth.signInWithEmailAndPassword(emailEntered, passwordEntered)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        user = firebaseAuth.getCurrentUser();
                        firebaseAuth.updateCurrentUser(user);
                    } else
                    {
                        Toast.makeText(context, "login failed", Toast.LENGTH_LONG).show();
                        //Simply tell the user the inputted username and password is wrong
                    }
                });
    }

    public boolean ifUserLoggedIn()
    {
        //Log.d(TAG, user.getEmail());
        user = firebaseAuth.getCurrentUser();
        if(user == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public void addProfileListener() {
        String UID = getCurrentUID();
        databaseReference.child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profile = snapshot.getValue(Profile.class);
                //Log.d("Firebase", value);
                if(profile == null)
                {
                    Log.d("Firebase", "could not grab profile info");
                    return;
                }
                Log.d("Firebase", profile.getUsername() + "'s profile found");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase", "database could not be reached");
            }
        });
    }


    public void sendResetEmail(String emailEntered)
    {
        firebaseAuth.sendPasswordResetEmail(emailEntered)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Password reset email sent", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "reset password failed, please enter a valid email connected to an account", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public void CreateUser(String email, String password, Profile NewProfile)
    {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Registration successful", Toast.LENGTH_LONG).show();
                            addToDatabase(NewProfile);

                            FirebaseAuth.getInstance().signOut();
                        } else {
                            Log.d("Firebase", task.getException().getMessage());
                            Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void addToDatabase(Profile NewProfile)
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String UID = user.getUid();
        databaseReference.child(UID).setValue(NewProfile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Log.d("Firebase", "Profile added to db");
                        }
                        else
                        {
                            Log.d("Firebase", task.getException().getMessage());
                        }
                    }
                });
    }

    public void updateProfileDB(String child, String value)
    {
        databaseReference.child(user.getUid()).child(child).setValue(value);
    }

}

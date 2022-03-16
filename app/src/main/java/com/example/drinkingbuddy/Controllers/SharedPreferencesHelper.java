package com.example.drinkingbuddy.Controllers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE );
    }

    // Save login ID when logging in
    public void saveLoginId(int id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("LoginId", id);
        editor.apply();
    }

    // Get login ID to know login status
    public int getLoginId() {
        return sharedPreferences.getInt("LoginId", 0);
    }
}

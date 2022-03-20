package com.example.drinkingbuddy.Models;

public class Profile {

    //instance variables
    String username;
    String password;
    String deviceName;
    String deviceCode;

    public Profile()
    {}

    public Profile(String username, String password, String deviceName, String deviceCode) {
        this.username = username;
        this.password = password;
        this.deviceName = deviceName;
        this.deviceCode = deviceCode;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }
}

package com.example.drinkingbuddy.Models;

public class Breathalyzer {

    //instance variables
    String Result;
    String TimeStamp;

    //constructor
    public Breathalyzer(String result, String timeStamp) {
        Result = result;
        TimeStamp = timeStamp;
    }

    //getters and setters
    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }
}


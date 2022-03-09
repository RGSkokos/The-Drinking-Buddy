package com.example.drinkingbuddy.Models;

public class Breathalyzer {

    //instance variables
    String Result;
    String TimeStamp;

    public Breathalyzer(String result, String timeStamp) {
        Result = result;
        TimeStamp = timeStamp;
    }

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


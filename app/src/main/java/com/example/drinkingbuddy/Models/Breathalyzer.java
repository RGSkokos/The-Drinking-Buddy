package com.example.drinkingbuddy.Models;

public class Breathalyzer {

    //instance variables
    String Result;
    String TimeStamp;
    String DayOfWeek;



    public Breathalyzer(String result, String timeStamp, String dayOfWeek) {
        Result = result;
        TimeStamp = timeStamp;
        DayOfWeek = dayOfWeek;
    }

    //getters and setters

    public String getDayOfWeek() {
        return DayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        DayOfWeek = dayOfWeek;
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


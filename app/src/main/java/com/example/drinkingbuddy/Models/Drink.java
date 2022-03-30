package com.example.drinkingbuddy.Models;

public class Drink {

    protected String drinkName;
    protected Integer quantity;

    public Drink(String drinkName, Integer quantity) {
        this.drinkName = drinkName;
        this.quantity = quantity;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

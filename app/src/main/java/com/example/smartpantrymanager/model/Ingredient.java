package com.example.smartpantrymanager.model;

public class Ingredient implements java.io.Serializable{
    private String name;
    private double quantity;
    private String unit;
    private static final long serialVersionUID = 1L;

    public Ingredient() { }

    public String getName() {

        return name;
    }
    public double getQuantity() {

        return quantity;
    }
    public String getUnit() {

        return unit;
    }
}

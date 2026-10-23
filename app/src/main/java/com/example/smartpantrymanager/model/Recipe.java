package com.example.smartpantrymanager.model;

import java.util.List;

public class Recipe implements java.io.Serializable{
    private int id;
    private String name;
    private String steps;
    private List<Ingredient> ingredients;

    private static final long serialVersionUID = 2L;

    public Recipe() { }

    public int getId() {

        return id;
    }
    public String getName() {

        return name;
    }
    public String getSteps() {

        return steps;
    }
    public List<Ingredient> getIngredients() {

        return ingredients;
    }
}

package com.example.smartpantrymanager.logic;
import com.example.smartpantrymanager.model.Ingredient;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class RecipeMatcher {

    private RecipeMatcher() { }

    // Unit dimensions
    public static final String DIM_MASS   = "mass";    // base: g
    public static final String DIM_VOLUME = "volume";  // base: ml
    public static final String DIM_COUNT  = "count";   // base: piece
    public static final String DIM_SPOON  = "spoon";   // base: tsp

    private static final double EPSILON = 1e-9; // guards against float error


    //Returns only the recipes the user can cook right now
    public static List<Recipe> strictMatches(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, Map<String, Double>> stock = aggregatePantry(pantry);
        List<Recipe> matched = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (canMake(recipe, stock)) {
                matched.add(recipe);
            }
        }
        return matched;
    }


    public static boolean canMake(Recipe recipe, List<PantryItem> pantry) {
        return canMake(recipe, aggregatePantry(pantry));
    }


    public static int missingIngredientCount(Recipe recipe, List<PantryItem> pantry) {
        Map<String, Map<String, Double>> stock = aggregatePantry(pantry);
        int missing = 0;
        if (recipe.getIngredients() == null) return 0;
        for (Ingredient req : recipe.getIngredients()) {
            if (!hasEnough(req, stock)) missing++;
        }
        return missing;
    }


    public static String firstMissingIngredientName(Recipe recipe, List<PantryItem> pantry) {
        Map<String, Map<String, Double>> stock = aggregatePantry(pantry);
        if (recipe.getIngredients() == null) return "";
        for (Ingredient req : recipe.getIngredients()) {
            if (!hasEnough(req, stock)) return req.getName();
        }
        return "";
    }

    public static boolean hasIngredient(Ingredient req, List<PantryItem> pantry) {
        return hasEnough(req, aggregatePantry(pantry));
    }

    // Core check
    private static boolean canMake(Recipe recipe, Map<String, Map<String, Double>> stock) {
        // A recipe with no ingredients is not a meaningful suggestion.
        if (recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            return false;
        }
        for (Ingredient req : recipe.getIngredients()) {
            if (!hasEnough(req, stock)) return false;
        }
        return true;
    }

    private static boolean hasEnough(Ingredient req, Map<String, Map<String, Double>> stock) {
        String name = normaliseName(req.getName());
        String dimension = dimensionOf(req.getUnit());

        Map<String, Double> byDimension = stock.get(name);
        if (byDimension == null) return false;          // ingredient absent entirely

        Double available = byDimension.get(dimension);
        if (available == null) return false;            // present, but in the wrong unit type

        double needed = toBaseUnit(req.getQuantity(), req.getUnit());
        return available + EPSILON >= needed;
    }

    // Aggregation
    private static Map<String, Map<String, Double>> aggregatePantry(List<PantryItem> pantry) {
        Map<String, Map<String, Double>> stock = new HashMap<>();
        if (pantry == null) return stock;

        for (PantryItem item : pantry) {
            String name = normaliseName(item.getName());
            if (name.isEmpty()) continue;

            String dimension = dimensionOf(item.getUnit());
            double baseQuantity = toBaseUnit(item.getQuantity(), item.getUnit());

            Map<String, Double> byDimension = stock.get(name);
            if (byDimension == null) {
                byDimension = new HashMap<>();
                stock.put(name, byDimension);
            }
            Double running = byDimension.get(dimension);
            byDimension.put(dimension, (running == null ? 0.0 : running) + baseQuantity);
        }
        return stock;
    }

    // Name normalisation

    public static String normaliseName(String raw) {
        if (raw == null) return "";
        String s = raw.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        if (s.isEmpty()) return "";

        // tomatoes -> tomato, potatoes -> potato, heroes -> hero
        if (s.endsWith("oes")) {
            return s.substring(0, s.length() - 2);
        }
        // eggs -> egg, onions -> onion; but never "grass" -> "gras"
        if (s.length() > 2 && s.endsWith("s") && !s.endsWith("ss")) {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    // Unit handling
    public static String dimensionOf(String unit) {
        String u = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
        switch (u) {
            case "g": case "kg":                 return DIM_MASS;
            case "ml": case "l":                 return DIM_VOLUME;
            case "piece": case "pieces":         return DIM_COUNT;
            case "tsp": case "tbsp":             return DIM_SPOON;
            default:                             return "unit:" + u; // only equals itself
        }
    }

    public static double toBaseUnit(double quantity, String unit) {
        String u = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
        switch (u) {
            case "kg":   return quantity * 1000.0;  // -> g
            case "l":    return quantity * 1000.0;  // -> ml
            case "tbsp": return quantity * 3.0;     // -> tsp
            default:     return quantity;           // g, ml, piece, tsp, unknown
        }
    }
}

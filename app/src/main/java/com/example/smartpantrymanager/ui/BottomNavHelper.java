package com.example.smartpantrymanager.ui;
import android.app.Activity;
import android.content.Intent;

import com.example.smartpantrymanager.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public final class BottomNavHelper {

    private BottomNavHelper() { }

    public static void setup(BottomNavigationView nav, Activity activity, int currentItemId) {
        nav.setSelectedItemId(currentItemId);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == currentItemId) return true;

            Class<?> target;
            if (id == R.id.nav_pantry)        target = PantryListActivity.class;
            else if (id == R.id.nav_recipes)  target = SuggestedRecipesActivity.class;
            else if (id == R.id.nav_settings) target = SettingsActivity.class;
            else return false;

            Intent intent = new Intent(activity, target);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activity.startActivity(intent);
            return true;
        });
    }
}
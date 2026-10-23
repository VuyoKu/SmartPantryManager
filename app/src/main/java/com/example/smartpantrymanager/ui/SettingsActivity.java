package com.example.smartpantrymanager.ui;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantrymanager.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

public class SettingsActivity extends AppCompatActivity {

    static final String PREFS       = "smart_pantry_prefs";
    static final String KEY_ALERTS  = "expiring_alerts";
    static final String KEY_UNIT    = "default_unit";

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        MaterialSwitch alertsSwitch = findViewById(R.id.swAlerts);
        alertsSwitch.setChecked(prefs.getBoolean(KEY_ALERTS, true));
        alertsSwitch.setOnCheckedChangeListener((v, checked) -> prefs.edit().putBoolean(KEY_ALERTS, checked).apply());

        //TextInputLayout tilUnit = findViewById(R.id.tilDefaultUnit);
        MaterialAutoCompleteTextView dropdown = findViewById(R.id.dropdownDefaultUnit);
        dropdown.setSimpleItems(getResources().getStringArray(R.array.units));
        dropdown.setText(prefs.getString(KEY_UNIT, "piece"), false);
        dropdown.setOnItemClickListener((parent, view, position, id) -> prefs.edit().putString(KEY_UNIT, dropdown.getText().toString()).apply());

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        BottomNavHelper.setup(nav, this, R.id.nav_settings);
    }
}

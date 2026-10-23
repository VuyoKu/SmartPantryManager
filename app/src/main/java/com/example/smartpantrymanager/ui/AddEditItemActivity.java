package com.example.smartpantrymanager.ui;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.network.ApiClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditItemActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM = "extra_item";

    private TextInputLayout tilName, tilQuantity, tilUnit;
    private TextInputEditText etName, etQuantity, etExpiry;
    private MaterialAutoCompleteTextView dropdownUnit;
    private MaterialToolbar toolbar;

    private PantryItem editing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_item);

        toolbar      = findViewById(R.id.toolbar);
        tilName      = findViewById(R.id.tilName);
        tilQuantity  = findViewById(R.id.tilQuantity);
        tilUnit      = findViewById(R.id.tilUnit);
        etName       = findViewById(R.id.etName);
        etQuantity   = findViewById(R.id.etQuantity);
        etExpiry     = findViewById(R.id.etExpiry);
        dropdownUnit = findViewById(R.id.dropdownUnit);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        Object extra = getIntent().getSerializableExtra(EXTRA_ITEM);
        if (extra instanceof PantryItem) {
            editing = (PantryItem) extra;
            toolbar.setTitle(R.string.edit_item_title);
            prefill();
        }

        setupUnitDropdown();
        setupDatePicker();
    }

    private void prefill() {
        etName.setText(editing.getName());
        etQuantity.setText(editing.getQuantity() == (long) editing.getQuantity() ? String.valueOf((long) editing.getQuantity()) : String.valueOf(editing.getQuantity()));
        dropdownUnit.setText(editing.getUnit(), false);
        if (editing.getExpiryDate() != null) {
            etExpiry.setText(editing.getExpiryDate());
        }
    }

    private void setupUnitDropdown() {
        String[] units = getResources().getStringArray(R.array.units);
        dropdownUnit.setSimpleItems(units);
    }

    private void setupDatePicker() {
        etExpiry.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (picker, year, month, day) -> {
                Calendar picked = Calendar.getInstance();
                picked.set(year, month, day);
                etExpiry.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(picked.getTime()));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            if (validate()) save();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validate() {
        tilName.setError(null);
        tilQuantity.setError(null);
        tilUnit.setError(null);

        String name   = text(etName);
        String qtyStr = text(etQuantity);
        String unit   = text(dropdownUnit.getText());
        boolean ok = true;

        if (name.isEmpty()) {
            tilName.setError("Name is required");
            ok = false;
        } else if (name.length() < 2) {
            tilName.setError("Name must be at least 2 characters");
            ok = false;
        }

        if (qtyStr.isEmpty()) {
            tilQuantity.setError("Quantity is required");
            ok = false;
        } else {
            try {
                double q = Double.parseDouble(qtyStr);
                if (q <= 0) {
                    tilQuantity.setError("Quantity must be greater than 0");
                    ok = false;
                } else if (q > 100000) {
                    tilQuantity.setError("That quantity is unrealistically large");
                    ok = false;
                }
            } catch (NumberFormatException e) {
                tilQuantity.setError("Enter a valid number");
                ok = false;
            }
        }

        if (unit.isEmpty()) {
            tilUnit.setError("Choose a unit");
            ok = false;
        }

        return ok;
    }

    private void save() {
        PantryItem item = new PantryItem();
        item.setId(editing != null ? editing.getId() : 0);
        //item.setName(text(etName).toLowerCase(Locale.ROOT));
        item.setName(text(etName));
        item.setQuantity(Double.parseDouble(text(etQuantity)));
        item.setUnit(text(dropdownUnit.getText()));
        String expiry = text(etExpiry);
        item.setExpiryDate(expiry.isEmpty() ? null : expiry);

        setSaveEnabled(false);
        Call<PantryItem> call = editing == null
                ? ApiClient.get().addPantryItem(item)
                : ApiClient.get().updatePantryItem(item.getId(), item);

        call.enqueue(new Callback<PantryItem>() {
            @Override public void onResponse(Call<PantryItem> c, Response<PantryItem> r) {
                if (r.isSuccessful()) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    setSaveEnabled(true);
                    Toast.makeText(AddEditItemActivity.this,
                            "Server error: " + r.code(), Toast.LENGTH_LONG).show();
                }
            }
            @Override public void onFailure(Call<PantryItem> c, Throwable t) {
                setSaveEnabled(true);
                Toast.makeText(AddEditItemActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setSaveEnabled(boolean enabled) {
        if (toolbar.getMenu() != null && toolbar.getMenu().findItem(R.id.action_save) != null) {
            toolbar.getMenu().findItem(R.id.action_save).setEnabled(enabled);
        }
    }

    private static String text(android.widget.EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private static String text(CharSequence cs) {

        return cs == null ? "" : cs.toString().trim();
    }
}

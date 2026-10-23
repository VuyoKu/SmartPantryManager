package com.example.smartpantrymanager.ui;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PantryListActivity extends AppCompatActivity {

    private PantryAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyState;
    private ProgressBar progressBar;
    private TextView summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        recyclerView = findViewById(R.id.recyclerPantry);
        emptyState = findViewById(R.id.emptyState);
        progressBar = findViewById(R.id.progressBar);
        summary = findViewById(R.id.pantrySummary);

        adapter = new PantryAdapter(this::openEditor, this::confirmDelete);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabAdd).setOnClickListener(v ->
                startActivity(new android.content.Intent(this, AddEditItemActivity.class)));

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        BottomNavHelper.setup(nav, this, R.id.nav_pantry);
    }

    private void openEditor(PantryItem item) {
        Intent intent = new Intent(this, AddEditItemActivity.class);
        intent.putExtra(AddEditItemActivity.EXTRA_ITEM, item);
        startActivity(intent);
    }

    private void confirmDelete(PantryItem item) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete " + pretty(item.getName()) + "?")
                .setMessage("This removes it from your pantry.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> deleteItem(item))
                .show();
    }

    private void deleteItem(PantryItem item) {
        ApiClient.get().deletePantryItem(item.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // 204 = deleted, 404 = already gone — either way, reload.
                if (response.isSuccessful() || response.code() == 404) {
                    loadPantry();
                } else {
                    Toast.makeText(PantryListActivity.this, "Server error: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PantryListActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private static String pretty(String s) {
        if (s == null || s.isEmpty()) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantry(); // reload whenever we come back — e.g. after add/edit/delete
    }

    private void loadPantry() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        ApiClient.get().getPantry().enqueue(new Callback<List<PantryItem>>() {
            @Override
            public void onResponse(Call<List<PantryItem>> call, Response<List<PantryItem>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setItems(response.body());
                    updateUi(response.body().size());
                } else {
                    Toast.makeText(PantryListActivity.this, "Server error: " + response.code(), Toast.LENGTH_LONG).show();
                    updateUi(0);
                }
            }

            @Override
            public void onFailure(Call<List<PantryItem>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PantryListActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                updateUi(0);
            }
        });
    }

    private void updateUi(int count) {
        summary.setText(count + (count == 1 ? " item" : " items"));
        if (count == 0) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }
}

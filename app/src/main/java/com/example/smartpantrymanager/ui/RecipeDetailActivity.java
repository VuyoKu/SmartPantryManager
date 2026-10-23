package com.example.smartpantrymanager.ui;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.network.ApiClient;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE = "extra_recipe";

    private IngredientChecklistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        Recipe recipe = (Recipe) getIntent().getSerializableExtra(EXTRA_RECIPE);
        if (recipe == null) {
            finish();
            return;
        }

        TextView tvName  = findViewById(R.id.tvRecipeName);
        TextView tvSteps = findViewById(R.id.tvSteps);
        RecyclerView recycler = findViewById(R.id.recyclerIngredients);

        tvName.setText(recipe.getName());

        String steps = recipe.getSteps() == null ? "" : recipe.getSteps();
        tvSteps.setText(steps.replaceAll("(\\d+\\.)", "\n$1").trim());

        adapter = new IngredientChecklistAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        adapter.setData(recipe.getIngredients(), Collections.emptyList());

        loadPantry(recipe);
    }

    private void loadPantry(Recipe recipe) {
        ApiClient.get().getPantry().enqueue(new Callback<List<PantryItem>>() {
            @Override
            public void onResponse(Call<List<PantryItem>> c, Response<List<PantryItem>> r) {
                if (r.isSuccessful() && r.body() != null) {
                    adapter.setData(recipe.getIngredients(), r.body());
                } else {
                    Toast.makeText(RecipeDetailActivity.this, "Could not load pantry", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PantryItem>> c, Throwable t) {
                Toast.makeText(RecipeDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

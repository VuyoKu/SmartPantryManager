package com.example.smartpantrymanager.ui;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.logic.RecipeMatcher;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.network.ApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private RecipeAdapter strictAdapter;
    private RecipeAdapter almostAdapter;
    private RecyclerView recyclerStrict, recyclerAlmost;
    private TextView subtitle, strictHeader, almostHeader;
    private NestedScrollView scrollView;
    private View emptyState;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationOnClickListener(v -> finish());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        subtitle     = findViewById(R.id.subtitle);
        strictHeader = findViewById(R.id.strictHeader);
        almostHeader = findViewById(R.id.almostHeader);
        scrollView   = findViewById(R.id.scrollView);
        emptyState   = findViewById(R.id.emptyState);
        progressBar  = findViewById(R.id.progressBar);
        recyclerStrict = findViewById(R.id.recyclerStrict);
        recyclerAlmost = findViewById(R.id.recyclerAlmost);

        strictAdapter = new RecipeAdapter(false, this::openDetail);
        almostAdapter = new RecipeAdapter(true, this::openDetail);

        recyclerStrict.setLayoutManager(new LinearLayoutManager(this));
        recyclerStrict.setAdapter(strictAdapter);
        recyclerAlmost.setLayoutManager(new LinearLayoutManager(this));
        recyclerAlmost.setAdapter(almostAdapter);

        load();
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        BottomNavHelper.setup(nav, this, R.id.nav_recipes);
    }

    private void load() {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);

        // fetches pantry, then fetches recipes, then matches them.
        ApiClient.get().getPantry().enqueue(new Callback<List<PantryItem>>() {
            @Override
            public void onResponse(Call<List<PantryItem>> c, Response<List<PantryItem>> r) {
                if (!r.isSuccessful() || r.body() == null) {
                    showError(r.code());
                    return;
                }
                fetchRecipes(r.body());
            }
            @Override
            public void onFailure(Call<List<PantryItem>> c, Throwable t) {
                showNetworkError(t);
            }
        });
    }

    private void fetchRecipes(List<PantryItem> pantry) {
        ApiClient.get().getRecipes().enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> c, Response<List<Recipe>> r) {
                progressBar.setVisibility(View.GONE);
                if (!r.isSuccessful() || r.body() == null) {
                    showError(r.code());
                    return;
                }
                display(pantry, r.body());
            }
            @Override
            public void onFailure(Call<List<Recipe>> c, Throwable t) {
                showNetworkError(t);
            }
        });
    }

    private void display(List<PantryItem> pantry, List<Recipe> allRecipes) {
        // The strict rule matching
        List<Recipe> strict = RecipeMatcher.strictMatches(allRecipes, pantry);

        // when exactly one ingredient missing.
        List<Recipe> almost = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (RecipeMatcher.missingIngredientCount(recipe, pantry) == 1) {
                almost.add(recipe);
            }
        }

        subtitle.setText(pantry.isEmpty() ? "Your pantry is empty" : "Based on " + pantry.size() + (pantry.size() == 1 ? " ingredient" : " ingredients"));

        if (strict.isEmpty() && almost.isEmpty()) {
            scrollView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
            return;
        }

        scrollView.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        if (strict.isEmpty()) {
            strictHeader.setText("No recipes ready yet");
            recyclerStrict.setVisibility(View.GONE);
        } else {
            strictHeader.setText("Ready to cook · " + strict.size());
            recyclerStrict.setVisibility(View.VISIBLE);
            strictAdapter.setData(strict, pantry);
        }

        if (almost.isEmpty()) {
            almostHeader.setVisibility(View.GONE);
            recyclerAlmost.setVisibility(View.GONE);
        } else {
            almostHeader.setVisibility(View.VISIBLE);
            almostHeader.setText("Almost there · " + almost.size());
            recyclerAlmost.setVisibility(View.VISIBLE);
            almostAdapter.setData(almost, pantry);
        }
    }

    private void openDetail(Recipe recipe) {
        android.content.Intent intent = new android.content.Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE, recipe);
        startActivity(intent);
    }

    private void showError(int code) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Server error: " + code, Toast.LENGTH_LONG).show();
    }

    private void showNetworkError(Throwable t) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
    }
}

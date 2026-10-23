package com.example.smartpantrymanager.ui;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.logic.RecipeMatcher;
import com.example.smartpantrymanager.model.Ingredient;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;

import java.util.ArrayList;
import java.util.List;


public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private List<PantryItem> pantry = new ArrayList<>();
    private final boolean showMissing;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(boolean showMissing, OnRecipeClickListener listener) {
        this.showMissing = showMissing;
        this.listener = listener;
    }

    public void setData(List<Recipe> items, List<PantryItem> currentPantry) {
        recipes.clear();
        if (items != null) recipes.addAll(items);
        pantry = (currentPantry == null) ? new ArrayList<>() : currentPantry;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.tvName.setText(recipe.getName());

        if (showMissing) {
            String missing = RecipeMatcher.firstMissingIngredientName(recipe, pantry);
            holder.tvDetail.setText(missing.isEmpty() ? "" : "Missing: " + missing);
        } else {
            holder.tvDetail.setText(joinNames(recipe.getIngredients()));
        }

        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    private static String joinNames(List<Ingredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("Needs: ");
        for (int i = 0; i < ingredients.size(); i++) {
            if (i > 0) sb.append(" · ");
            sb.append(ingredients.get(i).getName());
        }
        return sb.toString();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName, tvDetail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDetail = itemView.findViewById(R.id.tvDetail);
        }
    }
}
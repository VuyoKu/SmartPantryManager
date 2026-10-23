package com.example.smartpantrymanager.ui;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.logic.RecipeMatcher;
import com.example.smartpantrymanager.model.Ingredient;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.ArrayList;
import java.util.List;


public class IngredientChecklistAdapter
        extends RecyclerView.Adapter<IngredientChecklistAdapter.ViewHolder> {

    private final List<Ingredient> ingredients = new ArrayList<>();
    private List<PantryItem> pantry = new ArrayList<>();

    public void setData(List<Ingredient> items, List<PantryItem> currentPantry) {
        ingredients.clear();
        if (items != null) ingredients.addAll(items);
        pantry = (currentPantry == null) ? new ArrayList<>() : currentPantry;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient_checklist, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ing = ingredients.get(position);
        boolean available = RecipeMatcher.hasIngredient(ing, pantry);

        holder.tvName.setText(capitalise(ing.getName()));
        holder.iconStatus.setImageResource(available ? R.drawable.ic_check_circle : R.drawable.ic_circle_empty);

        String amount = formatQuantity(ing.getQuantity()) + " " + ing.getUnit();
        holder.tvDetail.setText(available ? amount + " · In pantry" : amount + " · Missing");
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    private static String capitalise(String s) {
        if (s == null || s.isEmpty()) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String formatQuantity(double q) {
        return q == (long) q ? String.valueOf((long) q) : String.valueOf(q);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView iconStatus;
        final TextView tvName, tvDetail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconStatus = itemView.findViewById(R.id.iconStatus);
            tvName = itemView.findViewById(R.id.tvIngredientName);
            tvDetail = itemView.findViewById(R.id.tvIngredientDetail);
        }
    }
}

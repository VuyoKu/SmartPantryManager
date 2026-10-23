package com.example.smartpantrymanager.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.PantryItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final OnItemClickListener clickListener;
    private final OnItemLongClickListener longClickListener;

    public PantryAdapter(OnItemClickListener clickListener,
                         OnItemLongClickListener longClickListener) {
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public void setItems(List<PantryItem> newItems) {
        items.clear();
        if (newItems != null) items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), clickListener, longClickListener);
    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvQuantity, tvExpiry;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
        }

        void bind(PantryItem item, OnItemClickListener click, OnItemLongClickListener longClick) {tvName.setText(capitalise(item.getName()));tvQuantity.setText(formatQuantity(item.getQuantity()) + " " + pluraliseUnit(item.getUnit(), item.getQuantity()));

            if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
                tvExpiry.setVisibility(View.VISIBLE);
                tvExpiry.setText(prettyDate(item.getExpiryDate()));
            } else {
                tvExpiry.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (click != null) click.onItemClick(item);
            });
            itemView.setOnLongClickListener(v -> {
                if (longClick != null) {
                    longClick.onItemLongClick(item);
                    return true;
                }
                return false;
            });
        }

        private static String capitalise(String s) {
            if (s == null || s.isEmpty()) return "";
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }

        private static String formatQuantity(double q) {
            return q == (long) q ? String.valueOf((long) q) : String.valueOf(q);
        }

        private static String pluraliseUnit(String unit, double qty) {
            if (unit == null) return "";
            return unit.equals("piece") && qty != 1 ? "pieces" : unit;
        }

        private static String prettyDate(String iso) {
            try {
                Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(iso);
                return d == null ? iso
                        : new SimpleDateFormat("d MMM", Locale.getDefault()).format(d);
            } catch (ParseException e) {
                return iso;
            }
        }
    }
}
package com.example.ewasteapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecycleHistoryAdapter extends RecyclerView.Adapter<RecycleHistoryAdapter.ViewHolder> {

    private List<RecycleItem> recycleItems;

    public RecycleHistoryAdapter(List<RecycleItem> recycleItems) {
        this.recycleItems = recycleItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycle_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecycleItem item = recycleItems.get(position);
        holder.tvItemCode.setText(item.getItemCode());
        holder.tvCategory.setText(item.getCategory());
        holder.tvWeight.setText(item.getWeight());
    }

    @Override
    public int getItemCount() {
        return recycleItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemCode;
        TextView tvCategory;
        TextView tvWeight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemCode = itemView.findViewById(R.id.tvItemCode);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvWeight = itemView.findViewById(R.id.tvWeight);
        }
    }
}

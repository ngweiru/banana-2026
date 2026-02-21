package com.example.ewasteapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private List<LeaderboardUser> users;

    public LeaderboardAdapter(List<LeaderboardUser> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardUser user = users.get(position);

        // Format rank
        String rankText = String.format("%02d", user.getRank());
        holder.tvRank.setText(rankText);

        holder.tvName.setText(user.getName());

        // Format weight
        String weightText = String.format("%.1f KG", user.getWeight());
        holder.tvWeight.setText(weightText);

        // TODO: Load profile image from URL
        // placeholder
        holder.ivProfile.setImageResource(R.drawable.ic_profile_placeholder);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank;
        ImageView ivProfile;
        TextView tvName;
        TextView tvWeight;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvWeight = itemView.findViewById(R.id.tvWeight);
        }
    }
}
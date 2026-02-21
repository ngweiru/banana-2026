package com.example.ewasteapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends BaseActivity {

    // Top 3 views
    private ImageView ivProfile1, ivProfile2, ivProfile3;
    private TextView tvName1, tvName2, tvName3;
    private TextView tvWeight1, tvWeight2, tvWeight3;

    // Your Ranking views
    private TextView tvYourRank;
    private ImageView ivYourProfile;
    private TextView tvYourWeight;

    // RecyclerView for ranks 4 and above
    private RecyclerView recyclerViewLeaderboard;
    private LeaderboardAdapter adapter;
    private List<LeaderboardUser> users;

    // Bottom navigation
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // Initialize views
        initializeViews();

        // Load sample data
        loadLeaderboardData();

        // Display top 3
        displayTop3();

        // Setup RecyclerView for ranks 4 and above
        setupRecyclerView();

        // Display your ranking
        displayYourRanking();

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void initializeViews() {
        // Top 3 profiles
        ivProfile1 = findViewById(R.id.ivProfile1);
        ivProfile2 = findViewById(R.id.ivProfile2);
        ivProfile3 = findViewById(R.id.ivProfile3);

        // Top 3 names
        tvName1 = findViewById(R.id.tvName1);
        tvName2 = findViewById(R.id.tvName2);
        tvName3 = findViewById(R.id.tvName3);

        // Top 3 weights
        tvWeight1 = findViewById(R.id.tvWeight1);
        tvWeight2 = findViewById(R.id.tvWeight2);
        tvWeight3 = findViewById(R.id.tvWeight3);

        // Your Ranking
        tvYourRank = findViewById(R.id.tvYourRank);
        ivYourProfile = findViewById(R.id.ivYourProfile);
        tvYourWeight = findViewById(R.id.tvYourWeight);

        // RecyclerView
        recyclerViewLeaderboard = findViewById(R.id.recyclerViewLeaderboard);

        // Bottom navigation
        bottomNavigation = findViewById(R.id.bottomNavigationView);
    }

    private void loadLeaderboardData() {
        users = new ArrayList<>();

        users.add(new LeaderboardUser(1, "Ahmad Faiz", 18.5, ""));
        users.add(new LeaderboardUser(2, "Siti Noorhaliza", 17.2, ""));
        users.add(new LeaderboardUser(3, "Lee Zheng Wei", 16.8, ""));
        users.add(new LeaderboardUser(4, "Nurul Isshah", 15.3, ""));
        users.add(new LeaderboardUser(5, "Muhammad Hafiz", 14.7, ""));
        users.add(new LeaderboardUser(6, "Tan Siu Kuan", 14.2, ""));
        users.add(new LeaderboardUser(7, "Fatimah Zaleha", 13.9, ""));
        users.add(new LeaderboardUser(8, "Rajesh Kumar", 13.5, ""));
        users.add(new LeaderboardUser(9, "Wong Mei Ling", 13.1, ""));
        users.add(new LeaderboardUser(10, "Amir Haikal", 12.8, ""));
        users.add(new LeaderboardUser(11, "Nadia Binti Ali", 12.5, ""));
        users.add(new LeaderboardUser(12, "Lim Wei Jun", 12.2, ""));
        users.add(new LeaderboardUser(13, "Cassandra Chee", 11.9, ""));
        users.add(new LeaderboardUser(14, "Chong Kar Yan", 11.5, ""));
        users.add(new LeaderboardUser(15, "Zainal Mohd", 11.2, ""));
    }

    private void displayTop3() {
        if (users.size() >= 3) {
            // 1st place
            LeaderboardUser first = users.get(0);
            tvName1.setText(first.getName());
            tvWeight1.setText(String.format("%.1f KG", first.getWeight()));

            // 2nd place
            LeaderboardUser second = users.get(1);
            tvName2.setText(second.getName());
            tvWeight2.setText(String.format("%.1f KG", second.getWeight()));

            // 3rd place
            LeaderboardUser third = users.get(2);
            tvName3.setText(third.getName());
            tvWeight3.setText(String.format("%.1f KG", third.getWeight()));
        }
    }

    private void setupRecyclerView() {
        // Get users from rank 4 onwards
        List<LeaderboardUser> lowerRanks = new ArrayList<>();
        if (users.size() > 3) {
            lowerRanks = users.subList(3, users.size());
        }

        adapter = new LeaderboardAdapter(lowerRanks);
        recyclerViewLeaderboard.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLeaderboard.setAdapter(adapter);
    }

    private void displayYourRanking() {
        // TODO: Get actual user's ranking from Firebase/backend
        // Sample data
        int yourRank = 24;
        double yourWeight = 8.2;

        tvYourRank.setText(String.format("%02d", yourRank));
        tvYourWeight.setText(String.format("%.1f KG", yourWeight));
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_leaderboard);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(LeaderboardActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_leaderboard) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Navigate to Profile
                Intent intent = new Intent(LeaderboardActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });
    }
}
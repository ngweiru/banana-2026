package com.example.ewasteapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends BaseActivity {

    private RecyclerView recyclerViewHistory;
    private RecycleHistoryAdapter adapter;
    private List<RecycleItem> recycleItems;
    private FrameLayout btnIdentifyItem;
    private FrameLayout btnFindLocation;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        initializeViews();

        // Set up RecyclerView
        setupRecyclerView();

        // Set up click listeners
        setupClickListeners();

        // Set up bottom navigation
        setupBottomNavigation();
    }

    private void initializeViews() {
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        btnIdentifyItem = findViewById(R.id.btnIdentifyItem);
        btnFindLocation = findViewById(R.id.btnFindLocation);
        bottomNavigation = findViewById(R.id.bottomNavigationView);
    }

    private void setupRecyclerView() {
        recycleItems = new ArrayList<>();
        recycleItems.add(new RecycleItem("iPhone 12 Pro", "Electronics • Feb 14", "174g"));
        recycleItems.add(new RecycleItem("MacBook Pro 2019", "Laptop • Feb 13", "1.4kg"));
        recycleItems.add(new RecycleItem("Samsung Galaxy S21", "Smartphone • Feb 12", "169g"));
        recycleItems.add(new RecycleItem("Dell Monitor", "Display • Feb 11", "4.2kg"));
        recycleItems.add(new RecycleItem("AirPods Pro", "Earphones • Feb 8", "56g"));

        adapter = new RecycleHistoryAdapter(recycleItems);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHistory.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Identify Item button click
        btnIdentifyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Scanner Activity
                Intent intent = new Intent(DashboardActivity.this, ScannerActivity.class);
                startActivity(intent);
            }
        });

        // Find Location button click
        btnFindLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_leaderboard) {
                // Navigate to Leaderboard
                Intent intent = new Intent(DashboardActivity.this, LeaderboardActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Navigate to Profile
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });
    }
}

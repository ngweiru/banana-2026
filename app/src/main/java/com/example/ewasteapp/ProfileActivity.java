package com.example.ewasteapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends BaseActivity {

    // Views
    private ImageButton btnEdit;
    private ImageView ivProfilePicture;
    private CardView btnChangePhoto;
    private TextView tvProfileName;
    private TextView tvProfileEmail;
    private TextView tvTotalWeight;
    private TextView tvRank;
    private TextView tvItemsScanned;

    // Settings buttons
    private CardView btnEditProfile;
    private CardView btnNotifications;
    private CardView btnPrivacy;
    private CardView btnHelp;
    private CardView btnThemeToggle;

    // Theme switch
    private SwitchCompat themeSwitch;

    // SharedPreferences for theme
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ThemePrefs";
    private static final String KEY_DARK_MODE = "darkMode";

    // Bottom navigation
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadThemePreference();

        setContentView(R.layout.activity_profile);

        // Initialize views
        initializeViews();

        // Load user data
        loadUserData();

        // Setup click listeners
        setupClickListeners();

        // Setup theme toggle
        setupThemeToggle();

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void initializeViews() {
        btnEdit = findViewById(R.id.btnEdit);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvTotalWeight = findViewById(R.id.tvTotalWeight);
        tvRank = findViewById(R.id.tvRank);
        tvItemsScanned = findViewById(R.id.tvItemsScanned);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnPrivacy = findViewById(R.id.btnPrivacy);
        btnHelp = findViewById(R.id.btnHelp);
        btnThemeToggle = findViewById(R.id.btnThemeToggle);
        themeSwitch = findViewById(R.id.themeSwitch);

        bottomNavigation = findViewById(R.id.bottomNavigationView);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private void loadUserData() {
        // TODO: Load from Firebase/SharedPreferences
        // Sample data
        String userName = "Mary Wong";
        String userEmail = "mary@gmail.com";
        double totalWeight = 8.2;
        int rank = 24;
        int itemsScanned = 12;

        tvProfileName.setText(userName);
        tvProfileEmail.setText(userEmail);
        tvTotalWeight.setText(String.format("%.1f KG", totalWeight));
        tvRank.setText(String.format("#%d", rank));
        tvItemsScanned.setText(String.valueOf(itemsScanned));
    }

    private void setupClickListeners() {
        // Edit button
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditProfile();
            }
        });

        // Change photo button
        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfilePhoto();
            }
        });

        // Edit Profile
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditProfile();
            }
        });

        // Notifications
        btnNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this,
                        "Notifications settings", Toast.LENGTH_SHORT).show();
                // TODO: Open NotificationsActivity
            }
        });

        // Privacy
        btnPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this,
                        "Privacy settings", Toast.LENGTH_SHORT).show();
                // TODO: Open PrivacyActivity
            }
        });

        // Help
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this,
                        "Help and Support", Toast.LENGTH_SHORT).show();
                // TODO: Open HelpActivity
            }
        });
    }

    private void setupThemeToggle() {
        boolean isDarkMode = sharedPreferences.getBoolean(KEY_DARK_MODE, false);
        themeSwitch.setChecked(isDarkMode);

        // Handle switch change
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_DARK_MODE, isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            recreate();
        });
    }

    private void loadThemePreference() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean(KEY_DARK_MODE, false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_profile);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(ProfileActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_leaderboard) {
                Intent intent = new Intent(ProfileActivity.this, LeaderboardActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Already on profile
                return true;
            }

            return false;
        });
    }

    private void openEditProfile() {
        Toast.makeText(this, "Edit Profile", Toast.LENGTH_SHORT).show();
        // TODO: Open EditProfileActivity
        // Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        // startActivity(intent);
    }

    private void changeProfilePhoto() {
        Toast.makeText(this, "Change Profile Photo", Toast.LENGTH_SHORT).show();
        // TODO: Open image picker
        // Intent intent = new Intent(Intent.ACTION_PICK);
        // intent.setType("image/*");
        // startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
}
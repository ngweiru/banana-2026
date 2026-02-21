package com.example.ewasteapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MapActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etSearchLocation;
    private ImageButton btnSearch;
    private ImageButton btnZoomIn;
    private ImageButton btnZoomOut;
    private CardView bottomSheet;
    private BottomSheetBehavior<CardView> bottomSheetBehavior;
    private TextView tvLocationName;
    private TextView tvDistance;
    private TextView tvAddress;
    private TextView tvPhone;
    private TextView tvHours;
    private Button btnNavigate;
    private ImageButton btnCall;

    // Sample data
    private String locationName = "GreenTech Recycling Center";
    private String distance = "21 min (15 km)";
    private String address = "Jalan Teknologi 3/1, Kota Damansara, 47810 Petaling Jaya, Selangor";
    private String phoneNumber = "+60123456789";
    private String hours = "Mon-Sat: 9:00 AM - 6:00 PM\nSunday: Closed";
    private double latitude = 3.0738;
    private double longitude = 101.5183;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initializeViews();
        setupBottomSheet();
        displayLocationData();
        setupClickListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        etSearchLocation = findViewById(R.id.etSearchLocation);
        btnSearch = findViewById(R.id.btnSearch);
        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomOut = findViewById(R.id.btnZoomOut);
        bottomSheet = findViewById(R.id.bottomSheet);
        tvLocationName = findViewById(R.id.tvLocationName);
        tvDistance = findViewById(R.id.tvDistance);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvHours = findViewById(R.id.tvHours);
        btnNavigate = findViewById(R.id.btnNavigate);
        btnCall = findViewById(R.id.btnCall);
    }

    private void setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(500);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void displayLocationData() {
        Intent intent = getIntent();
        if (intent.hasExtra("locationName")) {
            locationName = intent.getStringExtra("locationName");
        }
        if (intent.hasExtra("distance")) {
            distance = intent.getStringExtra("distance");
        }
        if (intent.hasExtra("address")) {
            address = intent.getStringExtra("address");
        }
        if (intent.hasExtra("phoneNumber")) {
            phoneNumber = intent.getStringExtra("phoneNumber");
        }
        if (intent.hasExtra("hours")) {
            hours = intent.getStringExtra("hours");
        }
        if (intent.hasExtra("latitude")) {
            latitude = intent.getDoubleExtra("latitude", 3.0738);
        }
        if (intent.hasExtra("longitude")) {
            longitude = intent.getDoubleExtra("longitude", 101.5183);
        }

        tvLocationName.setText(locationName);
        tvDistance.setText(distance);
        tvAddress.setText(address);
        tvPhone.setText(phoneNumber);
        tvHours.setText(hours);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = etSearchLocation.getText().toString();
                if (!searchQuery.isEmpty()) {
                    // TODO: Implement search
                }
            }
        });

        btnZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement zoom in
            }
        });

        btnZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement zoom out
            }
        });

        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMapsNavigation();
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoneDialer();
            }
        });
    }

    private void openGoogleMapsNavigation() {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Uri browserUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" +
                    latitude + "," + longitude);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
            startActivity(browserIntent);
        }
    }

    private void openPhoneDialer() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
}
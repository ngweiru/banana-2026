package com.example.ewasteapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultsActivity extends BaseActivity {

    private ImageButton btnBack;
    private ImageView ivScannedImage;
    private TextView tvDeviceName;
    private TextView tvModelInfo;
    private TextView tvRecyclableComponents;
    private TextView tvHazardousMaterials;
    private TextView tvRecommendations;
    private TextView tvImpact;
    private LinearLayout btnFindLocation;

    // Data from scanner
    private String deviceName;
    private String modelId;
    private double weight;
    private String recyclableComponents;
    private String recycleInstructions;
    private Bitmap scannedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        initializeViews();
        getIntentData();
        displayResults();
        setupClickListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        ivScannedImage = findViewById(R.id.ivScannedImage);
        tvDeviceName = findViewById(R.id.tvDeviceName);
        tvModelInfo = findViewById(R.id.tvModelInfo);
        tvRecyclableComponents = findViewById(R.id.tvRecyclableComponents);
        tvHazardousMaterials = findViewById(R.id.tvHazardousMaterials);
        tvRecommendations = findViewById(R.id.tvRecommendations);
        tvImpact = findViewById(R.id.tvImpact);
        btnFindLocation = findViewById(R.id.btnFindLocation);
    }

    private void getIntentData() {
        Intent intent = getIntent();

        deviceName = intent.getStringExtra("deviceName");
        modelId = intent.getStringExtra("modelId");
        weight = intent.getDoubleExtra("weight", 0.0);
        recyclableComponents = intent.getStringExtra("recyclableComponents");
        recycleInstructions = intent.getStringExtra("recycleInstructions");

        if (intent.hasExtra("imageBytes")) {
            byte[] byteArray = intent.getByteArrayExtra("imageBytes");
            scannedImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }

        if (deviceName == null || deviceName.isEmpty()) deviceName = "Unknown Device";
        if (modelId == null || modelId.isEmpty()) modelId = "Unknown";
        if (recyclableComponents == null || recyclableComponents.isEmpty()) recyclableComponents = "No information available";
        if (recycleInstructions == null || recycleInstructions.isEmpty()) recycleInstructions = "No recycling information available";
    }

    private void displayResults() {
        tvDeviceName.setText(deviceName);
        tvModelInfo.setText("Model ID: " + modelId + " | " + weight + " kg");
        tvRecyclableComponents.setText(recyclableComponents);
        parseRecycleInstructions(recycleInstructions);

        if (scannedImage != null) {
            ivScannedImage.setImageBitmap(scannedImage);
        }
    }

    private void parseRecycleInstructions(String instructions) {
        String hazardous = "";
        String recommendations = "";
        String impact = "";

        if (instructions.contains("Hazardous Materials:")) {
            int start = instructions.indexOf("Hazardous Materials:") + "Hazardous Materials:".length();
            int end = instructions.indexOf("Recommendations:");
            if (end == -1) end = instructions.length();
            hazardous = instructions.substring(start, end).trim();
        }

        if (instructions.contains("Recommendations:")) {
            int start = instructions.indexOf("Recommendations:") + "Recommendations:".length();
            int end = instructions.indexOf("Environmental Impact:");
            if (end == -1) {
                end = instructions.indexOf("Impact:");
                if (end == -1) end = instructions.length();
            }
            recommendations = instructions.substring(start, end).trim();
        }

        if (instructions.contains("Environmental Impact:")) {
            int start = instructions.indexOf("Environmental Impact:") + "Environmental Impact:".length();
            impact = instructions.substring(start).trim();
        } else if (instructions.contains("Impact:")) {
            int start = instructions.indexOf("Impact:") + "Impact:".length();
            impact = instructions.substring(start).trim();
        }

        tvHazardousMaterials.setText(hazardous.isEmpty() ? "No hazardous materials identified" : hazardous);
        tvRecommendations.setText(formatRecommendations(recommendations));
        tvImpact.setText(impact.isEmpty() ? "Unknown" : impact);
        setImpactColor(impact);
    }

    private String formatRecommendations(String recommendations) {
        if (recommendations == null || recommendations.isEmpty()) {
            return "No specific recommendations";
        }

        String cleaned = recommendations.trim();

        if (cleaned.matches(".*\\b\\d+\\..*")) {
            String[] items = cleaned.split("(?=\\b\\d+\\.)");
            if (items.length > 1) {
                StringBuilder formatted = new StringBuilder();
                int counter = 1;
                for (String item : items) {
                    String trimmed = item.trim();
                    if (trimmed.isEmpty()) continue;
                    if (!trimmed.endsWith(".") && !trimmed.endsWith("!") && !trimmed.endsWith("?")) {
                        trimmed += ".";
                    }
                    if (formatted.length() > 0) formatted.append("\n\n");
                    trimmed = trimmed.replaceFirst("^\\d+\\.\\s*", "");
                    formatted.append(counter++).append(". ").append(trimmed);
                }
                return formatted.toString();
            }
        }

        if (cleaned.contains(":")) {
            String[] items = cleaned.split("(?<=\\.)\\s+(?=[A-Z][a-zA-Z ]+:)|(?<=\\n)(?=[A-Z][a-zA-Z ]+:)");

            if (cleaned.contains("\n")) {
                String[] lines = cleaned.split("\n+");
                if (lines.length > 1) {
                    StringBuilder formatted = new StringBuilder();
                    int counter = 1;
                    for (String line : lines) {
                        String trimmed = line.trim();
                        if (trimmed.isEmpty()) continue;
                        if (!trimmed.endsWith(".") && !trimmed.endsWith("!") && !trimmed.endsWith("?")) {
                            trimmed += ".";
                        }
                        if (formatted.length() > 0) formatted.append("\n\n");
                        formatted.append(counter++).append(". ").append(trimmed);
                    }
                    return formatted.toString();
                }
            }

            String[] segments = cleaned.split("(?<=\\.)\\s+(?=[A-Z][a-zA-Z]+ [a-zA-Z]+:|[A-Z][a-zA-Z]+:)");
            if (segments.length > 1) {
                StringBuilder formatted = new StringBuilder();
                int counter = 1;
                for (String seg : segments) {
                    String trimmed = seg.trim();
                    if (trimmed.isEmpty()) continue;
                    if (!trimmed.endsWith(".") && !trimmed.endsWith("!") && !trimmed.endsWith("?")) {
                        trimmed += ".";
                    }
                    if (formatted.length() > 0) formatted.append("\n\n");
                    formatted.append(counter++).append(". ").append(trimmed);
                }
                return formatted.toString();
            }
        }

        if (cleaned.contains("\n")) {
            String[] lines = cleaned.split("\n+");
            StringBuilder formatted = new StringBuilder();
            int counter = 1;
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;
                if (!trimmed.endsWith(".") && !trimmed.endsWith("!") && !trimmed.endsWith("?")) {
                    trimmed += ".";
                }
                if (formatted.length() > 0) formatted.append("\n\n");
                formatted.append(counter++).append(". ").append(trimmed);
            }
            return formatted.toString();
        }

        String[] sentences = cleaned.split("\\.\\s+(?=[A-Z])");
        if (sentences.length > 1) {
            StringBuilder formatted = new StringBuilder();
            int counter = 1;
            for (String sentence : sentences) {
                String trimmed = sentence.trim();
                if (trimmed.isEmpty()) continue;
                if (!trimmed.endsWith(".") && !trimmed.endsWith("!") && !trimmed.endsWith("?")) {
                    trimmed += ".";
                }
                if (formatted.length() > 0) formatted.append("\n\n");
                formatted.append(counter++).append(". ").append(trimmed);
            }
            return formatted.toString();
        }

        return cleaned;
    }

    private void setImpactColor(String impact) {
        if (impact.toLowerCase().contains("high")) {
            tvImpact.setTextColor(Color.parseColor("#D32F2F"));
        } else if (impact.toLowerCase().contains("moderate") || impact.toLowerCase().contains("medium")) {
            tvImpact.setTextColor(Color.parseColor("#F57C00"));
        } else if (impact.toLowerCase().contains("low")) {
            tvImpact.setTextColor(Color.parseColor("#388E3C"));
        } else {
            tvImpact.setTextColor(Color.parseColor("#757575"));
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnFindLocation.setOnClickListener(v -> {
            Intent intent = new Intent(ResultsActivity.this, MapActivity.class);
            intent.putExtra("locationName", "Nearest Recycling Center");
            startActivity(intent);
        });
    }
}
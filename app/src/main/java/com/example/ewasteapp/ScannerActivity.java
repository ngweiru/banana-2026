package com.example.ewasteapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerationConfig;
import com.google.firebase.ai.type.GenerativeBackend;
import com.google.firebase.ai.type.Schema;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class ScannerActivity extends BaseActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private PreviewView previewView;
    private ImageView btnBack;
    private MaterialCardView btnGallery;
    private ImageView btnCapture;
    private MaterialCardView btnFlash;
    private ImageView ivFlashIcon;
    private FrameLayout loadingOverlay;

    private ImageCapture imageCapture;
    private boolean isFlashOn = false;
    private ProcessCameraProvider cameraProvider;

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        initializeViews();
        setupClickListeners();
        setupGalleryLauncher();

        // Request camera permission
        if (checkCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }
    }

    private void initializeViews() {
        previewView = findViewById(R.id.previewView);
        btnBack = findViewById(R.id.btnBack);
        btnGallery = findViewById(R.id.btnGallery);
        btnCapture = findViewById(R.id.btnCapture);
        btnFlash = findViewById(R.id.btnFlash);
        ivFlashIcon = findViewById(R.id.ivFlashIcon);
        loadingOverlay = findViewById(R.id.loadingOverlay);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnCapture.setOnClickListener(v -> captureImage());
        btnGallery.setOnClickListener(v -> openGallery());
        btnFlash.setOnClickListener(v -> toggleFlash());
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            processImageFromGallery(imageUri);
                        }
                    }
                }
        );
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to scan items",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases();
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error starting camera: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases() {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        try {
            cameraProvider.unbindAll();

            cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture);

        } catch (Exception e) {
            Toast.makeText(this, "Error binding camera: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void captureImage() {
        if (imageCapture == null) return;

        loadingOverlay.setVisibility(View.VISIBLE);

        imageCapture.takePicture(
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageCapturedCallback() {
                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        Bitmap bitmap = imageProxyToBitmap(image);
                        image.close();

                        // Process image with Firebase AI
                        processImageWithAI(bitmap);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        loadingOverlay.setVisibility(View.GONE);
                        Toast.makeText(ScannerActivity.this,
                                "Capture failed: " + exception.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void processImageFromGallery(Uri imageUri) {
        loadingOverlay.setVisibility(View.VISIBLE);

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            if (bitmap != null) {
                processImageWithAI(bitmap);
            } else {
                loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            loadingOverlay.setVisibility(View.GONE);
            Toast.makeText(this, "Error reading image: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void processImageWithAI(Bitmap bitmap) {
        Schema jsonSchema = Schema.obj(
                Map.of(
                        "ewaste", Schema.array(
                                Schema.obj(
                                        Map.of(
                                                "itemName", Schema.str(),
                                                "weight", Schema.numDouble(),
                                                "recyclableComponents", Schema.str(),
                                                "hazardousMaterials", Schema.str(),
                                                "reuseRecommendations", Schema.str(),
                                                "impact",
                                                Schema.enumeration(
                                                        List.of("Low", "Moderate", "High")
                                                )
                                        ),
                                        List.of(
                                                "itemName",
                                                "weight",
                                                "recyclableComponents",
                                                "hazardousMaterials",
                                                "reuseRecommendations",
                                                "impact"
                                        )
                                )
                        )
                ),
                List.of("ewaste")
        );

        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.responseMimeType = "application/json";
        configBuilder.responseSchema = jsonSchema;
        GenerationConfig generationConfig = configBuilder.build();

        GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel("gemini-2.5-flash", generationConfig);

        GenerativeModelFutures model = GenerativeModelFutures.from(ai);
        Executor executor = ContextCompat.getMainExecutor(this);

        Content content = new Content.Builder()
                .addImage(bitmap)
                .addText("Identify this e-waste item and provide a breakdown for recycling. Include: item name, estimated weight in g, recyclable components (e.g. copper, plastic), hazardous materials (e.g. lead, mercury), 3-4 creative reuse or proper disposal recommendations, and a brief environmental impact statement.")
                .build();

        ListenableFuture<GenerateContentResponse> imageResponse = model.generateContent(content);

        Futures.addCallback(imageResponse, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                loadingOverlay.setVisibility(View.GONE);

                try {
                    String jsonText = result.getText();
                    if(jsonText != null){
                        jsonText = jsonText.replace("json", "").replace("","").trim();
                    }
                    JSONObject root = new JSONObject(jsonText);
                    JSONArray items = root.getJSONArray("ewaste");
                    JSONObject item = items.getJSONObject(0);

                    // Extract data
                    String deviceName = item.getString("itemName");
                    double weightInGrams = item.getDouble("weight");
                    double weightInKg = weightInGrams / 1000.0;
                    String components = item.getString("recyclableComponents");
                    String hazardous = item.getString("hazardousMaterials");
                    String recommendations = item.getString("reuseRecommendations");
                    String impactLevel = item.getString("impact");

                    // Compress bitmap
                    //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    int targetWidth = 500;
                    int targetHeight = (int)(targetWidth*((float)bitmap.getHeight()/bitmap.getWidth()));
                    Bitmap thumbnail = Bitmap.createScaledBitmap(bitmap,targetWidth,targetHeight,true);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG,60,stream);
                    byte[] byteArray = stream.toByteArray();

                    // Navigate to ResultsActivity
                    Intent intent = new Intent(ScannerActivity.this, ResultsActivity.class);
                    intent.putExtra("deviceName", deviceName);
                    intent.putExtra("modelId", "Unknown");
                    intent.putExtra("weight", weightInKg);
                    intent.putExtra("recyclableComponents", components);
                    intent.putExtra("recycleInstructions",
                            "Hazardous Materials: " + hazardous + "\n\n" +
                                    "Recommendations: " + recommendations + "\n\n" +
                                    "Impact: " + impactLevel);
                    intent.putExtra("imageBytes", byteArray);
                    startActivity(intent);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ScannerActivity.this,
                            "Error analyzing image: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(ScannerActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }, executor);
    }

    private void toggleFlash() {
        if (imageCapture != null) {
            isFlashOn = !isFlashOn;

            if (isFlashOn) {
                imageCapture.setFlashMode(ImageCapture.FLASH_MODE_ON);
                ivFlashIcon.setImageResource(R.drawable.ic_flash_on);
            } else {
                imageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
                ivFlashIcon.setImageResource(R.drawable.ic_flash_off);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }
}
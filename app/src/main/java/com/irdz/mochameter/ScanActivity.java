package com.irdz.mochameter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.irdz.mochameter.databinding.ActivityScanBinding;
import com.irdz.mochameter.model.openfoodfacts.OpenFoodFactsResponse;
import com.irdz.mochameter.service.OpenFoodFactsService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ScanActivity extends AppCompatActivity {

    private static final String COFFEE_TAG = "en:coffees";
    private ActivityScanBinding binding;

    private AdView bannerScan;

    private static Toast toast;

    private static final Set<String> NOT_COFFEE_BARCODES = new HashSet<>();
    public static boolean coffeeRead = false;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 66;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindCamera();
    }

    private void loadBanner() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            bannerScan = findViewById(R.id.banner_scan);
            AdRequest adRequest = new AdRequest.Builder().build();
            bannerScan.loadAd(adRequest);
        }, 500);
    }

    private void bindCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA},
                MY_PERMISSIONS_REQUEST_CAMERA);
        }

        // Get an instance of the ProcessCameraProvider
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        // Set up the camera preview
        Preview preview = new Preview.Builder().build();
        PreviewView previewView = findViewById(R.id.previewView);
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Set up the image analyzer for barcode scanning
        ImageAnalysis imageAnalysis =
            new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        loadBanner();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), image -> {
            // Process the image for barcode scanning
            processImage(image);
        });

        // Bind the camera to the lifecycle
        cameraProviderFuture.addListener(() -> {
            try {
                // Get the ProcessCameraProvider instance
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Bind the lifecycle of the camera to the lifecycle owner
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                );

            } catch (ExecutionException | InterruptedException e) {
                // Handle the error
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void processImage(ImageProxy imageProxy) {
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image =
                InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            BarcodeScanner scanner = BarcodeScanning.getClient();
            Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    // Handle the barcode results
                    if(!barcodes.isEmpty()) {
                        String displayValue = barcodes.get(0).getDisplayValue();
                        readBarcodeFromOpenFoodFacts(displayValue);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    e.printStackTrace();
                })
                .addOnCompleteListener(task -> imageProxy.close());
        }
    }

    private void readBarcodeFromOpenFoodFacts(final String barcode) {
        OpenFoodFactsResponse productByBarcode = OpenFoodFactsService.getInstance().findProductByBarcode(barcode);
        Boolean isCoffee = Optional.ofNullable(productByBarcode)
            .map(pbbc -> pbbc.product)
            .map(p -> p.categories_tags)
            .map(strings -> strings.contains(COFFEE_TAG))
            .orElse(false);
        if(isCoffee && !coffeeRead) {
            coffeeRead = true;
            loadCoffee(productByBarcode);
        } else if(!isCoffee && !NOT_COFFEE_BARCODES.contains(barcode)) {
            NOT_COFFEE_BARCODES.add(barcode);
            showAToast(getString(R.string.not_coffee));
        }
    }

    private void loadCoffee(final OpenFoodFactsResponse productByBarcode) {
        Intent intent = new Intent(this, CoffeeDetail.class);
        intent.putExtra("coffeeDetail", productByBarcode);
        startActivity(intent);
    }

    public void showAToast (String st){ //"Toast toast" is declared in the class
        String oldText = Optional.ofNullable(toast)
            .map(Toast::getView)
            .map(toast -> (LinearLayout)toast)
            .map(linearLayout -> linearLayout.getChildAt(0))
            .map(children -> (TextView) children)
            .map(TextView::getText)
            .map(CharSequence::toString)
            .orElse(null);
        if(toast == null || toast.getView() == null) {
            if(oldText == null || !oldText.equalsIgnoreCase(st) || !toast.getView().isShown()) {
                toast = Toast.makeText(this, st, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
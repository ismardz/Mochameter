package com.irdz.mochameter.ui.scan;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.irdz.mochameter.AddCoffeeCategory;
import com.irdz.mochameter.R;
import com.irdz.mochameter.databinding.ActivityScanBinding;
import com.irdz.mochameter.model.openfoodfacts.OpenFoodFactsResponse;
import com.irdz.mochameter.model.openfoodfacts.Product;
import com.irdz.mochameter.service.OpenFoodFactsService;
import com.irdz.mochameter.ui.coffeedetail.CoffeeDetail;
import com.irdz.mochameter.ui.registercoffee.RegisterCoffee;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ScanActivity extends AppCompatActivity {

    private static final String COFFEES_TAG = "en:coffees";
    private static final String COFFEE_TAG = "en:coffee";
    private static final String BEVERAGE_TAG = "en:beverages";
    private ActivityScanBinding binding;

    private FrameLayout frameLayoutNotRegistered;
    private FrameLayout frameLayoutNotCoffee;

    private static final Set<String> NOT_COFFEE_BARCODES = new HashSet<>();
    public static boolean coffeeRead = false;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 66;

    private String barcode;
    private Toast toast;
    private boolean keepScanning = true;

    private OpenFoodFactsResponse productByBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        frameLayoutNotRegistered = binding.frameLayoutNotRegistered;
        frameLayoutNotRegistered.setVisibility(View.INVISIBLE);

        frameLayoutNotCoffee = binding.frameLayoutNotCoffee;
        frameLayoutNotCoffee.setVisibility(View.INVISIBLE);

        TextView tvMessageNotRegistered = binding.tvMessageNotRegistered;
        tvMessageNotRegistered.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterCoffee.class);
            intent.putExtra("barcode", barcode);
            startActivity(intent);
        });

        TextView tvMessageNotCoffee = binding.tvMessageNotCoffee;
        tvMessageNotCoffee.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCoffeeCategory.class);
            intent.putExtra("coffeeDetail", productByBarcode);
            startActivity(intent);
        });

        bindCamera();
    }

    private void loadBanner() {
        AdView bannerScan = findViewById(R.id.banner_scan);
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerScan.loadAd(adRequest);
    }

    private void bindCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA},
                MY_PERMISSIONS_REQUEST_CAMERA);
        }

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, R.string.camera_needed, Toast.LENGTH_LONG).show();
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
                    if(!barcodes.isEmpty() && keepScanning) {
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

    public void readBarcodeFromOpenFoodFacts(final String barcode) {
        productByBarcode = OpenFoodFactsService.getInstance().findProductByBarcode(barcode);
        Optional<Product> product = Optional.ofNullable(productByBarcode)
            .map(pbbc -> pbbc.product);
        product.map(p -> p.labels_tags)
            .map(strings -> strings.contains(COFFEES_TAG));
        Boolean isCoffee = isCoffee(product);
        if(isCoffee && !coffeeRead) {
            keepScanning = false;
            coffeeRead = true;
            loadCoffee(productByBarcode);
        } else if(!isCoffee && !NOT_COFFEE_BARCODES.contains(barcode)) {
            NOT_COFFEE_BARCODES.add(barcode);
            new Handler(Looper.getMainLooper()).postDelayed(() -> NOT_COFFEE_BARCODES.remove(barcode), 5000);
            this.barcode = barcode;
            if(productByBarcode == null) {
                showAToast("Error");
            } else if(productByBarcode.product == null) {
                showMessageNotRegistered();
            } else {
                showMessageNotCoffee();
            }
        }
    }

    private Boolean isCoffee(final Optional<Product> product) {
        Boolean isCoffee = product
            .map(p -> p.categories_tags)
            .map(strings -> strings.contains(COFFEES_TAG))
            .filter(Boolean::booleanValue)
            .orElseGet(() -> product.map(p -> p.labels_tags)
                .map(strings -> strings.contains(COFFEES_TAG))
                .filter(Boolean::booleanValue)
                .orElseGet(
                    () -> product.map(p ->
                    p.categories_tags != null && p.categories_tags.contains(BEVERAGE_TAG) &&
                        p.ingredients != null && p.ingredients.stream().anyMatch(i -> i.id.equalsIgnoreCase(COFFEE_TAG)))
                    .orElse(false)
                )
            );
        return isCoffee;
    }

    private void loadCoffee(final OpenFoodFactsResponse productByBarcode) {
        Intent intent = new Intent(this, CoffeeDetail.class);
        intent.putExtra("coffeeDetail", productByBarcode);
        startActivity(intent);
        keepScanning = true;
    }

    private void showMessageNotRegistered() {
        showMessage(frameLayoutNotRegistered);
    }

    private void showMessageNotCoffee() {
        showMessage(frameLayoutNotCoffee);
    }

    private void showMessage(final FrameLayout layoutToShow) {
        if(frameLayoutNotRegistered.getVisibility() == View.INVISIBLE && frameLayoutNotCoffee.getVisibility() == View.INVISIBLE) {
            keepScanning = false;
            layoutToShow.setVisibility(View.VISIBLE);
            layoutToShow.postDelayed(() -> {
                layoutToShow.setVisibility(View.INVISIBLE);
                keepScanning = true;
            }, 3000);
        }
    }

    private void launchOpenFoodFacts() {
        String packageName = "org.openfoodfacts.scanner";
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);

        if (launchIntent != null) {
            // Open Food Facts app is installed, launch it
            startActivity(launchIntent);
        } else {
            // Open Food Facts app is not installed, open Play Store to install it
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
            if (playStoreIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(playStoreIntent);
            } else {
                // Play Store app not found on the device, open web browser to Open Food Facts app page
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                startActivity(webIntent);
            }
        }
    }

    private void showAToast (final String st){
        String oldText = Optional.ofNullable(toast)
            .map(Toast::getView)
            .map(toast -> (TextView)toast)
            .map(TextView::getText)
            .map(CharSequence::toString)
            .orElse(null);
        if(toast == null || toast.getView() == null) {
            if(oldText == null || !oldText.equalsIgnoreCase(st) || !toast.getView().isShown()) {
                // Create a new TextView for the toast message
                TextView textView = new TextView(getApplicationContext());
                textView.setText(st);

                textView.setBackgroundColor(Color.BLACK);
                textView.setTextColor(Color.WHITE);

                toast = Toast.makeText(this, st, Toast.LENGTH_LONG);

                toast.show();
            }
        }
    }
}
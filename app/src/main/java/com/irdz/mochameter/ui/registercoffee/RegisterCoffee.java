package com.irdz.mochameter.ui.registercoffee;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.FullScreenContentCallback;
import com.irdz.mochameter.R;
import com.irdz.mochameter.dao.impl.UserDaoImpl;
import com.irdz.mochameter.model.openfoodfacts.OpenFoodFactsResponse;
import com.irdz.mochameter.service.CoffeeCreatorService;
import com.irdz.mochameter.service.OpenFoodFactsService;
import com.irdz.mochameter.ui.coffeedetail.CoffeeDetail;
import com.irdz.mochameter.util.AdUtils;

public class RegisterCoffee extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private RegisterCoffeeViewModel registerCoffeeViewModel;

    private EditText etBrand;
    private EditText etName;
    private ImageView ivPreview;
    private Bitmap imageBitmap;
    private ImageButton btnPhoto;
    private Button registerButton;

    private OpenFoodFactsResponse productByBarcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_coffee);

        String barcode = getIntent().getStringExtra("barcode");

        registerCoffeeViewModel = new ViewModelProvider(this, new RegisterCoffeeViewModelFactory())
            .get(RegisterCoffeeViewModel.class);

        TextView tvBarcodeValue = findViewById(R.id.tvBarcodeValue);
        tvBarcodeValue.setText(barcode);

        etBrand = findViewById(R.id.etBrand);
        etName = findViewById(R.id.etName);
        ivPreview = findViewById(R.id.ivPreview);
        btnPhoto = findViewById(R.id.btnPhoto);


        registerButton = findViewById(R.id.register_button);

        formValidations();

        AdUtils.loadAd(getString(R.string.interstitialAdReviewCoffee_id), this, new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();

                Intent intent = new Intent(RegisterCoffee.this, CoffeeDetail.class);
                intent.putExtra("coffeeDetail", productByBarcode);
                startActivity(intent);
            }
        });

        btnPhoto.setOnClickListener(view -> takePhoto());

        saveButtonLogic(barcode);

    }

    private void saveButtonLogic(final String barcode) {
        registerButton.setOnClickListener(v -> {
            OpenFoodFactsService.getInstance().uploadProduct(
                barcode,
                etBrand.getText().toString(),
                etName.getText().toString(),
                imageBitmap);
            finish();
            productByBarcode = OpenFoodFactsService.getInstance().findProductByBarcode(barcode);
            if(productByBarcode.product == null) {
                Toast.makeText(this, R.string.error_creating_coffee, Toast.LENGTH_SHORT);
            } else {
                AdUtils.showAd(getString(R.string.interstitialAdReviewCoffee_id), this);
                CoffeeCreatorService.getInstance().insert(
                    UserDaoImpl.getAndroidId(this),
                    barcode
                );
            }
        });
    }

    private void formValidations() {
        registerCoffeeViewModel.registerFormState.observe(this, registerFormStateFormState -> {
            if (registerFormStateFormState == null) {
                return;
            }
            registerButton.setEnabled(registerFormStateFormState.isDataValid());
            if (registerFormStateFormState.getBrandError() != null) {
                etBrand.setError(getString(registerFormStateFormState.getBrandError()));
            }
            if (registerFormStateFormState.getNameError() != null) {
                etName.setError(getString(registerFormStateFormState.getNameError()));
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                registerCoffeeViewModel.registerDataChanged(
                    etBrand.getText().toString(),
                    etName.getText().toString(),
                    imageBitmap
                );
            }
        };
        etBrand.addTextChangedListener(afterTextChangedListener);
        etName.addTextChangedListener(afterTextChangedListener);
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    ivPreview.setImageBitmap(imageBitmap);
                    registerCoffeeViewModel.registerDataChanged(
                        etBrand.getText().toString(),
                        etName.getText().toString(),
                        imageBitmap
                    );
                    break;
                default:
                    break;
            }
        }
    }



}
package com.irdz.mochameter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.irdz.mochameter.model.openfoodfacts.OpenFoodFactsResponse;
import com.irdz.mochameter.service.OpenFoodFactsService;
import com.irdz.mochameter.ui.coffeedetail.CoffeeDetail;

import java.util.Optional;

public class AddCoffeeCategory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coffee_category);

        OpenFoodFactsResponse productByBarcode = (OpenFoodFactsResponse) getIntent().getSerializableExtra("coffeeDetail");

        Optional.ofNullable(productByBarcode)
            .map(detail -> detail.product)
            .ifPresent(p -> {
                TextView tvName = findViewById(R.id.tvName);
                tvName.setText(p.product_name);

                TextView tvBrand = findViewById(R.id.tvBrand);
                tvBrand.setText(p.brands);

                findViewById(R.id.btnSendCategory).setOnClickListener(v -> {
                    OpenFoodFactsService.getInstance().addCoffeeCategory(p.code);
                    finish();
                    Intent intent = new Intent(this, CoffeeDetail.class);
                    intent.putExtra("coffeeDetail", productByBarcode);
                    startActivity(intent);
                });
            });

    }
}
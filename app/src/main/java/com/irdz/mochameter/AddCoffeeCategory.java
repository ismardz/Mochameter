package com.irdz.mochameter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.irdz.mochameter.dao.impl.UserDaoImpl;
import com.irdz.mochameter.model.openfoodfacts.OpenFoodFactsResponse;
import com.irdz.mochameter.service.CoffeeCreatorService;
import com.irdz.mochameter.service.OpenFoodFactsService;
import com.irdz.mochameter.ui.coffeedetail.CoffeeDetail;
import com.irdz.mochameter.util.OpenFoodFactsUtils;
import com.squareup.picasso.Picasso;

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

                ImageView ivCoffee = findViewById(R.id.ivCoffee);
                Optional.ofNullable(OpenFoodFactsUtils.getImageUrl(productByBarcode))
                    .map(Picasso.get()::load)
                    .ifPresent(requestCreator -> requestCreator.into(ivCoffee));

                findViewById(R.id.btnSendCategory).setOnClickListener(v -> {
                    OpenFoodFactsService.getInstance().addCoffeeCategory(p.code);
                    CoffeeCreatorService.getInstance().insert(UserDaoImpl.getAndroidId(this), p.code);
                    finish();
                    Intent intent = new Intent(this, CoffeeDetail.class);
                    intent.putExtra("coffeeDetail", productByBarcode);
                    startActivity(intent);
                });
            });

    }
}
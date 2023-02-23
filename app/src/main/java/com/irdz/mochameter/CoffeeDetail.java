package com.irdz.mochameter;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.irdz.mochameter.model.openfoodfacts.OpenFoodFactsResponse;
import com.squareup.picasso.Picasso;

public class CoffeeDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_detail);

        OpenFoodFactsResponse coffeeDetail = (OpenFoodFactsResponse) getIntent().getSerializableExtra("coffeeDetail");
        fillCoffeInfo(coffeeDetail);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ScanActivity.coffeeRead = false;
    }

    private void fillCoffeInfo(final OpenFoodFactsResponse coffeeDetail) {

//        Coffee byBarcode = CoffeeService.getInstance().findByBarcode(coffeeDetail.code);
//        ReviewService.getInstance().getReviewByCoffeeId()

        ImageView ivCoffee = findViewById(R.id.ivCoffee);
        Picasso.get().load(coffeeDetail.product.image_front_url).into(ivCoffee);

        TextView tvName = findViewById(R.id.tvName);
        tvName.setText(coffeeDetail.product.product_name);

        TextView tvBrand = findViewById(R.id.tvBrand);
        tvBrand.setText(coffeeDetail.product.brands);

    }
}
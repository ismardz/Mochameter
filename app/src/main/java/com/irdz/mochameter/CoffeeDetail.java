package com.irdz.mochameter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.irdz.mochameter.model.entity.Coffee;
import com.irdz.mochameter.model.entity.Review;
import com.irdz.mochameter.model.openfoodfacts.OpenFoodFactsResponse;
import com.irdz.mochameter.service.CoffeeService;
import com.irdz.mochameter.service.ReviewService;
import com.irdz.mochameter.util.ExecutorUtils;
import com.squareup.picasso.Picasso;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class CoffeeDetail extends AppCompatActivity {

    public static Coffee coffeeByBarCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_detail);

        OpenFoodFactsResponse coffeeDetail = (OpenFoodFactsResponse) getIntent().getSerializableExtra("coffeeDetail");
        fillCoffeInfo(coffeeDetail);

        Button btnReviewCoffe = findViewById(R.id.btnReviewCoffe);
        btnReviewCoffe.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewCoffee.class);
            intent.putExtra("coffeByBarcode", coffeeByBarCode);
            intent.putExtra("coffeeDetail", coffeeDetail);
            startActivity(intent);
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        OpenFoodFactsResponse coffeeDetail = (OpenFoodFactsResponse) getIntent().getSerializableExtra("coffeeDetail");
        fillCoffeInfo(coffeeDetail);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ScanActivity.coffeeRead = false;
    }

    private void fillCoffeInfo(final OpenFoodFactsResponse coffeeDetail) {

        coffeeByBarCode = CoffeeService.getInstance().findByBarcode(coffeeDetail.code);
        AtomicReference<Review> review = new AtomicReference<>();
        Optional.ofNullable(coffeeByBarCode)
            .map(Coffee::getId)
                .ifPresent(coffeeId -> ExecutorUtils.runCallables(() -> {
                    review.set(ReviewService.getInstance().getReviewByCoffeeId(coffeeByBarCode.getId()));
                    return review;
                }));

        fillCoffeeData(coffeeDetail);
        fillReview(review.get());
    }

    private void fillReview(final Review review) {
        boolean coffeeReviewed = Optional.ofNullable(review).isPresent();
        RatingBar rbAcidity = findViewById(R.id.rbAcidity);
        RatingBar rbAroma = findViewById(R.id.rbAroma);
        RatingBar rbBody = findViewById(R.id.rbBody);
        RatingBar rbAftertaste = findViewById(R.id.rbAftertaste);
        RatingBar rbScore = findViewById(R.id.rbScore);

        if(coffeeReviewed) {
            findViewById(R.id.tvNoReviews).setVisibility(View.INVISIBLE);
            rbAcidity.setRating(review.getAcidity().floatValue());
            rbAroma.setRating(review.getAroma().floatValue());
            rbBody.setRating(review.getBody().floatValue());
            rbAftertaste.setRating(review.getAftertaste().floatValue());
            rbScore.setRating(review.getScore().floatValue());
        } else {
            rbAcidity.setRating(0);
            rbAroma.setRating(0);
            rbBody.setRating(0);
            rbAftertaste.setRating(0);
            rbScore.setRating(0);
        }

    }

    private void fillCoffeeData(final OpenFoodFactsResponse coffeeDetail) {
        ImageView ivCoffee = findViewById(R.id.ivCoffee);
        Picasso.get().load(coffeeDetail.product.image_front_url).into(ivCoffee);

        TextView tvName = findViewById(R.id.tvName);
        tvName.setText(coffeeDetail.product.product_name);

        TextView tvBrand = findViewById(R.id.tvBrand);
        tvBrand.setText(coffeeDetail.product.brands);

    }
}
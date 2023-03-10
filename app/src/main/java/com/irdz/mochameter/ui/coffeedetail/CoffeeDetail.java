package com.irdz.mochameter.ui.coffeedetail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.irdz.mochameter.R;
import com.irdz.mochameter.model.entity.Coffee;
import com.irdz.mochameter.model.entity.Review;
import com.irdz.mochameter.model.openfoodfacts.OpenFoodFactsResponse;
import com.irdz.mochameter.service.CoffeeService;
import com.irdz.mochameter.service.OpenFoodFactsService;
import com.irdz.mochameter.service.ReviewService;
import com.irdz.mochameter.ui.reviewcoffee.ReviewCoffee;
import com.irdz.mochameter.ui.scan.ScanActivity;
import com.irdz.mochameter.util.ExecutorUtils;
import com.irdz.mochameter.util.OpenFoodFactsUtils;
import com.irdz.mochameter.util.Score;
import com.squareup.picasso.Picasso;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class CoffeeDetail extends AppCompatActivity {

    public static Coffee coffeeDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_detail);

        coffeeDatabase = null;

        OpenFoodFactsResponse coffeeDetail = (OpenFoodFactsResponse) getIntent().getSerializableExtra("coffeeDetail");
        Review reviewAvg = (Review) getIntent().getSerializableExtra("reviewAvg");
        fillCoffeInfo(coffeeDetail, reviewAvg);

        Button btnReviewCoffe = findViewById(R.id.btnReviewCoffe);
        btnReviewCoffe.setOnClickListener(v -> {
            ReviewCoffee.finish = false;
            Intent intent = new Intent(this, ReviewCoffee.class);
            intent.putExtra("coffeeDatabase", coffeeDatabase);
            intent.putExtra("coffeeDetail", coffeeDetail);
            startActivity(intent);
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        OpenFoodFactsResponse coffeeDetail = (OpenFoodFactsResponse) getIntent().getSerializableExtra("coffeeDetail");
        fillCoffeInfo(coffeeDetail, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ScanActivity.coffeeRead = false;
    }

    private void fillCoffeInfo(final OpenFoodFactsResponse coffeeDetail, final Review reviewAvg) {
        AtomicReference<Review> review = new AtomicReference<>();

        if(reviewAvg == null) {
            if(coffeeDatabase == null) {
                coffeeDatabase = CoffeeService.getInstance().findByBarcode(coffeeDetail.code);
            }
            Optional.ofNullable(coffeeDatabase)
                .map(Coffee::getId)
                .ifPresent(coffeeId -> ExecutorUtils.runCallables(() -> {
                    review.set(ReviewService.getInstance().getReviewByCoffeeId(coffeeDatabase.getId()));
                    return review;
                }));
        } else {
            coffeeDatabase = reviewAvg.getCoffee();
            review.set(reviewAvg);
        }

        if(coffeeDetail == null) {
            OpenFoodFactsResponse productByBarcode = OpenFoodFactsService.getInstance()
                .findProductByBarcode(coffeeDatabase.getBarcode());
            if(productByBarcode != null) {
                fillOFFData(productByBarcode);
                updateObsoleteData(productByBarcode);
            }
        } else {
            fillOFFData(coffeeDetail);
            updateObsoleteData(coffeeDetail);
        }


        fillCoffeeData(coffeeDetail, review.get());
        fillReview(review.get());
    }

    private void fillOFFData(final OpenFoodFactsResponse coffeeDetail) {
        Optional.ofNullable(coffeeDetail)
            .map(openFoodFactsResponse -> openFoodFactsResponse.product)
            .map(product -> product.nutriscore_grade)
            .map(grade -> Score.getNutriscoreResourceByGrade(grade))
            .ifPresent(resource -> findViewById(R.id.ivNutriscore)
                .setForeground(getResources().getDrawable(resource, null)));
        Optional.ofNullable(coffeeDetail)
            .map(openFoodFactsResponse -> openFoodFactsResponse.product)
            .map(product -> product.ecoscore_grade)
            .map(grade -> Score.getEcocoreResourceByGrade(grade))
            .ifPresent(resource -> findViewById(R.id.ivEcoscore)
                .setForeground(getResources().getDrawable(resource, null)));
    }

    private void updateObsoleteData(final OpenFoodFactsResponse coffeeDetail) {
        if (coffeeDatabase != null) {
            Coffee coffeeFromOFF = mapFromOFFtoCoffeDB(coffeeDetail);
            if(coffeeFromOFF != null && !coffeeDatabase.equals(coffeeFromOFF)) {
                coffeeDatabase.setBrand(coffeeFromOFF.getBrand());
                coffeeDatabase.setCoffeeName(coffeeFromOFF.getCoffeeName());
                coffeeDatabase.setImageUrl(coffeeFromOFF.getImageUrl());
                CoffeeService.getInstance().update(coffeeDatabase);
            }
        }
    }

    private void fillReview(final Review review) {
        boolean coffeeReviewed = Optional.ofNullable(review).isPresent();
        RatingBar rbAcidity = findViewById(R.id.rbAcidity);
        RatingBar rbAroma = findViewById(R.id.rbAroma);
        RatingBar rbBody = findViewById(R.id.rbBody);
        RatingBar rbAftertaste = findViewById(R.id.rbAftertaste);
        RatingBar rbScore = findViewById(R.id.rbScore);

        if(coffeeReviewed) {
            TextView noReviewsDesc = findViewById(R.id.tvNoReviews);
            noReviewsDesc.setHeight(0);
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

    private void fillCoffeeData(final OpenFoodFactsResponse coffeeDetail, final Review reviewAvg) {
        ImageView ivCoffee = findViewById(R.id.ivCoffee);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvBrand = findViewById(R.id.tvBrand);

        if(coffeeDetail != null) {
            Optional.ofNullable(OpenFoodFactsUtils.getImageUrl(coffeeDetail))
                .map(Picasso.get()::load)
                .ifPresent(requestCreator -> requestCreator.into(ivCoffee));
            tvName.setText(coffeeDetail.product.product_name);
            tvBrand.setText(coffeeDetail.product.brands);
        } else if(reviewAvg != null) {
            Optional.ofNullable(reviewAvg.getCoffee().getImageUrl()).map(Picasso.get()::load)
                .ifPresent(requestCreator -> requestCreator.into(ivCoffee));
            tvName.setText(reviewAvg.getCoffee().getCoffeeName());
            tvBrand.setText(reviewAvg.getCoffee().getBrand());
        }

    }

    private Coffee mapFromOFFtoCoffeDB(final OpenFoodFactsResponse coffeeDetail) {
        return Coffee.builder()
            .brand(coffeeDetail.product.brands)
            .coffeeName(coffeeDetail.product.product_name)
            .imageUrl(OpenFoodFactsUtils.getImageUrl(coffeeDetail))
            .build();
    }
}
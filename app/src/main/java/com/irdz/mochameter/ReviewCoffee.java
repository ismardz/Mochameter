package com.irdz.mochameter;

import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.irdz.mochameter.config.AppDatabase;
import com.irdz.mochameter.dao.impl.UserDaoImpl;
import com.irdz.mochameter.model.entity.Coffee;
import com.irdz.mochameter.model.entity.Review;
import com.irdz.mochameter.model.entity.User;
import com.irdz.mochameter.model.openfoodfacts.OpenFoodFactsResponse;
import com.irdz.mochameter.service.CoffeeService;
import com.irdz.mochameter.service.ReviewService;
import com.irdz.mochameter.service.UserService;
import com.irdz.mochameter.util.ExecutorUtils;
import com.squareup.picasso.Picasso;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class ReviewCoffee extends AppCompatActivity {

    private RatingBar rbAcidity;
    private RatingBar rbAroma;
    private RatingBar rbBody;
    private RatingBar rbAftertaste;
    private RatingBar rbScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_coffee);

        rbAcidity = findViewById(R.id.rbAcidity);
        rbAroma = findViewById(R.id.rbAroma);
        rbBody = findViewById(R.id.rbBody);
        rbAftertaste = findViewById(R.id.rbAftertaste);
        rbScore = findViewById(R.id.rbScore);


        OpenFoodFactsResponse coffeeDetail = (OpenFoodFactsResponse) getIntent().getSerializableExtra("coffeeDetail");
        Coffee coffeByBarcode = (Coffee) getIntent().getSerializableExtra("coffeByBarcode");

        //buscar review por coffee + MAC address/userid para rellenarlo
        AtomicReference<Review> review = new AtomicReference<>(null);
        if (coffeByBarcode != null) {
            ExecutorUtils.runCallables(() -> {
                review.set(AppDatabase.getInstance().reviewDao.findByCoffeIdAndUserMacOrLoggedInUser(
                    coffeByBarcode.getId(),
                    getAndroidId(),
                    this
                ));
                return null;
            });
        }
        //TODO
        fillCoffeInfo(coffeeDetail, coffeByBarcode, review.get());

    }

    private void fillCoffeInfo(
        final OpenFoodFactsResponse coffeeDetail,
        final Coffee coffeByBarcode,
        final Review review
    ) {

        fillCoffeeData(coffeeDetail);
        fillReview(review);

        Button btnSendReview = findViewById(R.id.btnSendReview);
        btnSendReview.setOnClickListener(v -> {

            User user = getUser();
            Coffee coffeeByBarCode = getCoffee(coffeByBarcode, coffeeDetail);

            Review revw = Review.builder()
                .id(Optional.ofNullable(review).map(Review::getId).orElse(null))
                .acidity(Double.valueOf(rbAcidity.getRating()))
                .aroma(Double.valueOf(rbAroma.getRating()))
                .body(Double.valueOf(rbBody.getRating()))
                .aftertaste(Double.valueOf(rbAftertaste.getRating()))
                .score(Double.valueOf(rbAftertaste.getRating()))
                .user(user)
                .coffee(coffeeByBarCode)
                .build();

            ReviewService.getInstance().insertOrUpdate(revw);

            CoffeeDetail.coffeeByBarCode = coffeeByBarCode;

            onBackPressed();
        });

    }

    private Coffee getCoffee(
        final Coffee coffeByBarcode,
        final OpenFoodFactsResponse coffeeDetail
    ) {
        if(coffeByBarcode == null) {
            return CoffeeService.getInstance().insert(coffeeDetail);
        }
        return coffeByBarcode;
    }

    private User getUser() {
        int userId;
        if(UserDaoImpl.getLoggedInUserId(this) != null) {
            userId = UserDaoImpl.getLoggedInUserId(this);
        } else {
            userId = UserService.getInstance().findByAndroidIdOrCreateIt(getAndroidId()).getId();
        }
        return User.builder().id(userId).build();
    }

    private void fillReview(final Review review) {
        boolean coffeeReviewedByUser = Optional.ofNullable(review).isPresent();

        if (coffeeReviewedByUser) {
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

    private String getAndroidId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
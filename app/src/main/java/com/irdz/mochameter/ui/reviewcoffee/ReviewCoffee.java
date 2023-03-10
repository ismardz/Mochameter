package com.irdz.mochameter.ui.reviewcoffee;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.irdz.mochameter.R;
import com.irdz.mochameter.dao.impl.UserDaoImpl;
import com.irdz.mochameter.model.entity.Coffee;
import com.irdz.mochameter.model.entity.Review;
import com.irdz.mochameter.model.entity.User;
import com.irdz.mochameter.model.openfoodfacts.OpenFoodFactsResponse;
import com.irdz.mochameter.service.CoffeeService;
import com.irdz.mochameter.service.ReviewService;
import com.irdz.mochameter.service.UserService;
import com.irdz.mochameter.ui.coffeedetail.CoffeeDetail;
import com.irdz.mochameter.util.AdUtils;
import com.squareup.picasso.Picasso;

import java.util.Optional;

public class ReviewCoffee extends AppCompatActivity {

    private RatingBar rbAcidity;
    private RatingBar rbAroma;
    private RatingBar rbBody;
    private RatingBar rbAftertaste;
    private RatingBar rbScore;

    private InterstitialAd mInterstitialAd;

    public static boolean finish = false;
    public static boolean reviewUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(finish) {
            onBackPressed();
            finish = false;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_coffee);

        rbAcidity = findViewById(R.id.rbAcidity);
        rbAroma = findViewById(R.id.rbAroma);
        rbBody = findViewById(R.id.rbBody);
        rbAftertaste = findViewById(R.id.rbAftertaste);
        rbScore = findViewById(R.id.rbScore);


        OpenFoodFactsResponse coffeeDetail = (OpenFoodFactsResponse) getIntent().getSerializableExtra("coffeeDetail");
        Coffee coffeeDatabase = (Coffee) getIntent().getSerializableExtra("coffeeDatabase");

        //buscar review por coffee + MAC address/userid para rellenarlo
        Review review = null;
        if (coffeeDatabase != null) {
            review = ReviewService.getInstance().findByCoffeIdAndUserAndroidIdOrLoggedInUser(
                coffeeDatabase.getId(),
                UserDaoImpl.getAndroidId(this),
                UserDaoImpl.getLoggedInUserId(this));
        }
        fillCoffeInfo(coffeeDetail, coffeeDatabase, review);

        AdUtils.loadAd(getString(R.string.interstitialAdReviewCoffee_id), this, null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!finish) {
            finish = true;
        }
    }

    private void fillCoffeInfo(
        final OpenFoodFactsResponse coffeeDetail,
        final Coffee coffeeDatabase,
        final Review review
    ) {

        fillCoffeeData(coffeeDetail, coffeeDatabase);
        fillReview(review);

        Button btnSendReview = findViewById(R.id.btnSendReview);
        btnSendReview.setOnClickListener(v -> {
            float acidity = rbAcidity.getRating();
            float aroma = rbAroma.getRating();
            float body = rbBody.getRating();
            float aftertaste = rbAftertaste.getRating();
            float score = rbScore.getRating();

            if(acidity == 0 && aroma == 0 && body == 0 && aftertaste == 0 && score == 0) {
                Toast.makeText(this, R.string.thatBad, Toast.LENGTH_SHORT).show();
            } else {
                User user = getUser();
                Coffee coffeeByBarCode = getCoffee(coffeeDatabase, coffeeDetail);

                Review revw = Review.builder()
                    .id(Optional.ofNullable(review).map(Review::getId).orElse(null))
                    .acidity(Double.valueOf(acidity))
                    .aroma(Double.valueOf(aroma))
                    .body(Double.valueOf(body))
                    .aftertaste(Double.valueOf(aftertaste))
                    .score(Double.valueOf(score))
                    .user(user)
                    .coffee(coffeeByBarCode)
                    .build();

                if(review == null || !review.equals(revw)) {
                    ReviewService.getInstance().insertOrUpdate(revw);

                    CoffeeDetail.coffeeDatabase = coffeeByBarCode;

                    finish = true;

                    AdUtils.showAd(getString(R.string.interstitialAdReviewCoffee_id), this);

                    reviewUpdated = true;

                }
                onBackPressed();
            }


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
            userId = UserService.getInstance().findByAndroidIdOrCreateIt(
                UserDaoImpl.getAndroidId(this)).getId();
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

    private void fillCoffeeData(final OpenFoodFactsResponse coffeeDetail, final Coffee coffeeDatabase) {
        ImageView ivCoffee = findViewById(R.id.ivCoffee);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvBrand = findViewById(R.id.tvBrand);

        if(coffeeDatabase == null) {
            Optional.ofNullable(coffeeDetail.product.image_front_url).map(Picasso.get()::load)
                .ifPresent(requestCreator -> requestCreator.into(ivCoffee));
            tvName.setText(coffeeDetail.product.product_name);
            tvBrand.setText(coffeeDetail.product.brands);
        } else {
            Optional.ofNullable(coffeeDatabase.getImageUrl()).map(Picasso.get()::load)
                .ifPresent(requestCreator -> requestCreator.into(ivCoffee));
            tvName.setText(coffeeDatabase.getCoffeeName());
            tvBrand.setText(coffeeDatabase.getBrand());
        }

    }
}
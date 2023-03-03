package com.irdz.mochameter;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
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

    private InterstitialAd mInterstitialAd;

    public static boolean finish = false;

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
        AtomicReference<Review> review = new AtomicReference<>(null);
        if (coffeeDatabase != null) {
            ExecutorUtils.runCallables(() -> {
                review.set(AppDatabase.getInstance().reviewDao.findByCoffeIdAndUserAndroidIdOrLoggedInUser(
                    coffeeDatabase.getId(),
                    getAndroidId(),
                    this
                ));
                return null;
            });
        }
        fillCoffeInfo(coffeeDetail, coffeeDatabase, review.get());

        loadAd();
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

            User user = getUser();
            Coffee coffeeByBarCode = getCoffee(coffeeDatabase, coffeeDetail);

            Review revw = Review.builder()
                .id(Optional.ofNullable(review).map(Review::getId).orElse(null))
                .acidity(Double.valueOf(rbAcidity.getRating()))
                .aroma(Double.valueOf(rbAroma.getRating()))
                .body(Double.valueOf(rbBody.getRating()))
                .aftertaste(Double.valueOf(rbAftertaste.getRating()))
                .score(Double.valueOf(rbScore.getRating()))
                .user(user)
                .coffee(coffeeByBarCode)
                .build();

            ReviewService.getInstance().insertOrUpdate(revw);

            CoffeeDetail.coffeeDatabase = coffeeByBarCode;

            finish = true;

            showAd();

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

    private void fillCoffeeData(final OpenFoodFactsResponse coffeeDetail, final Coffee coffeeDatabase) {
        ImageView ivCoffee = findViewById(R.id.ivCoffee);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvBrand = findViewById(R.id.tvBrand);

        if(coffeeDatabase == null) {
            Picasso.get().load(coffeeDetail.product.image_front_url).into(ivCoffee);
            tvName.setText(coffeeDetail.product.product_name);
            tvBrand.setText(coffeeDetail.product.brands);
        } else {
            Picasso.get().load(coffeeDatabase.getImageUrl()).into(ivCoffee);
            tvName.setText(coffeeDatabase.getCoffeeName());
            tvBrand.setText(coffeeDatabase.getBrand());

        }

    }

    private String getAndroidId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }



    private void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        //TODO
        InterstitialAd.load(this, getString(R.string.testInterstitialAd_id), adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mInterstitialAd = interstitialAd;
                    configureAdd();
                    Log.i(TAG, "onAdLoaded");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.d(TAG, loadAdError.toString());
                    mInterstitialAd = null;
                }
            });
    }

    private void showAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    private void configureAdd() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.");
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.");
                mInterstitialAd = null;
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.");
                mInterstitialAd = null;
            }

            @Override
            public void onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.");
            }

            @Override
            public void onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.");
            }
        });
    }
}
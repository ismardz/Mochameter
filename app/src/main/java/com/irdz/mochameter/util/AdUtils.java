package com.irdz.mochameter.util;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AdUtils {

    private static final Map<String, InterstitialAd> mapAds = new HashMap<>();

    public static void loadAd(final String adId, final Activity activity, final FullScreenContentCallback fullScreenContentCallback) {
        AdRequest adRequest = new AdRequest.Builder().build();
        //TODO
        InterstitialAd.load(activity, adId, adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    mapAds.put(adId, configureAdd(interstitialAd));
                    Optional.ofNullable(fullScreenContentCallback).ifPresent(interstitialAd::setFullScreenContentCallback);
                    Log.i(TAG, "onAdLoaded");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.d(TAG, loadAdError.toString());
                    mapAds.put(adId, null);
                }
            });
    }

    public static void showAd(final String key, final Activity activity) {
        InterstitialAd mInterstitialAd = mapAds.get(key);
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    private static InterstitialAd configureAdd(final InterstitialAd mInterstitialAd) {
        final InterstitialAd[] adAux = {mInterstitialAd};
        adAux[0].setFullScreenContentCallback(new FullScreenContentCallback(){
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
                adAux[0] = null;
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.");
                adAux[0] = null;
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
        return adAux[0];
    }
}

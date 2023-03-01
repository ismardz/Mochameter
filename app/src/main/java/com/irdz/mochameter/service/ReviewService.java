package com.irdz.mochameter.service;

import com.irdz.mochameter.config.AppDatabase;
import com.irdz.mochameter.model.entity.Review;
import com.irdz.mochameter.util.ExecutorUtils;

import java.util.concurrent.atomic.AtomicReference;

public class ReviewService {

    private static ReviewService instance;

    public static ReviewService getInstance() {
        if(instance == null) {
            instance = new ReviewService();
        }
        return instance;
    }

    //TODO sumar valoraciones y dividir entre total
    public Review getReviewByCoffeeId(final int coffeeId) {
        AtomicReference<Review> review = new AtomicReference<>();
        ExecutorUtils.runCallables(() -> {
            review.set(AppDatabase.getInstance().reviewDao.findByCoffeIdAvg(coffeeId));
            return review;
        });
        return review.get();
    }

    public void insertOrUpdate(final Review review) {
        ExecutorUtils.runCallables(() -> {
            AppDatabase.getInstance().reviewDao.createOrUpdate(review);
            return null;
        });
    }
}

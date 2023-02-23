package com.irdz.mochameter.service;

import com.irdz.mochameter.model.entity.Review;

public class ReviewService {

    private static ReviewService instance;

    public static ReviewService getInstance() {
        if(instance == null) {
            instance = new ReviewService();
        }
        return instance;
    }

    public Review getReviewByCoffeeId(final int coffeeId) {
//        ExecutorUtils.runCallables(() -> {
//            Review review = AppDatabase.getInstance().reviewDao.queryForId(id);
//            return review;
//        })
        return Review.builder().build();
    }
}

package com.irdz.mochameter.service;

import com.irdz.mochameter.config.AppDatabase;
import com.irdz.mochameter.model.entity.Review;
import com.irdz.mochameter.util.CoffeeOrder;
import com.irdz.mochameter.util.ExecutorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ReviewService {

    private static ReviewService instance;

    public static ReviewService getInstance() {
        if(instance == null) {
            instance = new ReviewService();
        }
        return instance;
    }

    public Review getReviewByCoffeeId(final Integer coffeeId) {
        AtomicReference<Review> review = new AtomicReference<>();
        ExecutorUtils.runCallables(() -> {
            review.set(AppDatabase.getInstance().getReviewDao().findByCoffeIdAvg(coffeeId));
            return review;
        });
        return review.get();
    }

    public List<Review> findAvgOrderByPaged(
        final String queryNameBrand,
        final CoffeeOrder order,
        final Boolean reversed,
        final Integer page
    ) {
        List<Review> reviews = new ArrayList<>();
        ExecutorUtils.runCallables(() -> {
            reviews.addAll(AppDatabase.getInstance().getReviewDao().findByAvgOrderBy(queryNameBrand, order, reversed, page));
            return null;
        });
        return reviews;
    }

    public List<Review> findMyEvaluationsOrderByPaged(
        final String queryNameBrand,
        final CoffeeOrder order,
        final Boolean reversed,
        final Integer page,
        final String androidId
    ) {
        List<Review> reviews = new ArrayList<>();
        ExecutorUtils.runCallables(() -> {
            reviews.addAll(AppDatabase.getInstance().getReviewDao()
                .findMyEvaluationsOrderByPaged(queryNameBrand, order, reversed, page, androidId));
            return null;
        });
        return reviews;
    }

    public void insertOrUpdate(final Review review) {
        ExecutorUtils.runCallables(() -> {
            AppDatabase.getInstance().getReviewDao().createOrUpdate(review);
            return null;
        });
    }

    public Review findByCoffeIdAndUserAndroidIdOrLoggedInUser(final int coffeeId, final String androidId, final Integer userId) {
        AtomicReference<Review> rev = new AtomicReference<>(null);
        ExecutorUtils.runCallables(() -> {
            rev.set(AppDatabase.getInstance().getReviewDao().findByCoffeIdAndUserAndroidIdOrLoggedInUser(
                coffeeId,
                androidId,
                userId
            ));
            return rev;
        });
        return rev.get();
    }
}

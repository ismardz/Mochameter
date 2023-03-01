package com.irdz.mochameter.dao.impl;

import android.content.ContextWrapper;

import com.irdz.mochameter.config.AppDatabase;
import com.irdz.mochameter.dao.ReviewDao;
import com.irdz.mochameter.model.entity.Coffee;
import com.irdz.mochameter.model.entity.Review;
import com.irdz.mochameter.model.entity.User;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import lombok.NonNull;

public class ReviewDaoImpl extends BaseDaoImpl<Review, Integer> implements ReviewDao {

    private static ReviewDaoImpl instance;

    private ReviewDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Review.class);
    }

    @NonNull
    public static ReviewDaoImpl getInstance(ConnectionSource connectionSource) {
        if (instance == null) {
            try {
                instance = new ReviewDaoImpl(connectionSource);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public Review findByCoffeIdAndUserMacOrLoggedInUser(final int coffeeId, final String androidId, final ContextWrapper context) {
        try {
            QueryBuilder<Review, Integer> reviewQueryBuilder = AppDatabase.getInstance().reviewDao.queryBuilder();
            QueryBuilder<User, Integer> userQueryBuilder = AppDatabase.getInstance().userDao.queryBuilder();

            Where<Review, Integer> whereReview = queryBuilder().where()
                .eq("coffee_id", coffeeId);

            Integer loggedInUserId = UserDaoImpl.getLoggedInUserId(context);
            if(loggedInUserId != null) {
                whereReview.and().eq("user_id", loggedInUserId);
            } else {
                Where<User, Integer> whereUser = userQueryBuilder.where().eq("android_id", androidId);
                userQueryBuilder.setWhere(whereUser);

                reviewQueryBuilder.join(userQueryBuilder);
            }

            reviewQueryBuilder.setWhere(whereReview);

            return reviewQueryBuilder.queryForFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Review findByCoffeIdAvg(final int coffeeId) {
        String query = "select " +
            " coffee_id, " +
            " avg(aroma) as aroma," +
            " avg(acidity) as acidity," +
            " avg(body) as body," +
            " avg(aftertaste) as aftertaste," +
            " avg(score) as score" +
            " from public.review " +
            " where coffee_id = " + coffeeId +
            " group by coffee_id";
        try {
            GenericRawResults<String[]> results = AppDatabase.getInstance().reviewDao.queryRaw(query);
            String[] row = results.getFirstResult();
            Review review = new Review();
            review.setCoffee(Coffee.builder().id(Integer.valueOf(row[0])).build()); // set the coffee using the coffee_id column
            review.setAroma(Double.valueOf(row[1])); // set the aroma using the aroma column
            review.setAcidity(Double.valueOf(row[2])); // set the acidity using the acidity column
            review.setBody(Double.valueOf(row[3])); // set the body using the body column
            review.setAftertaste(Double.valueOf(row[4])); // set the aftertaste using the aftertaste column
            review.setScore(Double.valueOf(row[5])); // set the score using the score column

            return review;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.irdz.mochameter.dao.impl;

import com.irdz.mochameter.config.AppDatabase;
import com.irdz.mochameter.dao.ReviewDao;
import com.irdz.mochameter.model.entity.Review;
import com.j256.ormlite.dao.BaseDaoImpl;
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

    @Override
    public int create(final Review review) {
        try {
            return AppDatabase.getInstance().reviewDao.create(review);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.irdz.mochameter.dao;

import com.irdz.mochameter.model.entity.Review;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public interface ReviewDao extends Dao<Review, Integer> {

    int create(Review review) throws SQLException;

}

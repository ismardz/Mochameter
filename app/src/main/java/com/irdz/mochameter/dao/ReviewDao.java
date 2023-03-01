package com.irdz.mochameter.dao;

import android.content.ContextWrapper;

import com.irdz.mochameter.model.entity.Review;
import com.j256.ormlite.dao.Dao;

public interface ReviewDao extends Dao<Review, Integer> {

    Review findByCoffeIdAndUserMacOrLoggedInUser(int id, String  androidId, final ContextWrapper context);

    Review findByCoffeIdAvg(int coffeeId);
}

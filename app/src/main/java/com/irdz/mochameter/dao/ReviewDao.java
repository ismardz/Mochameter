package com.irdz.mochameter.dao;

import android.content.ContextWrapper;

import com.irdz.mochameter.model.entity.Review;
import com.irdz.mochameter.util.CoffeeOrder;
import com.j256.ormlite.dao.Dao;

import java.util.List;

public interface ReviewDao extends Dao<Review, Integer> {

    Review findByCoffeIdAndUserAndroidIdOrLoggedInUser(int id, String  androidId, final ContextWrapper context);

    Review findByCoffeIdAvg(Integer coffeeId);

    List<Review> findByAvgOrderBy(CoffeeOrder order, Boolean reversed, Integer page);

    List<Review> findMyEvaluationsOrderByPaged(CoffeeOrder order, Boolean reversed, Integer page, final String androidId);
}

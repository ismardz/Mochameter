package com.irdz.mochameter.dao;

import com.irdz.mochameter.model.entity.Review;
import com.irdz.mochameter.util.CoffeeOrder;
import com.j256.ormlite.dao.Dao;

import java.util.List;

public interface ReviewDao extends Dao<Review, Integer> {

    Review findByCoffeIdAndUserAndroidIdOrLoggedInUser(int id, String  androidId, Integer userId);

    Review findByCoffeIdAvg(Integer coffeeId);

    List<Review> findByAvgOrderBy(String queryNameBrand, CoffeeOrder order, Boolean reversed, Integer page);

    List<Review> findMyEvaluationsOrderByPaged(String queryNameBrand, CoffeeOrder order, Boolean reversed, Integer page, final String androidId);
}

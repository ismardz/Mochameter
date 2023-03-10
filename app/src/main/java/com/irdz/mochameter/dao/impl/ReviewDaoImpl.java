package com.irdz.mochameter.dao.impl;

import com.google.android.gms.common.util.Strings;
import com.irdz.mochameter.config.AppDatabase;
import com.irdz.mochameter.dao.ReviewDao;
import com.irdz.mochameter.model.entity.Coffee;
import com.irdz.mochameter.model.entity.Review;
import com.irdz.mochameter.model.entity.User;
import com.irdz.mochameter.util.CoffeeOrder;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.NonNull;

public class ReviewDaoImpl extends BaseDaoImpl<Review, Integer> implements ReviewDao {

    private static ReviewDaoImpl instance;

    private static final int PAGE_LIMIT = 10;

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

    public Review findByCoffeIdAndUserAndroidIdOrLoggedInUser(final int coffeeId, final String androidId, final Integer userId) {
        try {
            QueryBuilder<Review, Integer> reviewQueryBuilder = AppDatabase.getInstance().getReviewDao().queryBuilder();
            QueryBuilder<User, Integer> userQueryBuilder = AppDatabase.getInstance().getUserDao().queryBuilder();

            Where<Review, Integer> whereReview = queryBuilder().where()
                .eq("coffee_id", coffeeId);

            if(userId != null) {
                whereReview.and().eq("user_id", userId);
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
    public Review findByCoffeIdAvg(final Integer coffeeId) {
        String query = buildQueryAvg(coffeeId, null, null, null, null);
        try {
            GenericRawResults<String[]> results = AppDatabase.getInstance().getReviewDao().queryRaw(query);
            String[] row = results.getFirstResult();
            return Optional.ofNullable(row)
                .map(this::mapReviewFromRawRow)
                .orElse(null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Review> findByAvgOrderBy(
        final String queryNameBrand,
        final CoffeeOrder order,
        final Boolean reversed,
        final Integer page
    ) {
        String query = buildQueryAvg(null, queryNameBrand, order, reversed, page);
        return getReviewList(query);
    }

    @Override
    public List<Review> findMyEvaluationsOrderByPaged(
        final String queryNameBrand,
        final CoffeeOrder order,
        final Boolean reversed,
        final Integer page,
        final String androidId) {
        String query = "select  coffee_id, aroma, acidity, body, aftertaste, score,  coffee.coffee_name coffee_name,  coffee.brand brand,  coffee.image_url image_url   " +
            " from public.review review, public.coffee coffee, public.user usu " +
            " where review.coffee_id = coffee.id  " +
            " and review.user_id = usu.id  " +
            " and usu.android_id = '" + androidId + "' ";

        query = setFilterNameBrand(queryNameBrand, query);

        query += " group by coffee_id, aroma, acidity, body, aftertaste, score, coffee_name, brand, image_url   ";

        query = setOrderBy(order, reversed, query);

        query = setPage(page, query);
        return getReviewList(query);
    }

    private List<Review> getReviewList(final String query) {
        try {
            GenericRawResults<String[]> results = AppDatabase.getInstance().getReviewDao().queryRaw(query);
            return results.getResults().stream()
                .map(this::mapReviewFromRawRow)
                .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Review mapReviewFromRawRow(final String[] row) {
        Review review = new Review();
        review.setAroma(Double.valueOf(row[1])); // set the aroma using the aroma column
        review.setAcidity(Double.valueOf(row[2])); // set the acidity using the acidity column
        review.setBody(Double.valueOf(row[3])); // set the body using the body column
        review.setAftertaste(Double.valueOf(row[4])); // set the aftertaste using the aftertaste column
        review.setScore(Double.valueOf(row[5])); // set the score using the score column
        review.setCoffee(Coffee.builder()
            .id(Integer.valueOf(row[0]))
                .coffeeName(row[6])
                .brand(row[7])
                .imageUrl(row[8])
            .build()); // set the coffee using the coffee_id column
        return review;
    }

    private String buildQueryAvg(
        final Integer coffeeId,
        final String queryNameBrand,
        final CoffeeOrder order,
        final Boolean reversed,
        final Integer page
    ) {
        String query = "select " +
            " coffee_id, " +
            " avg(aroma) as aroma," +
            " avg(acidity) as acidity," +
            " avg(body) as body," +
            " avg(aftertaste) as aftertaste," +
            " avg(score) as score, " +
            " coffee.coffee_name coffee_name, " +
            " coffee.brand brand, " +
            " coffee.image_url image_url " +
            " from public.review review, public.coffee coffee " +
            " where review.coffee_id = coffee.id ";

        if(coffeeId != null) {
            query += " and coffee_id = " + coffeeId;
        }

        query = setFilterNameBrand(queryNameBrand, query);

        query += " group by coffee_id, coffee_name, brand, image_url ";

        query = setOrderBy(order, reversed, query);

        query = setPage(page, query);

        return query;
    }

    private String setFilterNameBrand(final String queryNameBrand, String query) {
        if(!Strings.isEmptyOrWhitespace(queryNameBrand)) {
            query += " and (lower(coffee.coffee_name) LIKE '%" + queryNameBrand + "%' " +
                " or lower(coffee.brand) LIKE '%" + queryNameBrand + "%') ";
        }
        return query;
    }

    private String setOrderBy(final CoffeeOrder order, final Boolean reversed, String query) {
        if(order != null) {
            query += " order by " + order.getColumn();
            if(reversed != null) {
                query += reversed ? " asc " : " desc ";
            }
        }
        return query;
    }

    private String setPage(final Integer page, String query) {
        if(page != null) {
            query += " LIMIT " + PAGE_LIMIT + " OFFSET " + page * PAGE_LIMIT;
        }
        return query;
    }
}

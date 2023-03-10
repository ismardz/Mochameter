package com.irdz.mochameter.dao.impl;

import com.irdz.mochameter.config.AppDatabase;
import com.irdz.mochameter.dao.CoffeeDao;
import com.irdz.mochameter.model.entity.Coffee;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import lombok.NonNull;

public class CoffeeDaoImpl extends BaseDaoImpl<Coffee, Integer> implements CoffeeDao {

    private static CoffeeDaoImpl instance;

    private CoffeeDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Coffee.class);
    }

    @NonNull
    public static CoffeeDaoImpl getInstance(ConnectionSource connectionSource) {
        if (instance == null) {
            try {
                instance = new CoffeeDaoImpl(connectionSource);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    @Override
    public Coffee findByBarcode(final String barcode) {
        try {
            QueryBuilder<Coffee, Integer> coffeeQueryBuilder = AppDatabase.getInstance().getCoffeeDao().queryBuilder();

            Where<Coffee, Integer> where = queryBuilder().where().eq("barcode", barcode);
            coffeeQueryBuilder.setWhere(where);

            return coffeeQueryBuilder.queryForFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

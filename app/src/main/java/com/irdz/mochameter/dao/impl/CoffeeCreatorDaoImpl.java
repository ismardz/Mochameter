package com.irdz.mochameter.dao.impl;

import com.irdz.mochameter.dao.CoffeeCreatorDao;
import com.irdz.mochameter.model.entity.CoffeeCreator;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import lombok.NonNull;

public class CoffeeCreatorDaoImpl extends BaseDaoImpl<CoffeeCreator, Integer> implements CoffeeCreatorDao {

    private static CoffeeCreatorDaoImpl instance;

    private CoffeeCreatorDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, CoffeeCreator.class);
    }

    @NonNull
    public static CoffeeCreatorDaoImpl getInstance(ConnectionSource connectionSource) {
        if (instance == null) {
            try {
                instance = new CoffeeCreatorDaoImpl(connectionSource);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

}

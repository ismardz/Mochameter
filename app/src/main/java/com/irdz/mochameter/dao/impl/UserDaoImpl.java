package com.irdz.mochameter.dao.impl;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContextWrapper;
import android.content.SharedPreferences;

import com.irdz.mochameter.dao.UserDao;
import com.irdz.mochameter.model.entity.User;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Optional;

import lombok.NonNull;

public class UserDaoImpl extends BaseDaoImpl<User, Integer> implements UserDao {

    private static UserDaoImpl instance;

    private UserDaoImpl(final ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, User.class);
    }

    @NonNull
    public static UserDaoImpl getInstance(final ConnectionSource connectionSource) {
        if (instance == null) {
            try {
                instance = new UserDaoImpl(connectionSource);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public static Integer getLoggedInUserId(final ContextWrapper contextWrapper) {
        SharedPreferences sp1 = contextWrapper.getSharedPreferences("Login", MODE_PRIVATE);
        String userID = sp1.getString("userID", null);
        return Optional.ofNullable(userID)
                    .map(Integer::valueOf)
                    .orElse(null);
    }
}
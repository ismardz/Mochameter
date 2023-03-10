package com.irdz.mochameter.dao.impl;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.provider.Settings;

import com.irdz.mochameter.config.AppDatabase;
import com.irdz.mochameter.dao.UserDao;
import com.irdz.mochameter.model.entity.User;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Optional;

import lombok.NonNull;

public class UserDaoImpl extends BaseDaoImpl<User, Integer> implements UserDao {

    public static final String PREFERENCES_MOCHAMETER = "LoginMM";
    public static final String USER_ID = "userID";
    public static final String ANDROID_ID = "androidID";
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

    @Override
    public User findByAndroidId(final String androidId) {
        try {
            QueryBuilder<User, Integer> coffeeQueryBuilder = AppDatabase.getInstance().getUserDao().queryBuilder();

            Where<User, Integer> where = queryBuilder().where().eq("android_id", androidId);
            coffeeQueryBuilder.setWhere(where);

            return coffeeQueryBuilder.queryForFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Integer getLoggedInUserId(final ContextWrapper contextWrapper) {
        SharedPreferences sp1 = contextWrapper.getSharedPreferences(PREFERENCES_MOCHAMETER, MODE_PRIVATE);
        String userID = sp1.getString(USER_ID, null);
        return Optional.ofNullable(userID)
                    .map(Integer::valueOf)
                    .orElse(null);
    }

    public static String getAndroidId(final ContextWrapper contextWrapper) {
        SharedPreferences sharedPreferences = contextWrapper.getSharedPreferences(PREFERENCES_MOCHAMETER, MODE_PRIVATE);
        String androidID = sharedPreferences.getString(ANDROID_ID, null);
        return Optional.ofNullable(androidID)
            .orElseGet(() -> storeAndroidId(contextWrapper, sharedPreferences));
    }

    private static String storeAndroidId(final ContextWrapper contextWrapper, final SharedPreferences sharedPreferences) {
        String androidID = Settings.Secure.getString(contextWrapper.getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(ANDROID_ID, String.valueOf(androidID));
        edit.commit();
        return androidID;
    }
}

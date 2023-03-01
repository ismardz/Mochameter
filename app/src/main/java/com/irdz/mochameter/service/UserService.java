package com.irdz.mochameter.service;

import com.irdz.mochameter.config.AppDatabase;
import com.irdz.mochameter.model.entity.User;
import com.irdz.mochameter.util.ExecutorUtils;
import com.irdz.mochameter.util.UserType;

import java.sql.SQLException;
import java.util.Optional;

public class UserService {

    private static UserService instance;

    public static UserService getInstance() {
        if(instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public User findByAndroidIdOrCreateIt(final String androidId) {
        final User[] user = {null};
        ExecutorUtils.runCallables(() -> {
            User userByAndroidId = AppDatabase.getInstance().userDao.findByAndroidId(androidId);
            user[0] = Optional.ofNullable(userByAndroidId)
                .orElseGet(() -> {
                    try {
                        return AppDatabase.getInstance().userDao.createIfNotExists(createUserWithAndroidId(androidId));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            return null;
        });
        return user[0];
    }

    private User createUserWithAndroidId(final String androidId) {
        return User.builder()
            .androidId(androidId)
            .typeu(UserType.ANDROID_ID.getValue())
            .build();
    }
}

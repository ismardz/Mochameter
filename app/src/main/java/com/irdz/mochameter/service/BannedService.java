package com.irdz.mochameter.service;

import com.irdz.mochameter.config.AppDatabase;
import com.irdz.mochameter.util.ExecutorUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public class BannedService {

    private static BannedService instance;

    public static BannedService getInstance() {
        if(instance == null) {
            instance = new BannedService();
        }
        return instance;
    }

    public boolean isBannedUser(final String androidId, final Integer userId) {
        AtomicBoolean banned = new AtomicBoolean(false);

        ExecutorUtils.runCallables(() -> {
            banned.set(AppDatabase.getInstance().getBannedDao().isBannedUser(androidId, userId));
            return null;
        });

        return banned.get();
    }
}

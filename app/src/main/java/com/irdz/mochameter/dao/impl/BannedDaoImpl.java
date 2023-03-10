package com.irdz.mochameter.dao.impl;

import com.irdz.mochameter.config.AppDatabase;
import com.irdz.mochameter.dao.BannedDao;
import com.irdz.mochameter.model.entity.Banned;
import com.irdz.mochameter.model.entity.User;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import lombok.NonNull;

public class BannedDaoImpl extends BaseDaoImpl<Banned, Integer> implements BannedDao {

    private static BannedDaoImpl instance;

    private BannedDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Banned.class);
    }

    @NonNull
    public static BannedDaoImpl getInstance(ConnectionSource connectionSource) {
        if (instance == null) {
            try {
                instance = new BannedDaoImpl(connectionSource);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    @Override
    public boolean isBannedUser(final String androidId, final Integer userId) {
        boolean banned;
        try {
            QueryBuilder<Banned, Integer> bannedQueryBuilder = AppDatabase.getInstance().getBannedDao().queryBuilder();
            Date date = getCurrentDate();
            Where<Banned, Integer> whereBanned = bannedQueryBuilder.where()
                .le("date_from", date)
                .and().ge("date_until", date);
            if(userId != null) {
                whereBanned.and().eq("user_id", userId);
            } else {
                QueryBuilder<User, Integer> userQueryBuilder = AppDatabase.getInstance().getUserDao().queryBuilder();
                Where<User, Integer> whereUser = userQueryBuilder.where()
                    .eq("android_id", androidId);
                userQueryBuilder.setWhere(whereUser);

                bannedQueryBuilder.join(userQueryBuilder);
            }

            bannedQueryBuilder.setWhere(whereBanned);

            banned = bannedQueryBuilder.countOf() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return banned;
    }

    @androidx.annotation.NonNull
    private Date getCurrentDate() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        date = cal.getTime();
        return date;
    }
}

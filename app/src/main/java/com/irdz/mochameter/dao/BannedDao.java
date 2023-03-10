package com.irdz.mochameter.dao;

import com.irdz.mochameter.model.entity.Banned;
import com.j256.ormlite.dao.Dao;

public interface BannedDao extends Dao<Banned, Integer> {

    boolean isBannedUser(final String androidId, final Integer userId);
}

package com.irdz.mochameter.dao;

import com.irdz.mochameter.model.entity.User;
import com.j256.ormlite.dao.Dao;

public interface UserDao extends Dao<User, Integer> {

    User findByAndroidId(String androidId);
}

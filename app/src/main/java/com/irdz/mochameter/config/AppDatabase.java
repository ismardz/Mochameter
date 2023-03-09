package com.irdz.mochameter.config;

import com.irdz.mochameter.dao.CoffeeCreatorDao;
import com.irdz.mochameter.dao.CoffeeDao;
import com.irdz.mochameter.dao.ReviewDao;
import com.irdz.mochameter.dao.UserDao;
import com.irdz.mochameter.dao.impl.CoffeeCreatorDaoImpl;
import com.irdz.mochameter.dao.impl.CoffeeDaoImpl;
import com.irdz.mochameter.dao.impl.ReviewDaoImpl;
import com.irdz.mochameter.dao.impl.UserDaoImpl;
import com.irdz.mochameter.util.ExecutorUtils;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

public class AppDatabase {

    private static final String USER = "isma";
    private static final String PASS = "Qj9GJgu3-vtbgk93VwReww";
    private static final String URL = "jdbc:postgresql://board-gamers-5934.7tc.cockroachlabs.cloud:26257/mochameter?" +
            "ssl=true&" +
            "sslfactory=org.postgresql.ssl.NonValidatingFactory&";

    private static AppDatabase instance;
    public ConnectionSource connection;
    public UserDao userDao;
    public CoffeeDao coffeeDao;
    public ReviewDao reviewDao;
    public CoffeeCreatorDao coffeeCreatorDao;

    public static AppDatabase getInstance() {
        if (instance == null) {
            try {
                createInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return instance;
    }

    private static void createInstance() {
        ExecutorUtils.runCallables(() -> {
            try {
                ConnectionSource connection = new JdbcConnectionSource(URL, USER, PASS);
                instance = new AppDatabase();
                instance.connection = connection;
                instance.userDao = UserDaoImpl.getInstance(connection);
                instance.coffeeDao = CoffeeDaoImpl.getInstance(connection);
                instance.reviewDao = ReviewDaoImpl.getInstance(connection);
                instance.coffeeCreatorDao = CoffeeCreatorDaoImpl.getInstance(connection);
                return instance;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

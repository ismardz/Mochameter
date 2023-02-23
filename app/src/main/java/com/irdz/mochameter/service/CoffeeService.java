package com.irdz.mochameter.service;

import com.irdz.mochameter.config.AppDatabase;
import com.irdz.mochameter.model.entity.Coffee;
import com.irdz.mochameter.util.ExecutorUtils;

public class CoffeeService {

    private static CoffeeService instance;

    public static CoffeeService getInstance() {
        if(instance == null) {
            instance = new CoffeeService();
        }
        return instance;
    }

    public Coffee findByBarcode(final String barcode) {
        return ExecutorUtils.runCallables(() -> AppDatabase.getInstance().coffeeDao.findByBarcode(barcode))
            .stream().findFirst()
            .orElse(null);
    }
}

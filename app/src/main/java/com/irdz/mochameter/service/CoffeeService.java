package com.irdz.mochameter.service;

import com.irdz.mochameter.config.AppDatabase;
import com.irdz.mochameter.model.entity.Coffee;
import com.irdz.mochameter.model.openfoodfacts.OpenFoodFactsResponse;
import com.irdz.mochameter.util.ExecutorUtils;

import java.util.concurrent.atomic.AtomicReference;

public class CoffeeService {

    private static CoffeeService instance;

    public static CoffeeService getInstance() {
        if(instance == null) {
            instance = new CoffeeService();
        }
        return instance;
    }

    public Coffee findByBarcode(final String barcode) {
        final Coffee[] coffee = {null};
        ExecutorUtils.runCallables(() -> {
                coffee[0] = AppDatabase.getInstance().coffeeDao.findByBarcode(barcode);
                return coffee[0];
            });
        return coffee[0];
    }

    public Coffee findById(final Integer id) {
        final Coffee[] coffee = {null};
        ExecutorUtils.runCallables(() -> {
            coffee[0] = AppDatabase.getInstance().coffeeDao.queryForId(id);
            return coffee[0];
        });
        return coffee[0];
    }



    public Coffee insert(final OpenFoodFactsResponse coffeeDetail) {
        AtomicReference<Coffee> coffee = new AtomicReference<>(Coffee.builder()
            .barcode(coffeeDetail.code)
            .brand(coffeeDetail.product.brands)
            .coffeeName(coffeeDetail.product.product_name)
            .imageUrl(coffeeDetail.product.image_front_url)
            .build());
        ExecutorUtils.runCallables(() -> {
            coffee.set(AppDatabase.getInstance().coffeeDao.createIfNotExists(coffee.get()));
            return null;
        });
        return coffee.get();
    }
}

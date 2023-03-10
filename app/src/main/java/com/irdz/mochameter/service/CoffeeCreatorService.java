package com.irdz.mochameter.service;

import com.irdz.mochameter.config.AppDatabase;
import com.irdz.mochameter.model.entity.CoffeeCreator;
import com.irdz.mochameter.util.ExecutorUtils;

import java.util.concurrent.atomic.AtomicReference;

public class CoffeeCreatorService {

    private static CoffeeCreatorService instance;

    public static CoffeeCreatorService getInstance() {
        if(instance == null) {
            instance = new CoffeeCreatorService();
        }
        return instance;
    }

    public CoffeeCreator insert(final String androidId, final String barcode) {
        AtomicReference<CoffeeCreator> coffee = new AtomicReference<>(CoffeeCreator.builder()
            .androidId(androidId)
            .barcode(barcode)
            .build());
        ExecutorUtils.runCallables(() -> {
            coffee.set(AppDatabase.getInstance().getCoffeeCreatorDao().createIfNotExists(coffee.get()));
            return null;
        });
        return coffee.get();
    }
}

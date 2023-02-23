package com.irdz.mochameter.dao;

import com.irdz.mochameter.model.entity.Coffee;
import com.j256.ormlite.dao.Dao;

public interface CoffeeDao extends Dao<Coffee, Integer> {

    Coffee findByBarcode(String barcode);
}

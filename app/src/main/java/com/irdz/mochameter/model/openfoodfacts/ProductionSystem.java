package com.irdz.mochameter.model.openfoodfacts;

import java.io.Serializable;
import java.util.List;
public class ProductionSystem implements Serializable {
    public List<Object> labels;
    public int value;
    public String warning;
}

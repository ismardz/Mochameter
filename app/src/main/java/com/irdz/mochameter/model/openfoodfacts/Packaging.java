package com.irdz.mochameter.model.openfoodfacts;

import java.io.Serializable;
import java.util.List;
public class Packaging implements Serializable {
    public int non_recyclable_and_non_biodegradable_materials;
    public List<Packaging> packagings;
    public int score;
    public int value;
}

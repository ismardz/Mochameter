package com.irdz.mochameter.model.openfoodfacts;

import java.io.Serializable;

public class Ingredient implements Serializable {
    public String id;
    public double percent_estimate;
    public double percent_max;
    public double percent_min;
    public int rank;
    public String text;
    public String geometry;
    public String imgid;
    public String normalize;
    public String rev;
    public Sizes sizes;
    public String white_magic;
    public Display display;
    public Small small;
    public Thumb thumb;
}

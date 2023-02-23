package com.irdz.mochameter.model.openfoodfacts;

import java.io.Serializable;

public class Agribalyse implements Serializable {
    public String agribalyse_food_code;
    public double co2_agriculture;
    public double co2_consumption;
    public double co2_distribution;
    public double co2_packaging;
    public double co2_processing;
    public double co2_total;
    public double co2_transportation;
    public String code;
    public String dqr;
    public double ef_agriculture;
    public double ef_consumption;
    public double ef_distribution;
    public double ef_packaging;
    public double ef_processing;
    public double ef_total;
    public double ef_transportation;
    public int is_beverage;
    public String name_en;
    public String name_fr;
    public int score;
    public String version;
}

package com.irdz.mochameter.model.openfoodfacts;

import java.io.Serializable;

public class AggregatedOrigin implements Serializable {
    public String epi_score;
    public String origin;
    public double percent;
    public Object transportation_score;
}

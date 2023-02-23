package com.irdz.mochameter.model.openfoodfacts;

import java.io.Serializable;
import java.util.List;
public class OriginsOfIngredients implements Serializable {
    public List<AggregatedOrigin> aggregated_origins;
    public int epi_score;
    public int epi_value;
    public List<String> origins_from_origins_field;
    public int transportation_score;
    public TransportationScores transportation_scores;
    public int transportation_value;
    public TransportationValues transportation_values;
    public int value;
    public Values values;
    public String warning;
}

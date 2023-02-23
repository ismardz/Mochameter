package com.irdz.mochameter.model.openfoodfacts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Nutriments implements Serializable {

    @JsonProperty("fruits-vegetables-nuts-estimate-from-ingredients_100g")
    public int fruits_vegetables_nuts_estimate_from_ingredients_100g;
    @JsonProperty("fruits-vegetables-nuts-estimate-from-ingredients_serving")
    public int fruits_vegetables_nuts_estimate_from_ingredients_serving;
}

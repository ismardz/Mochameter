package com.irdz.mochameter.model.openfoodfacts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class CategoriesProperties implements Serializable {

    @JsonProperty("agribalyse_food_code:en")
    public String agribalyse_food_code_en;
    @JsonProperty("ciqual_food_code:en")
    public String ciqual_food_code_en;
}

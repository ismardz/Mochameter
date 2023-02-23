package com.irdz.mochameter.model.openfoodfacts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class CategoryProperties implements Serializable {
    @JsonProperty("ciqual_food_name:en")
    public String ciqual_food_name_en;
    @JsonProperty("ciqual_food_name:fr")
    public String ciqual_food_name_fr;
}

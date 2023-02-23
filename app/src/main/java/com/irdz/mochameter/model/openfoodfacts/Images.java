package com.irdz.mochameter.model.openfoodfacts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Images implements Serializable {
    @JsonProperty("1") 
    public _1 _1;
    @JsonProperty("2") 
    public _2 _2;
    @JsonProperty("3") 
    public _3 _3;
    @JsonProperty("4") 
    public _4 _4;
    @JsonProperty("5") 
    public _5 _5;
    public Front front;
    public FrontEs front_es;
    public Ingredient ingredients;
    public IngredientsEs ingredients_es;
    public Nutrition nutrition;
    public NutritionEs nutrition_es;
}

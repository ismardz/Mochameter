package com.irdz.mochameter.model.openfoodfacts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Sizes implements Serializable {
    @JsonProperty("100") 
    public _100 _100;
    @JsonProperty("400") 
    public _400 _400;
    public Full full;
    @JsonProperty("200") 
    public _200 _200;
}

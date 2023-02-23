package com.irdz.mochameter.model.openfoodfacts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Languages implements Serializable {
    @JsonProperty("en:spanish")
    public int en_spanish;
}

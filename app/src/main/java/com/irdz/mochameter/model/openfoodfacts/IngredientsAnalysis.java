package com.irdz.mochameter.model.openfoodfacts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;
public class IngredientsAnalysis implements Serializable {

    @JsonProperty("en:palm-oil-content-unknown")
    public List<String> en_palm_oil_content_unknown;
    @JsonProperty("en:vegan-status-unknown")
    public List<String> en_vegan_status_unknown;
    @JsonProperty("en:vegetarian-status-unknown")
    public List<String> en_vegetarian_status_unknown;
}

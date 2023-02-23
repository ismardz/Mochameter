package com.irdz.mochameter.model.openfoodfacts;

import java.io.Serializable;

public class OpenFoodFactsResponse  implements Serializable {
    public String code;
    public Product product;
    public int status;
    public String status_verbose;
}

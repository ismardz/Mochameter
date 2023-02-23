package com.irdz.mochameter.model.openfoodfacts;

import java.io.Serializable;

public class Adjustments implements Serializable {
    public OriginsOfIngredients origins_of_ingredients;
    public Packaging packaging;
    public ProductionSystem production_system;
    public ThreatenedSpecies threatened_species;
}

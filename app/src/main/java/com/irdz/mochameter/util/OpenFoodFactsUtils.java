package com.irdz.mochameter.util;

import com.irdz.mochameter.model.openfoodfacts.OpenFoodFactsResponse;

import java.util.Optional;

public class OpenFoodFactsUtils {

    public static String getImageUrl(final OpenFoodFactsResponse coffeeDetail) {
        return Optional.ofNullable(coffeeDetail.product.image_front_url)
            .orElseGet(() -> Optional.ofNullable(coffeeDetail.product.image_ingredients_url)
                .orElseGet(() -> coffeeDetail.product.image_nutrition_url));
    }
}

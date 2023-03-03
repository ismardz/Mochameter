package com.irdz.mochameter.util;

import com.irdz.mochameter.R;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CoffeeOrder {

    GLOBAL_SCORE(R.string.score, "score"),
    AROMA(R.string.aroma, "aroma"),
    ACIDITY(R.string.acidity, "acidity"),
    BODY(R.string.body, "body"),
    AFTERTASTE(R.string.aftertaste, "aftertaste");

    private int resourceText;
    private String column;

}

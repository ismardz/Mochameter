package com.irdz.mochameter.util;

import com.irdz.mochameter.R;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Score {

    A("a", R.drawable.nutriscore_a, R.drawable.ecoscore_a),
    B("b", R.drawable.nutriscore_b, R.drawable.ecoscore_b),
    C("c", R.drawable.nutriscore_c, R.drawable.ecoscore_c),
    D("d", R.drawable.nutriscore_d, R.drawable.ecoscore_d),
    E("e", R.drawable.nutriscore_e, R.drawable.ecoscore_e);

    private String grade;
    private int nutriscoreResource;
    private int ecocoreResource;

    public static Integer getNutriscoreResourceByGrade(final String grade) {
        if(grade == null) {
            return null;
        }
        return Arrays.stream(values())
            .filter(score -> score.getGrade().equalsIgnoreCase(grade))
            .findFirst().map(Score::getNutriscoreResource)
            .orElse(null);
    }

    public static Integer getEcocoreResourceByGrade(final String grade) {
        if(grade == null) {
            return null;
        }
        return Arrays.stream(values())
            .filter(score -> score.getGrade().equalsIgnoreCase(grade))
            .findFirst().map(Score::getEcocoreResource)
            .orElse(null);
    }

}

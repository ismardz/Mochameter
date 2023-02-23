package com.irdz.mochameter.model.openfoodfacts;

import java.io.Serializable;

public class EcoscoreData implements Serializable {
    public Adjustments adjustments;
    public Agribalyse agribalyse;
    public String grade;
    public Grades grades;
    public Missing missing;
    public int missing_data_warning;
    public PreviousData previous_data;
    public int score;
    public Scores scores;
    public String status;
}

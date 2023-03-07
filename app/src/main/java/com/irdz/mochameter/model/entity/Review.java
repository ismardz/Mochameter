package com.irdz.mochameter.model.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "review")
public class Review implements Serializable {

    @DatabaseField(generatedIdSequence = "review_id_seq") private Integer id;
    @DatabaseField(foreign = true, foreignColumnName = "id", columnName = "user_id") private User user;
    @DatabaseField(foreign = true, foreignColumnName = "id", columnName = "coffee_id") private Coffee coffee;
    @DatabaseField private Double aroma;
    @DatabaseField private Double acidity;
    @DatabaseField private Double body;
    @DatabaseField private Double aftertaste;
    @DatabaseField private Double score;

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Review)) {
            return false;
        }
        Review r = (Review) o;
        return aroma.compareTo(r.getAroma()) == 0 && acidity.compareTo(r.getAcidity()) == 0 && body.compareTo(r.getBody()) == 0 &&
            aftertaste.compareTo(r.getAftertaste()) == 0 && score.compareTo(r.getScore()) == 0;
    }


}

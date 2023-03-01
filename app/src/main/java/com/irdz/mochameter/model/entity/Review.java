package com.irdz.mochameter.model.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "review")
public class Review {

    @DatabaseField(generatedIdSequence = "review_id_seq") private Integer id;
    @DatabaseField(foreign = true, foreignColumnName = "id", columnName = "user_id") private User user;
    @DatabaseField(foreign = true, foreignColumnName = "id", columnName = "coffee_id") private Coffee coffee;
    @DatabaseField private Double aroma;
    @DatabaseField private Double acidity;
    @DatabaseField private Double body;
    @DatabaseField private Double aftertaste;
    @DatabaseField private Double score;
}

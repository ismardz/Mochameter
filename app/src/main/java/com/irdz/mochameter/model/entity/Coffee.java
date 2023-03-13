package com.irdz.mochameter.model.entity;

import com.irdz.mochameter.util.ValidationUtils;
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
@DatabaseTable(tableName = "coffee")
public class Coffee implements Serializable {

    @DatabaseField(generatedIdSequence = "coffee_id_seq") private Integer id;
    @DatabaseField(columnName = "coffee_name") private String coffeeName;
    @DatabaseField private String brand;
    @DatabaseField private String barcode;
    @DatabaseField(columnName = "image_url")  private String imageUrl;

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }

        if(!(o instanceof Coffee)) {
            return false;
        }
        Coffee c = (Coffee) o;
        return ValidationUtils.stringEquals(brand, c.getBrand()) &&
            ValidationUtils.stringEquals(coffeeName, c.getCoffeeName()) &&
            ValidationUtils.stringEquals(imageUrl, c.getImageUrl());
    }

}

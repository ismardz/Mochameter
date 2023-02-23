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
@DatabaseTable(tableName = "coffee")
public class Coffee {

    @DatabaseField(generatedIdSequence = "coffee_id_seq") private int id;
    @DatabaseField(columnName = "coffee_name") private String coffeeName;
    @DatabaseField private String brand;
    @DatabaseField private String barcode;

}

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
@DatabaseTable(tableName = "coffee")
public class Coffee implements Serializable {

    @DatabaseField(generatedIdSequence = "coffee_id_seq") private Integer id;
    @DatabaseField(columnName = "coffee_name") private String coffeeName;
    @DatabaseField private String brand;
    @DatabaseField private String barcode;

}

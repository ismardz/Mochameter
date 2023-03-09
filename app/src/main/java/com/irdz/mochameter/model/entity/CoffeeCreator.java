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
@DatabaseTable(tableName = "coffee_creators")
public class CoffeeCreator implements Serializable {

    @DatabaseField(generatedIdSequence = "coffee_creators_id_seq") private Integer id;
    @DatabaseField(columnName = "android_id") private String androidId;
    @DatabaseField(columnName = "barcode") private String barcode;

}

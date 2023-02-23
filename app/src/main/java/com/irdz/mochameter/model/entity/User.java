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
@DatabaseTable(tableName = "user")
public class User {

    @DatabaseField(generatedIdSequence = "user_id_seq") private int id;
    @DatabaseField private String email;
    @DatabaseField private String fullname;
    @DatabaseField private String password;
    @DatabaseField(columnName = "mac_address") private String macAddress;
    @DatabaseField private Integer typeu;
}

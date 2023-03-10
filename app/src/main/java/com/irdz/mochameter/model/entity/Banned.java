package com.irdz.mochameter.model.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DatabaseTable(tableName = "banned")
public class Banned {

    @DatabaseField(generatedIdSequence = "banned_id_seq") private Integer id;
    @DatabaseField(foreign = true, foreignColumnName = "id", columnName = "user_id") private User user;
    @DatabaseField(columnName = "date_from") private Date from;
    @DatabaseField(columnName = "date_until") private Date until;
}

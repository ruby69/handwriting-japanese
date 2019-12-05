package com.appskimo.app.japanese.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Collection;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(of = {"dictionaryUid", "name"})
@DatabaseTable(tableName = "Dictionary")
public class Dictionary implements Serializable {
    private static final long serialVersionUID = 4961022240058150457L;

    public static final int UID_FINDLIST = 10000;
    public static final int UID_CHECKLIST = 10001;
    public static final String FIELD_dictionaryUid = "dictionaryUid";

    @DatabaseField(columnName = FIELD_dictionaryUid, id = true, generatedId = false) Integer dictionaryUid;
    @DatabaseField String name;
    @ForeignCollectionField Collection<DictionaryWord> dictionaryWords;

    public Dictionary(Integer dictionaryUid) {
        this.dictionaryUid = dictionaryUid;
    }
}

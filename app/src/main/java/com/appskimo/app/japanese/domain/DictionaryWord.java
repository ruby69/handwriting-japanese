package com.appskimo.app.japanese.domain;

import android.view.View;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(of = {"dictionaryWordUid"})
@DatabaseTable(tableName = "DictionaryWord")
public class DictionaryWord implements Serializable {
    private static final long serialVersionUID = 66295126840162040L;

    public static final String FIELD_dictionaryWordUid = "dictionaryWordUid";
    public static final String FIELD_dictionaryUid = Dictionary.FIELD_dictionaryUid;
    public static final String FIELD_wordUid = Word.FIELD_wordUid;
    public static final String FIELD_complete = "complete";

    @DatabaseField(columnName = FIELD_dictionaryWordUid, id = true, generatedId = false) Integer dictionaryWordUid;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = FIELD_dictionaryUid) Dictionary dictionary;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = FIELD_wordUid) Word word;
    @DatabaseField private boolean complete;

    transient private View viewHolder;
    private boolean open;

    public DictionaryWord(Integer dictionaryWordUid) {
        this.dictionaryWordUid = dictionaryWordUid;
    }
}

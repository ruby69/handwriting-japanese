package com.appskimo.app.japanese.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
@DatabaseTable(tableName = "gameRecord")
public class GameRecord implements Serializable {
    private static final long serialVersionUID = -2296676738579173345L;

    public static final String FIELD_gameRecordId = "game_record_id";
    public static final String FIELD_gameId = "gameId";
    public static final String FIELD_dictionaryId = "dictionary_id";
    public static final String FIELD_wordCount = "wordCount";
    public static final String FIELD_recordTime = "recordTime";
    public static final String FIELD_maxComboCount = "maxComboCount";
    public static final String FIELD_score = "score";
    public static final String FIELD_registTime = "registTime";

    @DatabaseField(columnName = "game_record_id", generatedId = true) private Integer gameRecordId;
    @DatabaseField private Integer gameId;
    @DatabaseField(foreign=true, foreignAutoRefresh=true, columnName = "dictionary_id") private Dictionary dictionary;
    @DatabaseField private int wordCount;
    @DatabaseField private long recordTime;
    @DatabaseField private int maxComboCount;
    @DatabaseField private long score;

    @DatabaseField private Long userUid;
    @DatabaseField private String userId;
    @DatabaseField private String userNick;
    @DatabaseField private String comment;

    @DatabaseField private Date registTime;
}

package com.appskimo.app.japanese.support;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.appskimo.app.japanese.BuildConfig;
import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.domain.Dictionary;
import com.appskimo.app.japanese.domain.DictionaryWord;
import com.appskimo.app.japanese.domain.GameRecord;
import com.appskimo.app.japanese.domain.Word;
import com.appskimo.app.japanese.event.OnCompleteOrmLite;
import com.appskimo.app.japanese.event.ProgressMessage;
import com.appskimo.app.japanese.service.PrefsService_;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SQLiteOpenHelper extends OrmLiteSqliteOpenHelper {
    private Context context;
    private PrefsService_ prefs;

    public SQLiteOpenHelper(Context context) {
        super(context, context.getString(R.string.db_name), null, context.getResources().getInteger(R.integer.db_version));
        this.context = context;
    }

    public SQLiteOpenHelper setPrefs(PrefsService_ prefs) {
        this.prefs = prefs;
        return this;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            prefs.initializedDbStatus().put(1);

            dropAndCreateTables(connectionSource);
            initialize(database);

            prefs.initializedDbStatus().put(2);
            prefs.initializeDatabase().put(true);
        } catch (Exception e) {
            prefs.initializedDbStatus().put(3);
            prefs.initializeDatabase().put(false);
            if (BuildConfig.DEBUG) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
        } finally {
            EventBus.getDefault().post(new OnCompleteOrmLite());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            prefs.initializedDbStatus().put(1);

            dropAndCreateTables(connectionSource);
            initialize(database);

            prefs.initializedDbStatus().put(2);
            prefs.initializeDatabase().put(true);
        } catch (Exception e) {
            prefs.initializedDbStatus().put(3);
            prefs.initializeDatabase().put(false);
            if (BuildConfig.DEBUG) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
        } finally {
            EventBus.getDefault().post(new OnCompleteOrmLite());
        }
    }

    private void dropAndCreateTables(ConnectionSource connectionSource) throws Exception {
        TableUtils.dropTable(connectionSource, GameRecord.class, true);
        TableUtils.dropTable(connectionSource, DictionaryWord.class, true);
        TableUtils.dropTable(connectionSource, Word.class, true);
        TableUtils.dropTable(connectionSource, Dictionary.class, true);

        TableUtils.createTable(connectionSource, Dictionary.class);
        TableUtils.createTable(connectionSource, Word.class);
        TableUtils.createTable(connectionSource, DictionaryWord.class);
        TableUtils.createTable(connectionSource, GameRecord.class);
    }

    private void initialize(SQLiteDatabase database) throws Exception {
        initializeDictionaries(database);
        initializeWords(database);
        initializeDictionaryWords(database);
    }

    private void initializeDictionaries(SQLiteDatabase database) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.dict), "utf-8"));
        SQLiteStatement statement = null;
        try {
            database.beginTransaction();

            statement = database.compileStatement(context.getString(R.string.sql_dictionary_insert));
            JSONArray jsonArr = new JSONArray(br.readLine());
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONArray itemArray = (JSONArray) jsonArr.get(i);
                statement.bindLong(1, itemArray.getLong(0));
                statement.bindString(2, itemArray.getString(1));
                statement.execute();
            }
            statement.close();
            database.setTransactionSuccessful();
        } finally {
            if (statement != null) {
                statement.close();
            }

            if (database.inTransaction()) {
                database.endTransaction();
            }

            if (br != null) {
                br.close();
            }
        }
    }

    private void initializeWords(SQLiteDatabase database) throws Exception {
        BufferedReader br = null;
        SQLiteStatement statement = null;
        try {
            database.beginTransaction();
            statement = database.compileStatement(context.getString(R.string.sql_word_insert));

            int[] arr = new int[]{R.raw.word1};
            for(int j = 0; j<arr.length; j++) {
                int contentResId = arr[j];
                EventBus.getDefault().post(new ProgressMessage(context.getResources().getString(R.string.message_progress_update) + " ::: " + (j+1) + "/" + arr.length));

                br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(contentResId), "utf-8"));
                String str = br.readLine();
                if (str != null) {
                    JSONArray jsonArr = new JSONArray(str);
                    for(int i = 0; i<jsonArr.length(); i++) {
                        JSONArray itemArray = (JSONArray) jsonArr.get(i);
                        statement.bindLong(1, itemArray.getLong(0));
                        statement.bindString(2, itemArray.getString(1));
                        statement.bindString(3, itemArray.getString(2));
                        statement.bindString(4, itemArray.getString(3));
                        statement.bindString(5, itemArray.getString(4));
                        statement.bindString(6, itemArray.getString(5));
                        statement.bindString(7, itemArray.getString(6));
                        statement.bindString(8, itemArray.getString(7));
                        statement.bindString(9, itemArray.getString(8));
                        statement.bindString(10, itemArray.getString(9));
                        statement.bindString(11, itemArray.getString(10));
                        statement.bindString(12, itemArray.getString(11));
                        statement.bindString(13, itemArray.getString(12));
                        statement.bindString(14, itemArray.getString(13));
                        statement.bindString(15, itemArray.getString(14));
                        statement.bindString(16, itemArray.getString(15));
                        statement.bindString(17, itemArray.getString(16));
                        statement.bindString(18, itemArray.getString(17));
                        statement.bindString(19, itemArray.getString(18));
                        statement.bindString(20, itemArray.getString(19));
                        statement.bindString(21, itemArray.getString(20));
                        statement.bindString(22, itemArray.getString(21));
                        statement.bindString(23, itemArray.getString(22));
                        statement.bindString(24, itemArray.getString(23));
                        statement.bindString(25, itemArray.getString(24));
                        statement.bindString(26, itemArray.getString(25));
                        statement.bindString(27, itemArray.getString(26));
                        statement.bindString(28, itemArray.getString(27));
                        statement.bindString(29, itemArray.getString(28));
                        statement.bindString(30, itemArray.getString(29));
                        statement.bindString(31, itemArray.getString(30));
                        statement.bindString(32, itemArray.getString(31));
                        statement.execute();
                    }
                }
            }

            statement.close();
            database.setTransactionSuccessful();

        } finally {
            if (statement != null) {
                statement.close();
            }

            if (database.inTransaction()) {
                database.endTransaction();
            }

            if (br != null) {
                br.close();
            }
        }
    }

    private void initializeDictionaryWords(SQLiteDatabase database) throws Exception {
        BufferedReader br = null;
        SQLiteStatement statement = null;
        try {
            br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.dict_word), "utf-8"));
            String str = br.readLine();
            if (str != null) {
                JSONArray jsonArr = new JSONArray(str);

                database.beginTransaction();
                statement = database.compileStatement(context.getString(R.string.sql_dictionaryword_insert));
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONArray itemArray = (JSONArray) jsonArr.get(i);
                    statement.bindLong(1, itemArray.getLong(0));
                    statement.bindLong(2, itemArray.getLong(1));
                    statement.bindLong(3, itemArray.getLong(2));
                    statement.execute();
                }
                statement.close();
                database.setTransactionSuccessful();
            }
        } finally {
            if (statement != null) {
                statement.close();
            }

            if (database.inTransaction()) {
                database.endTransaction();
            }

            if (br != null) {
                br.close();
            }
        }
    }
}
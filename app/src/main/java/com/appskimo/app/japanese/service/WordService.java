package com.appskimo.app.japanese.service;

import android.content.Context;
import android.util.Log;

import com.appskimo.app.japanese.BuildConfig;
import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.domain.Callback;
import com.appskimo.app.japanese.domain.Dictionary;
import com.appskimo.app.japanese.domain.DictionaryWord;
import com.appskimo.app.japanese.domain.SupportLanguage;
import com.appskimo.app.japanese.domain.Word;
import com.appskimo.app.japanese.event.SelectDictionary;
import com.appskimo.app.japanese.support.SQLiteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.SelectArg;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.ormlite.annotations.OrmLiteDao;
import org.greenrobot.eventbus.EventBus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EBean(scope = EBean.Scope.Singleton)
public class WordService {
    @RootContext Context context;

    @OrmLiteDao(helper = SQLiteOpenHelper.class) DictionaryDao dictionaryDao;
    @OrmLiteDao(helper = SQLiteOpenHelper.class) WordDao wordDao;
    @OrmLiteDao(helper = SQLiteOpenHelper.class) DictionaryWordDao dictionaryWordDao;

    @Pref PrefsService_ prefs;

    private List<Dictionary> dictionaries;
    private Dictionary selectedDictionary;

    private Dictionary checklistDictionary;
    private Dictionary findDictionary = new Dictionary(Dictionary.UID_FINDLIST);
    private int prevDictionaryPosition = 1;

    private PreparedUpdate<DictionaryWord> wordResetQuery;
    private final SelectArg dictionaryIdArg = new SelectArg();
    private final SelectArg completeArg = new SelectArg();

    @AfterInject
    void afterInject() {
        initQueries();
    }

    private void initQueries() {
        try {
            var builder = dictionaryWordDao.updateBuilder();
            builder.updateColumnValue(DictionaryWord.FIELD_complete, completeArg);
            builder.setWhere(builder.where().eq(DictionaryWord.FIELD_dictionaryUid, dictionaryIdArg));
            wordResetQuery = builder.prepare();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Dictionary> getDictionaries() {
        if (dictionaries == null) {
            dictionaries = loadDictionaries();
        }
        return dictionaries;
    }

    private List<Dictionary> loadDictionaries() {
        try {
            var builder = dictionaryDao.queryBuilder().orderBy(Dictionary.FIELD_dictionaryUid, true);
            builder.setWhere(builder.where().ne(Dictionary.FIELD_dictionaryUid, Dictionary.UID_FINDLIST));
            dictionaries = builder.query();

            for (var dictionary : dictionaries) {
                if (dictionary.getDictionaryUid() == Dictionary.UID_CHECKLIST) {
                    checklistDictionary = dictionary;
                    break;
                }
            }

            return dictionaries;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Dictionary loadDictionary(Integer dictionaryUid) {
        return dictionaryDao.queryForId(dictionaryUid);
    }


    public void setComplete(DictionaryWord dictionaryWord) {
        dictionaryWord.setComplete(!dictionaryWord.isComplete());
        dictionaryWordDao.update(dictionaryWord);
    }

    public void resetCurrentDictionaryWords() {
        dictionaryIdArg.setValue(selectedDictionary.getDictionaryUid());
        completeArg.setValue(false);
        dictionaryWordDao.update(wordResetQuery);
        selectedDictionary = loadDictionary(selectedDictionary.getDictionaryUid());
        EventBus.getDefault().post(new SelectDictionary());
    }

    public void pin(DictionaryWord dictionaryWord) {
        if (dictionaryWord != null) {
            var find = findChecklistWord(dictionaryWord);
            if (find != null) {
                dictionaryWordDao.delete(find);
            } else {
                if (dictionaryWord.getDictionary() != null) {
                    if (dictionaryWord.getDictionary().getDictionaryUid() == Dictionary.UID_CHECKLIST) {
                        dictionaryWordDao.createIfNotExists(dictionaryWord);
                    } else {
                        var dWord = new DictionaryWord();
                        dWord.setDictionary(checklistDictionary);
                        dWord.setWord(dictionaryWord.getWord());
                        dictionaryWordDao.createIfNotExists(dWord);
                    }
                }
            }
        }
    }

    private DictionaryWord findChecklistWord(DictionaryWord dictionaryWord) {
        try {
            var selectQuery = dictionaryWordDao.queryBuilder();
            var where = selectQuery.where();
            where.and(where.eq(DictionaryWord.FIELD_dictionaryUid, Dictionary.UID_CHECKLIST), where.eq(DictionaryWord.FIELD_wordUid, dictionaryWord.getWord().getWordUid()));
            selectQuery.setWhere(where);
            return selectQuery.queryForFirst();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
        }
        return null;
    }

    public boolean pinned(DictionaryWord dictionaryWord) {
        return findChecklistWord(dictionaryWord) != null;
    }

    public List<DictionaryWord> getSelectedDictionaryWords(boolean complete) {
        var list = new ArrayList<DictionaryWord>();
        if (selectedDictionary != null) {
            for (DictionaryWord dictionaryWord : selectedDictionary.getDictionaryWords()) {
                if (dictionaryWord.isComplete() == complete) {
                    list.add(dictionaryWord);
                }
            }
        }
        return list;
    }

    public Dictionary getSelectedDictionary() {
        return this.selectedDictionary;
    }

    private void selectDictionary(Dictionary dictionary) {
        if (dictionary.getDictionaryUid() == Dictionary.UID_FINDLIST) {
            selectedDictionary = findDictionary;
        } else {
            selectedDictionary = loadDictionary(dictionary.getDictionaryUid());
        }
        EventBus.getDefault().post(new SelectDictionary());
    }

    public void selectDictionary(int position) {
        prevDictionaryPosition = position;
        selectDictionary(dictionaries.get(position));
    }

    public void selectPrevDictionary() {
        selectDictionary(dictionaries.get(prevDictionaryPosition));
    }

    public void find(String text) {
        var result = new ArrayList<DictionaryWord>();
        for (Word word : findBy(text)) {
            DictionaryWord dw = new DictionaryWord();
            dw.setDictionary(findDictionary);
            dw.setWord(word);
            result.add(dw);
        }
        findDictionary.setDictionaryWords(result);
        selectDictionary(findDictionary);
    }

    private List<Word> findBy(String text) {
        String likeText = "%".concat(text).concat("%");
        String pronounText = "[".concat(text).concat("]");
        try {
            var supportLanguage = SupportLanguage.valueOf(prefs.userLanguage().getOr("en"));
            var query = wordDao.queryBuilder().orderBy(Word.FIELD_word, true);
            var where = wordDao.queryBuilder().where();

            if (text.length() == 1) {
                if (supportLanguage == SupportLanguage.zh || supportLanguage == SupportLanguage.ja || supportLanguage == SupportLanguage.ko) {
                    where.or(where.eq(Word.FIELD_word, text), where.eq(Word.FIELD_pronunciation, pronounText), where.like(getSearchField(), likeText));
                } else {
                    where.or(where.eq(Word.FIELD_word, text), where.eq(Word.FIELD_pronunciation, pronounText));
                }
            } else if (text.length() == 2) {
                if (supportLanguage == SupportLanguage.zh || supportLanguage == SupportLanguage.ja || supportLanguage == SupportLanguage.ko) {
                    where.or(where.eq(Word.FIELD_pronunciation, pronounText), where.like(getSearchField(), likeText));
                } else {
                    where.eq(Word.FIELD_pronunciation, pronounText);
                }
            } else if (text.length() == 3) {
                where.or(where.like(getSearchField(), likeText), where.eq(Word.FIELD_pronunciation, pronounText));
            } else {
                where.like(getSearchField(), likeText);
            }
            query.setWhere(where);
            return query.query();
        } catch (SQLException e) {
            if (BuildConfig.DEBUG) {
                Log.e(getClass().getName(), e.getMessage(), e);
            }
            return Collections.emptyList();
        }
    }

    private String getSearchField() {
        String field = "";

        var supportLanguage = SupportLanguage.valueOf(prefs.userLanguage().getOr("en"));
        var country = prefs.userCountry().getOr("US");
        if (supportLanguage == SupportLanguage.zh) {
            if ("TW".equals(country)) {
                field = Word.FIELD_meaningZhTw;
            } else {
                field = Word.FIELD_meaningZhCn;
            }
        } else if (SupportLanguage.ar == supportLanguage) {
            field = Word.FIELD_meaningAr;
        } else if (SupportLanguage.da == supportLanguage) {
            field = Word.FIELD_meaningDa;
        } else if (SupportLanguage.de == supportLanguage) {
            field = Word.FIELD_meaningDe;
        } else if (SupportLanguage.el == supportLanguage) {
            field = Word.FIELD_meaningEl;
        } else if (SupportLanguage.en == supportLanguage) {
            field = Word.FIELD_meaningEn;
        } else if (SupportLanguage.es == supportLanguage) {
            field = Word.FIELD_meaningEs;
        } else if (SupportLanguage.fi == supportLanguage) {
            field = Word.FIELD_meaningFi;
        } else if (SupportLanguage.fr == supportLanguage) {
            field = Word.FIELD_meaningFr;
        } else if (SupportLanguage.hr == supportLanguage) {
            field = Word.FIELD_meaningHr;
        } else if (SupportLanguage.hu == supportLanguage) {
            field = Word.FIELD_meaningHu;
        } else if (SupportLanguage.id == supportLanguage) {
            field = Word.FIELD_meaningId;
        } else if (SupportLanguage.it == supportLanguage) {
            field = Word.FIELD_meaningIt;
        } else if (SupportLanguage.ko == supportLanguage) {
            field = Word.FIELD_meaningKo;
        } else if (SupportLanguage.ms == supportLanguage) {
            field = Word.FIELD_meaningMs;
        } else if (SupportLanguage.nl == supportLanguage) {
            field = Word.FIELD_meaningNl;
        } else if (SupportLanguage.pl == supportLanguage) {
            field = Word.FIELD_meaningPl;
        } else if (SupportLanguage.pt == supportLanguage) {
            field = Word.FIELD_meaningPt;
        } else if (SupportLanguage.ro == supportLanguage) {
            field = Word.FIELD_meaningRo;
        } else if (SupportLanguage.ru == supportLanguage) {
            field = Word.FIELD_meaningRu;
        } else if (SupportLanguage.sv == supportLanguage) {
            field = Word.FIELD_meaningSv;
        } else if (SupportLanguage.th == supportLanguage) {
            field = Word.FIELD_meaningTh;
        } else if (SupportLanguage.tr == supportLanguage) {
            field = Word.FIELD_meaningTr;
        } else if (SupportLanguage.uk == supportLanguage) {
            field = Word.FIELD_meaningUk;
        } else if (SupportLanguage.uz == supportLanguage) {
            field = Word.FIELD_meaningUz;
        } else if (SupportLanguage.vi == supportLanguage) {
            field = Word.FIELD_meaningVi;
        } else {
            field = Word.FIELD_meaningEn;
        }
        return field;
    }

    // ------------------------------------------------------------------------------------------------------------------------------

    public float getDensity() {
        try {
            return context.getResources().getDisplayMetrics().density;
        } catch (Exception e) {
            return 1.3F;
        }
    }

    public float getGuideScaleUnit() {
        int scaleFactor = context.getResources().getInteger(R.integer.stroke_scale_factor);
        return (float) scaleFactor * 0.1F * getDensity();
    }

    @Background
    public void initialize(Callback<Void> callback) {
        getDictionaries();
    }


    // ------------------------------------------------------------------------------------------------------------------------------

    public static class DictionaryDao extends RuntimeExceptionDao<Dictionary, Integer> {
        public DictionaryDao(Dao<Dictionary, Integer> dao) {
            super(dao);
        }
    }

    public static class WordDao extends RuntimeExceptionDao<Word, Integer> {
        public WordDao(Dao<Word, Integer> dao) {
            super(dao);
        }
    }

    public static class DictionaryWordDao extends RuntimeExceptionDao<DictionaryWord, Integer> {
        public DictionaryWordDao(Dao<DictionaryWord, Integer> dao) {
            super(dao);
        }
    }
}

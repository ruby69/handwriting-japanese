package com.appskimo.app.japanese.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@ToString(of = {"wordUid", "word", "en"})
@DatabaseTable(tableName = "Word")
public class Word implements Serializable {
    private static final long serialVersionUID = 7186673080695666021L;

    public static final String FIELD_wordUid = "wordUid";
    public static final String FIELD_dictionaryId = Dictionary.FIELD_dictionaryUid;
    public static final String FIELD_word = "word";
    public static final String FIELD_code = "code";
    public static final String FIELD_pronunciation = "pronunciation";
    public static final String FIELD_meaningAr = "ar";
    public static final String FIELD_meaningDa = "da";
    public static final String FIELD_meaningDe = "de";
    public static final String FIELD_meaningEl = "el";
    public static final String FIELD_meaningEn = "en";
    public static final String FIELD_meaningEs = "es";
    public static final String FIELD_meaningFi = "fi";
    public static final String FIELD_meaningFr = "fr";
    public static final String FIELD_meaningHr = "hr";
    public static final String FIELD_meaningHu = "hu";
    public static final String FIELD_meaningId = "id";
    public static final String FIELD_meaningIt = "it";
    public static final String FIELD_meaningKo = "ko";
    public static final String FIELD_meaningMs = "ms";
    public static final String FIELD_meaningNl = "nl";
    public static final String FIELD_meaningPl = "pl";
    public static final String FIELD_meaningPt = "pt";
    public static final String FIELD_meaningRo = "ro";
    public static final String FIELD_meaningRu = "ru";
    public static final String FIELD_meaningSv = "sv";
    public static final String FIELD_meaningTh = "th";
    public static final String FIELD_meaningTr = "tr";
    public static final String FIELD_meaningUk = "uk";
    public static final String FIELD_meaningUz = "uz";
    public static final String FIELD_meaningVi = "vi";
    public static final String FIELD_meaningZhCn = "zhCn";
    public static final String FIELD_meaningZhTw = "zhTw";
    public static final String FIELD_path = "path";

    @DatabaseField(columnName = FIELD_wordUid, id = true, generatedId = false) private Integer wordUid;
    @DatabaseField private String word;
    @DatabaseField private String code;
    @DatabaseField private String pronunciation;
    @DatabaseField private String ar;
    @DatabaseField private String da;
    @DatabaseField private String de;
    @DatabaseField private String el;
    @DatabaseField private String en;
    @DatabaseField private String es;
    @DatabaseField private String fi;
    @DatabaseField private String fr;
    @DatabaseField private String hr;
    @DatabaseField private String hu;
    @DatabaseField private String id;
    @DatabaseField private String it;
    @DatabaseField private String ko;
    @DatabaseField private String ms;
    @DatabaseField private String nl;
    @DatabaseField private String pl;
    @DatabaseField private String pt;
    @DatabaseField private String ro;
    @DatabaseField private String ru;
    @DatabaseField private String sv;
    @DatabaseField private String th;
    @DatabaseField private String tr;
    @DatabaseField private String uk;
    @DatabaseField private String uz;
    @DatabaseField private String vi;
    @DatabaseField private String zhCn;
    @DatabaseField private String zhTw;
    @DatabaseField private String path;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = FIELD_dictionaryId) private DictionaryWord dictionaryWord;

    private List<String> pathList;
    private List<OrderPoint> orderList;

    public String getMeaningForSimple(SupportLanguage supportLanguage, String country) {
        String meaning = getMeaning(supportLanguage, country).replaceAll("10,000", "10000");
        List<String> list = Arrays.asList(meaning.split(","));
        if (list.isEmpty()) {
            return "";
        } else {
            return list.get(0);
        }
    }

    public String getMeaningForCard(SupportLanguage supportLanguage, String country) {
        return getMeaning(supportLanguage, country);
    }

    public String getMeaningForGame(SupportLanguage supportLanguage, String country) {
        String meaning = getMeaning(supportLanguage, country).replaceAll("10,000", "10000");
        List<String> list = Arrays.asList(meaning.split(","));
        return StringUtils.join(list.size() > 5 ? list.subList(0, 5) : list, ", ");
    }

    public String getMeaning(SupportLanguage supportLanguage, String country) {
        String meaning = null;
        if (supportLanguage == SupportLanguage.zh) {
            if ("TW".equals(country)) {
                meaning = getZhTw();
            } else {
                meaning = getZhCn();
            }
        } else if (SupportLanguage.ar == supportLanguage) {
            meaning = getAr();
        } else if (SupportLanguage.da == supportLanguage) {
            meaning = getDa();
        } else if (SupportLanguage.de == supportLanguage) {
            meaning = getDe();
        } else if (SupportLanguage.el == supportLanguage) {
            meaning = getEl();
        } else if (SupportLanguage.en == supportLanguage) {
            meaning = getEn();
        } else if (SupportLanguage.es == supportLanguage) {
            meaning = getEs();
        } else if (SupportLanguage.fi == supportLanguage) {
            meaning = getFi();
        } else if (SupportLanguage.fr == supportLanguage) {
            meaning = getFr();
        } else if (SupportLanguage.hr == supportLanguage) {
            meaning = getHr();
        } else if (SupportLanguage.hu == supportLanguage) {
            meaning = getHu();
        } else if (SupportLanguage.id == supportLanguage || SupportLanguage.in == supportLanguage) {
            meaning = getId();
        } else if (SupportLanguage.it == supportLanguage) {
            meaning = getIt();
        } else if (SupportLanguage.ko == supportLanguage) {
            meaning = getKo();
        } else if (SupportLanguage.ms == supportLanguage) {
            meaning = getMs();
        } else if (SupportLanguage.nl == supportLanguage) {
            meaning = getNl();
        } else if (SupportLanguage.pl == supportLanguage) {
            meaning = getPl();
        } else if (SupportLanguage.pt == supportLanguage) {
            meaning = getPt();
        } else if (SupportLanguage.ro == supportLanguage) {
            meaning = getRo();
        } else if (SupportLanguage.ru == supportLanguage) {
            meaning = getRu();
        } else if (SupportLanguage.sv == supportLanguage) {
            meaning = getSv();
        } else if (SupportLanguage.th == supportLanguage) {
            meaning = getTh();
        } else if (SupportLanguage.tr == supportLanguage) {
            meaning = getTr();
        } else if (SupportLanguage.uk == supportLanguage) {
            meaning = getUk();
        } else if (SupportLanguage.uz == supportLanguage) {
            meaning = getUz();
        } else if (SupportLanguage.vi == supportLanguage) {
            meaning = getVi();
        } else {
            meaning = getEn();
        }

        return StringUtils.isEmpty(meaning) ? word : meaning;
    }

    public List<String> getPathList() {
        if (pathList == null) {
            pathList = new ArrayList<>();

            String[] temp = path.split(";");
            for (String pa : temp[0].split("[|]")) {
                pathList.add(pa);
            }
        }
        return pathList;
    }

    public List<OrderPoint> getOrderList() {
        if (orderList == null) {
            orderList = new ArrayList<>();

            String[] temp = path.split(";");
            String[] split = temp[1].split("[|]");
            for (int i = 0; i<split.length; i++) {
                orderList.add(new OrderPoint(i + 1, split[i]));
            }
        }
        return orderList;
    }

    @Getter
    public static class OrderPoint {
        private int order;
        private float x;
        private float y;

        public OrderPoint(int order, String str) {
            this.order = order;
            String[] temp = str.split(",");
            x = Float.valueOf(temp[0]);
            y = Float.valueOf(temp[1]);
        }

    }
}

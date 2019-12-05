package com.appskimo.app.japanese.domain;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;

@Getter
public enum SupportLanguage {

    ar("ar", "العربية"),
    da("da", "dansk"),
    de("de", "Deutsch"),
    el("el", "Ελληνικά"),
    en("en", "English"),
    es("es", "español"),
    fi("fi", "suomi"),
    fr("fr", "français"),
    hr("hr", "hrvatski"),
    hu("hu", "magyar"),
    in("in", "Bahasa Indonesia"),
    id("id", "Bahasa Indonesia"),
    it("it", "italiano"),
    ja("ja", "日本語"),
    ko("ko", "한국어"),
    ms("ms", "Bahasa Melayu"),
    nl("nl", "Nederlands"),
    pl("pl", "polski"),
    pt("pt", "português"),
    ro("ro", "română"),
    ru("ru", "русский"),
    sv("sv", "svenska"),
    th("th", "ไทย"),
    tr("tr", "Türkçe"),
    uk("uk", "українська"),
    uz("uz", "oʻzbekcha"),
    vi("vi", "Tiếng Việt"),
    zh("zh", "中文");

    private String code;
    private String displayName;
    private List<Map<String, String>> dictionaryUrls;

    private SupportLanguage(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    private static Set<SupportLanguage> set = EnumSet.of(SupportLanguage.ar, SupportLanguage.da, SupportLanguage.de, SupportLanguage.el, SupportLanguage.en, SupportLanguage.es, SupportLanguage.fi, SupportLanguage.fr, SupportLanguage.hr, SupportLanguage.hu, SupportLanguage.in, SupportLanguage.id, SupportLanguage.it, SupportLanguage.ja, SupportLanguage.ko, SupportLanguage.ms, SupportLanguage.nl, SupportLanguage.pl, SupportLanguage.pt, SupportLanguage.ro, SupportLanguage.ru, SupportLanguage.sv, SupportLanguage.th, SupportLanguage.tr, SupportLanguage.uk, SupportLanguage.uz, SupportLanguage.vi, SupportLanguage.zh);

    public static boolean isSupportLanguage(String code) {
        try {
            return set.contains(SupportLanguage.valueOf(code));
        } catch (Exception e) {
            return false;
        }
    }

    static {
        Map<String, String> zhDic = new HashMap<>();
        zhDic.put("name", "Glosbe");
        zhDic.put("url", "https://glosbe.com/ja/zh/%s");
        SupportLanguage.zh.dictionaryUrls = Arrays.asList(zhDic);

        Map<String, String> viDic = new HashMap<>();
        viDic.put("name", "Glosbe");
        viDic.put("url", "https://glosbe.com/ja/vi/%s");
        SupportLanguage.vi.dictionaryUrls = Arrays.asList(viDic);

        Map<String, String> uzDic = new HashMap<>();
        uzDic.put("name", "Glosbe");
        uzDic.put("url", "https://glosbe.com/ja/uz/%s");
        SupportLanguage.uz.dictionaryUrls = Arrays.asList(uzDic);

        Map<String, String> ukDic = new HashMap<>();
        ukDic.put("name", "Glosbe");
        ukDic.put("url", "https://glosbe.com/ja/uk/%s");
        SupportLanguage.uk.dictionaryUrls = Arrays.asList(ukDic);

        Map<String, String> trDic = new HashMap<>();
        trDic.put("name", "Glosbe");
        trDic.put("url", "https://glosbe.com/ja/tr/%s");
        SupportLanguage.tr.dictionaryUrls = Arrays.asList(trDic);

        Map<String, String> thDic = new HashMap<>();
        thDic.put("name", "Glosbe");
        thDic.put("url", "https://glosbe.com/ja/th/%s");
        SupportLanguage.th.dictionaryUrls = Arrays.asList(thDic);

        Map<String, String> svDic = new HashMap<>();
        svDic.put("name", "Glosbe");
        svDic.put("url", "https://glosbe.com/ja/sv/%s");
        SupportLanguage.sv.dictionaryUrls = Arrays.asList(svDic);

        Map<String, String> ruDic = new HashMap<>();
        ruDic.put("name", "Glosbe");
        ruDic.put("url", "https://glosbe.com/ja/ru/%s");
        SupportLanguage.ru.dictionaryUrls = Arrays.asList(ruDic);

        Map<String, String> roDic = new HashMap<>();
        roDic.put("name", "Glosbe");
        roDic.put("url", "https://glosbe.com/ja/ro/%s");
        SupportLanguage.ro.dictionaryUrls = Arrays.asList(roDic);

        Map<String, String> ptDic = new HashMap<>();
        ptDic.put("name", "Glosbe");
        ptDic.put("url", "https://glosbe.com/ja/pt/%s");
        SupportLanguage.pt.dictionaryUrls = Arrays.asList(ptDic);

        Map<String, String> plDic = new HashMap<>();
        plDic.put("name", "Glosbe");
        plDic.put("url", "https://glosbe.com/ja/pl/%s");
        SupportLanguage.pl.dictionaryUrls = Arrays.asList(plDic);

        Map<String, String> nlDic = new HashMap<>();
        nlDic.put("name", "Glosbe");
        nlDic.put("url", "https://glosbe.com/ja/nl/%s");
        SupportLanguage.nl.dictionaryUrls = Arrays.asList(nlDic);

        Map<String, String> msDic = new HashMap<>();
        msDic.put("name", "Glosbe");
        msDic.put("url", "https://glosbe.com/ja/ms/%s");
        SupportLanguage.ms.dictionaryUrls = Arrays.asList(msDic);

        Map<String, String> koDic1 = new HashMap<>();
        koDic1.put("name", "DAUM");
        koDic1.put("url", "https://dic.daum.net/search.do?dic=jp&q=%s");
        Map<String, String> koDic2 = new HashMap<>();
        koDic2.put("name", "NAVER");
        koDic2.put("url", "https://ja.dict.naver.com/search.nhn?query=%s");
        SupportLanguage.ko.dictionaryUrls = Arrays.asList(koDic1, koDic2);

        Map<String, String> jaDic1 = new HashMap<>();
        jaDic1.put("name", "jisho");
        jaDic1.put("url", "https://jisho.org/search/%s");
        SupportLanguage.ja.dictionaryUrls = Arrays.asList(jaDic1);

        Map<String, String> itDic = new HashMap<>();
        itDic.put("name", "Glosbe");
        itDic.put("url", "https://glosbe.com/ja/it/%s");
        SupportLanguage.it.dictionaryUrls = Arrays.asList(itDic);

        Map<String, String> inDic = new HashMap<>();
        inDic.put("name", "Glosbe");
        inDic.put("url", "https://glosbe.com/ja/id/%s");
        SupportLanguage.in.dictionaryUrls = Arrays.asList(inDic);
        SupportLanguage.id.dictionaryUrls = SupportLanguage.in.dictionaryUrls;

        Map<String, String> huDic = new HashMap<>();
        huDic.put("name", "Glosbe");
        huDic.put("url", "https://glosbe.com/ja/hu/%s");
        SupportLanguage.hu.dictionaryUrls = Arrays.asList(huDic);

        Map<String, String> hrDic = new HashMap<>();
        hrDic.put("name", "Glosbe");
        hrDic.put("url", "https://glosbe.com/ja/hr/%s");
        SupportLanguage.hr.dictionaryUrls = Arrays.asList(hrDic);

        Map<String, String> frDic = new HashMap<>();
        frDic.put("name", "Glosbe");
        frDic.put("url", "https://glosbe.com/ja/fr/%s");
        SupportLanguage.fr.dictionaryUrls = Arrays.asList(frDic);

        Map<String, String> fiDic = new HashMap<>();
        fiDic.put("name", "Glosbe");
        fiDic.put("url", "https://glosbe.com/ja/fi/%s");
        SupportLanguage.fi.dictionaryUrls = Arrays.asList(fiDic);

        Map<String, String> esDic = new HashMap<>();
        esDic.put("name", "Glosbe");
        esDic.put("url", "https://glosbe.com/ja/es/%s");
        SupportLanguage.es.dictionaryUrls = Arrays.asList(esDic);

        Map<String, String> enDic = new HashMap<>();
        enDic.put("name", "jisho");
        enDic.put("url", "https://jisho.org/search/%s");
        SupportLanguage.en.dictionaryUrls = Arrays.asList(enDic);

        Map<String, String> elDic = new HashMap<>();
        elDic.put("name", "Glosbe");
        elDic.put("url", "https://glosbe.com/ja/el/%s");
        SupportLanguage.el.dictionaryUrls = Arrays.asList(elDic);

        Map<String, String> deDic = new HashMap<>();
        deDic.put("name", "Glosbe");
        deDic.put("url", "https://glosbe.com/ja/de/%s");
        SupportLanguage.de.dictionaryUrls = Arrays.asList(deDic);

        Map<String, String> daDic = new HashMap<>();
        daDic.put("name", "Glosbe");
        daDic.put("url", "https://glosbe.com/ja/da/%s");
        SupportLanguage.da.dictionaryUrls = Arrays.asList(daDic);

        Map<String, String> arDic = new HashMap<>();
        arDic.put("name", "Glosbe");
        arDic.put("url", "https://glosbe.com/ja/ar/%s");
        SupportLanguage.ar.dictionaryUrls = Arrays.asList(arDic);
    }
}

package com.appskimo.app.japanese.service;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultFloat;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref(value = SharedPref.Scope.UNIQUE)
public interface PrefsService {
    @DefaultInt(0)
    int launchedCount();

    @DefaultFloat(20.0F)
    float guideLineStrokeWidth();

    @DefaultInt(2)
    int strokeRepeatCount();

    @DefaultInt(3)
    int strokeSpeed();

    String userLanguage();

    String userCountry();

    @DefaultBoolean(false)
    boolean initializeDatabase();

    @DefaultInt(0) // 0 - before, 1 - prgoress, 2 - succeed, 3 - failed
    int initializedDbStatus();

    @DefaultFloat(0.85F)
    float fontScale();

}

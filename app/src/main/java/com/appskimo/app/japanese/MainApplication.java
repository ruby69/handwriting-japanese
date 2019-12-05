package com.appskimo.app.japanese;

import android.content.Context;
import android.widget.Toast;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.appskimo.app.japanese.service.MiscService;
import com.appskimo.app.japanese.service.PrefsService_;
import com.appskimo.app.japanese.support.SQLiteOpenHelper;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.EventBus;

@EApplication
public class MainApplication extends MultiDexApplication {
    @Bean MiscService miscService;
    @Pref PrefsService_ prefs;

    @AfterInject
    void afterInject() {
        EventBus.builder().logNoSubscriberMessages(false).sendNoSubscriberEvent(false).installDefaultEventBus();
        initializeDatabase();
    }

    @Background
    void initializeDatabase() {
        SQLiteOpenHelper sqliteOpenHelper = new SQLiteOpenHelper(getApplicationContext());
        try {
            sqliteOpenHelper.setPrefs(prefs).getWritableDatabase(); // invoke db initialize method after calling onCreate or onUpgrade.
        } catch (Exception e) {
        } finally {
            sqliteOpenHelper.close();
            postInitializeDatabase();
        }
    }

    @UiThread
    void postInitializeDatabase() {
        if (prefs.initializedDbStatus().get() == 3) { // failed
            deleteDatabase(getString(R.string.db_name));
            toast(R.string.message_db_fail);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @UiThread
    void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }
}

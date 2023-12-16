package com.appskimo.app.japanese.service;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.appskimo.app.japanese.BuildConfig;
import com.appskimo.app.japanese.On;
import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.domain.Callback;
import com.appskimo.app.japanese.domain.SupportLanguage;
import com.appskimo.app.japanese.domain.Word;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Locale;

@EBean(scope = EBean.Scope.Singleton)
public class MiscService {
    @RootContext Context context;

    @Pref PrefsService_ prefs;
    @SystemService ConnectivityManager connectivityManager;
    @SystemService WindowManager windowManager;

    private boolean availableTts;
    private boolean initializedTts;

    private TextToSpeech tts;
    private static final Locale[] LOCALES = {Locale.JAPANESE, Locale.JAPAN};

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @AfterInject
    void afterInject() {
        availableTts(new Callback<Boolean>(){});
    }

    @UiThread
    public void showDialog(Activity activity, int positiveLabelResId, DialogInterface.OnClickListener positiveListener) {
        var dialog = new AlertDialog.Builder(activity)
                .setCancelable(true)
                .setMessage(activity.getString(positiveLabelResId))
                .setPositiveButton(R.string.label_confirm, positiveListener)
                .create();

        if (!activity.isFinishing() && !activity.isDestroyed()) {
            dialog.show();
        }
    }

    @UiThread
    public void showDialog(Activity activity, int titleResId, int positiveLabelResId, DialogInterface.OnClickListener positiveListener, int negativeLabelResId, DialogInterface.OnClickListener negativeListener) {
        var dialog = new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle(titleResId)
                .setPositiveButton(positiveLabelResId, positiveListener)
                .setNegativeButton(negativeLabelResId, negativeListener)
                .create();

        if (!activity.isFinishing() && !activity.isDestroyed()) {
            dialog.show();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void initializeTts(final Callback<Boolean> callback) {
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                availableTts = availableTts();
                callback.onSuccess(availableTts);
            }
            initializedTts = true;
            callback.onFinish();
        });
    }

    public void availableTts(Callback<Boolean> callback) {
        callback.before();
        if (!initializedTts) {
            initializeTts(callback);
        }
        callback.onSuccess(availableTts);
        callback.onFinish();
    }

    private boolean availableTts() {
        for(var locale : LOCALES) {
            int availableLang = tts.isLanguageAvailable(locale);
            if (availableLang == TextToSpeech.LANG_AVAILABLE || availableLang == TextToSpeech.LANG_COUNTRY_AVAILABLE || availableLang == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE) {
                try {
                    int result = tts.setLanguage(locale);
                    if (!(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)) {
                        return true;
                    }
                } catch (Exception e) {
                    if(BuildConfig.DEBUG) {
                        Log.e(getClass().getName(), e.getMessage(), e);
                    }
                }
            }
        }

        return false;
    }

    public void speech(Word word) {
        speech(word.getWord());
    }

    public void speech(String contents) {
        if (availableTts) {
            // tts.setSpeechRate(prefs.speechRate().get());
            tts.speak(contents, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public void stopSpeech() {
        if (availableTts && tts.isSpeaking()) {
            tts.stop();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void applyFontScale(Context context) {
        var resources = context.getResources();
        var configuration = resources.getConfiguration();
        configuration.fontScale = prefs.fontScale().getOr(0.85F);

        var metrics = resources.getDisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;

        context.createConfigurationContext(configuration);
    }

    public void applyLanguage(Context context) {
        var locale = Locale.getDefault();
        if (!prefs.userLanguage().exists()) {
            var language = locale.getLanguage();
            if (SupportLanguage.isSupportLanguage(language)) {
                prefs.userLanguage().put(SupportLanguage.valueOf(language).name());
            } else {
                prefs.userLanguage().put(SupportLanguage.en.name());
            }
        }
        if (!prefs.userCountry().exists()) {
            prefs.userCountry().put(locale.getCountry());
        }

        var languageCode = prefs.userLanguage().get();
        var resources = context.getResources();
        var displayMetrics = resources.getDisplayMetrics();
        var configuration = resources.getConfiguration();
        configuration.locale = new Locale(languageCode);
        resources.updateConfiguration(configuration, displayMetrics);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

}

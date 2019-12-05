package com.appskimo.app.japanese.service;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.appskimo.app.japanese.BuildConfig;
import com.appskimo.app.japanese.On;
import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.domain.Callback;
import com.appskimo.app.japanese.domain.SupportLanguage;
import com.appskimo.app.japanese.domain.Word;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Locale;

@EBean(scope = EBean.Scope.Singleton)
public class MiscService {
    @RootContext Context context;

    @Pref PrefsService_ prefs;
    @SystemService ConnectivityManager connectivityManager;
    @SystemService WindowManager windowManager;

    @StringRes(R.string.admob_app_id) String admobAppId;
    @StringRes(R.string.admob_banner_unit_id) String bannerAdUnitId;

    private AdRequest adRequest;
    private AdView rectangleAdView;

    private boolean availableTts;
    private boolean initializedTts;

    private TextToSpeech tts;
    private static final Locale[] LOCALES = {Locale.JAPANESE, Locale.JAPAN};

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @AfterInject
    void afterInject() {
        availableTts(new Callback<Boolean>(){});
    }

    private boolean initializedMobileAds;
    public void initializeMobileAds() {
        if (!initializedMobileAds) {
            initializedMobileAds = true;
            MobileAds.initialize(context, admobAppId);
            generateRectangleAdView(context);
        }
    }

    @UiThread
    public void loadBannerAdView(AdView adView) {
        if (adView != null && !adView.isLoading()) {
            adView.loadAd(getAdRequest());
        }
    }

    private AdRequest getAdRequest() {
        if(adRequest == null) {
            AdRequest.Builder builder = new AdRequest.Builder();
            if(BuildConfig.DEBUG) {
                for(String device : context.getResources().getStringArray(R.array.t_devices)) {
                    builder.addTestDevice(device);
                }
            }
            adRequest = builder.build();
        }
        return adRequest;
    }

    private AdView generateRectangleAdView(Context context) {
        if (rectangleAdView == null) {
            rectangleAdView = new AdView(context);
            rectangleAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            rectangleAdView.setAdUnitId(bannerAdUnitId);
            loadBannerAdView(rectangleAdView);
        }
        return rectangleAdView;
    }

    @UiThread
    public void showAdDialog(Activity activity, int positiveLabelResId, DialogInterface.OnClickListener positiveListener) {
        showAdDialog(activity, true, positiveLabelResId, positiveListener);
    }

    @UiThread
    public void showAdDialog(Activity activity, boolean cancelable, int positiveLabelResId, DialogInterface.OnClickListener positiveListener) {
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(cancelable)
                .setTitle(" ")
                .setPositiveButton(positiveLabelResId, positiveListener)
                .create();

        AdView adView = generateRectangleAdView(activity);
        if (adView != null) {
            ViewParent parent = adView.getParent();
            if(parent != null) {
                ((ViewGroup) parent).removeView(adView);
            }
            dialog.setView(adView);
        }

        if (!activity.isFinishing() && !activity.isDestroyed()) {
            dialog.show();
        }
    }

    @UiThread
    public void showAdDialog(Activity activity, int titleResId, int positiveLabelResId, DialogInterface.OnClickListener positiveListener, int negativeLabelResId, DialogInterface.OnClickListener negativeListener) {
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle(titleResId)
                .setPositiveButton(positiveLabelResId, positiveListener)
                .setNegativeButton(negativeLabelResId, negativeListener)
                .create();

        AdView adView = generateRectangleAdView(activity);
        if (adView != null) {
            ViewParent parent = adView.getParent();
            if(parent != null) {
                ((ViewGroup) parent).removeView(adView);
            }
            dialog.setView(adView);
        }

        if (!activity.isFinishing() && !activity.isDestroyed()) {
            dialog.show();
        }
    }


    @UiThread
    public void showAdDialog(Activity activity, On<Void> on) {
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(" ")
                .setPositiveButton(R.string.label_continue, (d,i) -> on.success(null))
                .setOnDismissListener(d -> on.success(null))
                .create();

        AdView adView = generateRectangleAdView(activity);
        if (adView != null) {
            ViewParent parent = adView.getParent();
            if(parent != null) {
                ((ViewGroup) parent).removeView(adView);
            }
            dialog.setView(adView);
        }

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
        for(Locale locale : LOCALES) {
            int availableLang = tts.isLanguageAvailable(locale);
            if(availableLang == TextToSpeech.LANG_AVAILABLE || availableLang == TextToSpeech.LANG_COUNTRY_AVAILABLE || availableLang == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE) {
                try {
                    int result = tts.setLanguage(locale);
                    if (!(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)) {
                        return true;
                    }
                }catch (Exception e) {
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
            if (Build.VERSION.SDK_INT < 21) {
                tts.speak(contents, TextToSpeech.QUEUE_FLUSH, null);
            } else {
                tts.speak(contents, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        }
    }

    public void stopSpeech() {
        if (availableTts && tts.isSpeaking()) {
            tts.stop();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void applyFontScale(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.fontScale = prefs.fontScale().getOr(0.85F);

        DisplayMetrics metrics = resources.getDisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;

        context.createConfigurationContext(configuration);
    }

    public void applyLanguage(Context context) {
        Locale locale = Locale.getDefault();
        if(!prefs.userLanguage().exists()) {
            String language = locale.getLanguage();
            if(SupportLanguage.isSupportLanguage(language)) {
                prefs.userLanguage().put(SupportLanguage.valueOf(language).name());
            } else {
                prefs.userLanguage().put(SupportLanguage.en.name());
            }
        }
        if(!prefs.userCountry().exists()) {
            prefs.userCountry().put(locale.getCountry());
        }

        String languageCode = prefs.userLanguage().get();
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(languageCode);
        resources.updateConfiguration(configuration, displayMetrics);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

}

package com.appskimo.app.japanese.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.event.ChangeFontScale;
import com.appskimo.app.japanese.service.PrefsService_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.EventBus;

@EViewGroup(R.layout.view_font_scale)
public class FontScaleView extends RelativeLayout {
    @ViewById(R.id.scales) RadioGroup scales;
    @Pref PrefsService_ prefs;

    public FontScaleView(Context context) {
        super(context);
    }

    public FontScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterViews
    void afterViews() {
        float scale = prefs.fontScale().getOr(0.85F);
        scales.clearCheck();
        if (scale == 0.70F) {
            scales.check(R.id.tiny);
        } else if (scale == 0.85F) {
            scales.check(R.id.small);
        } else if (scale == 1.00F) {
            scales.check(R.id.normal);
        } else {
            scales.check(R.id.large);
        }
    }

    @Click({R.id.tiny, R.id.small, R.id.normal, R.id.large})
    void onClick(View view) {
        prefs.fontScale().put(Float.valueOf((String) view.getTag()));
        EventBus.getDefault().post(new ChangeFontScale());
    }
}

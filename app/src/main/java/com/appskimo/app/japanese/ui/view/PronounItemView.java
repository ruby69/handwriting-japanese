package com.appskimo.app.japanese.ui.view;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.service.MiscService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.view_pronoun_item)
public class PronounItemView extends RelativeLayout {
    @ViewById(R.id.yomiView) TextView yomiView;
    @ViewById(R.id.romaziView) TextView romaziView;
    @Bean MiscService miscService;

    private String yomi;

    public PronounItemView(Context context) {
        super(context);
    }

    @AfterViews
    void afterViews() {
    }

    public void setPronoun(String pronoun) {
        String[] temp = pronoun.split("\\|");
        this.yomi = temp[0];
        yomiView.setText(yomi);
        romaziView.setText(temp[1]);
    }

    @Click(R.id.pronounSelector)
    void clickTTs() {
        miscService.speech(yomi);
    }
}
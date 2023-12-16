package com.appskimo.app.japanese.ui.view;

import android.animation.Animator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.domain.DictionaryWord;
import com.appskimo.app.japanese.domain.SupportLanguage;
import com.appskimo.app.japanese.event.OpenPronounDialog;
import com.appskimo.app.japanese.event.RefreshCardWord;
import com.appskimo.app.japanese.event.RefreshListWord;
import com.appskimo.app.japanese.service.MiscService;
import com.appskimo.app.japanese.service.PrefsService_;
import com.appskimo.app.japanese.service.WordService;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.EventBus;

@EViewGroup(R.layout.view_word_card_item)
public class WordCardItemView extends RelativeLayout {
    @ViewById(R.id.frontWordCard) View frontWordCard;
    @ViewById(R.id.characterView) TextView characterView;
    @ViewById(R.id.frontMaskingLayer) TextView frontMaskingLayer;
    @ViewById(R.id.frontCompleteFlag) ImageView frontCompleteFlag;

    @ViewById(R.id.backWordCard) View backWordCard;
    @ViewById(R.id.onlyPronounView) TextView onlyPronounView;
    @ViewById(R.id.pronounView) TextView pronounView;
    @ViewById(R.id.meaningView) TextView meaningView;
    @ViewById(R.id.pronounMeaningView) View pronounMeaningView;
    @ViewById(R.id.backMaskingLayer) TextView backMaskingLayer;
    @ViewById(R.id.backCompleteFlag) ImageView backCompleteFlag;
    @ViewById(R.id.cardPinBt) ImageView cardPinBt;
    @ViewById(R.id.dictionaryLayer) LinearLayout dictionaryLayer;

    @Bean WordService wordService;
    @Bean MiscService miscService;
    @Pref PrefsService_ prefs;
    @ColorRes(R.color.pink) int pink;

    private DictionaryWord dictionaryWord;
    private Animator.AnimatorListener flipListener = new Animator.AnimatorListener() {
        @Override public void onAnimationStart(Animator animation) {}
        @Override public void onAnimationCancel(Animator animation) {}
        @Override public void onAnimationRepeat(Animator animation) {}
        @Override public void onAnimationEnd(Animator animation) {
            lockFlip = Boolean.FALSE;
        }
    };
    private boolean lockFlip;

    public WordCardItemView(Context context) {
        super(context);
    }

    public void setComplity(boolean complity) {
        if (complity) {
            frontMaskingLayer.setVisibility(View.VISIBLE);
            backMaskingLayer.setVisibility(View.VISIBLE);
            frontCompleteFlag.setVisibility(View.VISIBLE);
            backCompleteFlag.setVisibility(View.VISIBLE);
        } else {
            frontMaskingLayer.setVisibility(View.GONE);
            backMaskingLayer.setVisibility(View.GONE);
            frontCompleteFlag.setVisibility(View.GONE);
            backCompleteFlag.setVisibility(View.GONE);
        }
    }

    public void setWord(DictionaryWord dictionaryWord) {
        this.dictionaryWord = dictionaryWord;
        var supportLanguage = SupportLanguage.valueOf(prefs.userLanguage().getOr("en"));

        var word = dictionaryWord.getWord();
        characterView.setText(word.getWord());

        var pronoun = word.getPronunciation();
        if(word.getWordUid() < 141) {
            onlyPronounView.setVisibility(View.VISIBLE);
            pronounMeaningView.setVisibility(View.GONE);
            onlyPronounView.setText(pronoun);

        } else {
            onlyPronounView.setVisibility(View.GONE);
            pronounMeaningView.setVisibility(View.VISIBLE);
            populatePronoun(pronoun);
            meaningView.setText(word.getMeaningForCard(supportLanguage, prefs.userCountry().getOr("US")));
        }
        setComplity(dictionaryWord.isComplete());

        if(dictionaryWord.isOpen()) {
            YoYo.with(Techniques.FadeOut).duration(0).playOn(frontWordCard);
            YoYo.with(Techniques.FadeIn).duration(0).playOn(backWordCard);
        } else {
            YoYo.with(Techniques.FadeIn).duration(0).playOn(frontWordCard);
            YoYo.with(Techniques.FadeOut).duration(0).playOn(backWordCard);
        }

        this.pinning();

        dictionaryLayer.removeAllViews();
        if(word.getWordUid() > 140) {
            for(var map : supportLanguage.getDictionaryUrls()) {
                var view = DictionaryLinkView_.build(getContext());
                view.setDictionaryWord(dictionaryWord, map.get("name"), map.get("url"));
                dictionaryLayer.addView(view);
            }
        }
    }

    private void populatePronoun(String pronouns) {
        StringBuilder sb = new StringBuilder();
        String[] arr = pronouns.split(", ");
        int count = arr.length > 5 ? 5 : arr.length;
        for(int i = 0; i<count; i++) {
            sb.append(arr[i].split("\\|")[0]).append((i < count - 1) ? ", " : "");
        }
        pronounView.setText(sb.toString());
    }

    @Click(R.id.hear)
    void onClickHear() {
        var word = dictionaryWord.getWord();
        if(word.getWordUid() < 141) {
            miscService.speech(word.getWord());
        } else {
            EventBus.getDefault().post(new OpenPronounDialog(word));
        }
    }

    @Click(value = {R.id.backWordCard, R.id.frontWordCard})
    void clickCard() {
        if(!lockFlip) {
            lockFlip = Boolean.TRUE;
            if(dictionaryWord.isOpen()) {
                YoYo.with(Techniques.FadeIn).duration(750).withListener(flipListener).playOn(frontWordCard);
                YoYo.with(Techniques.FadeOut).duration(0).playOn(backWordCard);
            } else {
                YoYo.with(Techniques.FadeIn).duration(750).withListener(flipListener).playOn(backWordCard);
                YoYo.with(Techniques.FadeOut).duration(0).playOn(frontWordCard);
            }

            dictionaryWord.setOpen(!dictionaryWord.isOpen());
        }
    }

    @UiThread
    void pinning() {
        if (wordService.pinned(dictionaryWord)) {
            cardPinBt.setColorFilter(pink, PorterDuff.Mode.SRC_ATOP);
        } else {
            cardPinBt.clearColorFilter();
        }
    }

    @Click(R.id.cardPinBt)
    void clickWritingViewPinBt() {
        wordService.pin(dictionaryWord);
        pinning();
        EventBus.getDefault().post(new RefreshListWord(true));
        EventBus.getDefault().post(new RefreshCardWord());
    }
}

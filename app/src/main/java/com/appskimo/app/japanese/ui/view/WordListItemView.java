package com.appskimo.app.japanese.ui.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.domain.Dictionary;
import com.appskimo.app.japanese.domain.DictionaryWord;
import com.appskimo.app.japanese.domain.SupportLanguage;
import com.appskimo.app.japanese.event.RefreshCardWord;
import com.appskimo.app.japanese.event.RefreshListWord;
import com.appskimo.app.japanese.event.SelectWord;
import com.appskimo.app.japanese.service.PrefsService_;
import com.appskimo.app.japanese.service.WordService;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DimensionRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.EventBus;

@EViewGroup(R.layout.view_word_list_item)
public class WordListItemView extends RelativeLayout {
    @ViewById(R.id.characterView) TextView characterView;
    @ViewById(R.id.meaningView) TextView meaningView;
    @ViewById(R.id.check) ImageView check;
    @Bean WordService wordService;
    @Pref PrefsService_ prefs;
    @ColorRes(R.color.pink) int pink;
    @ColorRes(R.color.bluegrey_light) int blueGreyLight;
    @ColorRes(R.color.textColorPrimary) int textColorPrimary;
    @ColorRes(R.color.textColorSecondary) int textColorSecondary;
    @ColorRes(R.color.bluegrey_light) int greyLight;

    @DimensionRes(R.dimen.text_size_character_max) float maxTextSize;
    @DimensionRes(R.dimen.text_size_character_min) float minTextSize;
    @DimensionRes(R.dimen.text_size_character_default) float defaultTextSize;

    private int position;
    private DictionaryWord dictionaryWord;

    public WordListItemView(Context context) {
        super(context);
    }

    public WordListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRecord(int position, DictionaryWord dictionaryWord) {
        this.dictionaryWord = dictionaryWord;
        rearrangePosition(position);

        SupportLanguage supportLanguage = SupportLanguage.valueOf(prefs.userLanguage().getOr("en"));
        if (dictionaryWord != null && dictionaryWord.getWord() != null) {
            characterView.setText(dictionaryWord.getWord().getWord());
            int codeValue = Integer.parseInt(dictionaryWord.getWord().getCode(), 16);
            if(codeValue > 12353 && codeValue < 12510) {
                meaningView.setText(null);
                meaningView.setVisibility(View.GONE);
            } else {
                meaningView.setVisibility(View.VISIBLE);
                meaningView.setText(dictionaryWord.getWord().getMeaningForSimple(supportLanguage, prefs.userCountry().getOr("US")));
            }

            check.setVisibility(View.VISIBLE);
            pinning();
            adjustCharacterApperance();
        } else {
            characterView.setText(null);
            meaningView.setText(null);
            meaningView.setVisibility(View.GONE);
            check.setVisibility(View.INVISIBLE);
        }
    }

    @UiThread
    void adjustCharacterApperance () {
        Dictionary dictionary = wordService.getSelectedDictionary();
        if (!(dictionary.getDictionaryUid() == 10001 || dictionary.getDictionaryUid() == 10002 || dictionary.getDictionaryUid() == 10003)) {
            if (wordService.pinned(dictionaryWord)) {
                characterView.setTextSize(TypedValue.COMPLEX_UNIT_PX, maxTextSize);
                characterView.setTextColor(textColorPrimary);

            } else if (dictionaryWord.isComplete()) {
                characterView.setTextSize(TypedValue.COMPLEX_UNIT_PX, minTextSize);
                characterView.setTextColor(greyLight);
            } else {
                characterView.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultTextSize);
                characterView.setTextColor(textColorSecondary);
            }
        } else {
            characterView.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultTextSize);
            characterView.setTextColor(textColorPrimary);
        }
    }

    @UiThread
    void pinning() {
        if (wordService.pinned(dictionaryWord)) {
            check.setColorFilter(pink, PorterDuff.Mode.SRC_ATOP);
        } else {
            check.setColorFilter(blueGreyLight, PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void rearrangePosition(int position) {
        Dictionary dictionary = wordService.getSelectedDictionary();
        if (dictionary.getDictionaryUid() == 10003) {
            position = position > 67 ? position - 1 : position;
            position = position > 66 ? position - 1 : position;
        }
        if (dictionary.getDictionaryUid() == 10002 || dictionary.getDictionaryUid() == 10003) {
            position = position > 54 ? position - 1 : position;
            position = position > 53 ? position - 1 : position;
            position = position > 52 ? position - 1 : position;
            position = position > 51 ? position - 1 : position;
            position = position > 48 ? position - 1 : position;
            position = position > 47 ? position - 1 : position;
            position = position > 46 ? position - 1 : position;
            position = position > 38 ? position - 1 : position;
            position = position > 36 ? position - 1 : position;
        }

        this.position = position;
    }

    @Click(value = {R.id.characterView, R.id.meaningView, R.id.characterLayer})
    void onClickCharacter() {
        if (dictionaryWord != null) {
            EventBus.getDefault().post(new SelectWord(position));
        }
    }

    @Click({R.id.checkLayer, R.id.check})
    void onClickCheck() {
        wordService.pin(dictionaryWord);
        pinning();
        EventBus.getDefault().post(new RefreshListWord(position));
        EventBus.getDefault().post(new RefreshCardWord());
    }
}
package com.appskimo.app.japanese.ui.view;

import android.content.Context;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.domain.DictionaryWord;
import com.appskimo.app.japanese.event.Link;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

@EViewGroup(R.layout.view_dictionary_link)
public class DictionaryLinkView extends RelativeLayout {
    @ViewById(R.id.dictionaryLink) Button dictionaryLink;

    private String url;
    private DictionaryWord dictionaryWord;

    public DictionaryLinkView(Context context) {
        super(context);
    }

    public void setDictionaryWord(DictionaryWord dictionaryWord, String externalDictionaryName, String urlFormat) {
        this.dictionaryWord = dictionaryWord;
        dictionaryLink.setText(externalDictionaryName);
        this.url = String.format(urlFormat, dictionaryWord.getWord().getWord());
    }

    @Click(R.id.dictionaryLink)
    void onClick() {
        if(dictionaryWord.isOpen()) {
            EventBus.getDefault().post(new Link(url));
        }
    }
}

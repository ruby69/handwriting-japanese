package com.appskimo.app.japanese.ui.view;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.event.SelectGameDictionary;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;

@EViewGroup(R.layout.view_dictionary_item)
public class DictionaryItemView extends RelativeLayout {
    @ViewById(R.id.dictionaryTitleView) TextView dictionaryTitleView;

    private int position = -1;

    public DictionaryItemView(Context context) {
        super(context);
    }

    public void setTitle(int position, String title) {
        this.position = position;
        dictionaryTitleView.setText(title);
    }

    @Click(R.id.dictionaryTitleView)
    void onClick() {
        if(position != -1) {
            EventBus.getDefault().post(new SelectGameDictionary(position));
        }
    }
}
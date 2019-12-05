package com.appskimo.app.japanese.ui.frags;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.domain.Dictionary;
import com.appskimo.app.japanese.event.GameNextAction;
import com.appskimo.app.japanese.event.SelectGameDictionary;
import com.appskimo.app.japanese.service.GameService;
import com.appskimo.app.japanese.support.EventBusObserver;
import com.appskimo.app.japanese.ui.view.DictionaryItemView;
import com.appskimo.app.japanese.ui.view.DictionaryItemView_;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

@EFragment(R.layout.fragment_game_menu)
public class GameMenuFragment extends Fragment {
    ViewPager viewPager;
    @ViewById(R.id.categoryLabel) TextView categoryLabel;
    @ViewById(R.id.categorySelection) View categorySelection;
    @ViewById(R.id.categoriesLayer) LinearLayout categoriesLayer;
    @ViewById(R.id.countLabel) TextView countLabel;
    @ViewById(R.id.hintDelayLabel) TextView hintDelayLabel;

    @Bean GameService gameService;
    @StringRes(R.string.message_selectDictionary) String selectDictionaryMessage;
    @StringRes(R.string.label_game_select_category) String selectDictionaryLabel;
    @StringRes(R.string.label_game_select_count) String selectCountLabel;
    @StringRes(R.string.label_game_select_hint_delay) String selectHintDelayLabel;

    String [] titles;
    private List<Dictionary> dictionaries;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new EventBusObserver.AtStartStop(this));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewPager = getActivity().findViewById(R.id.viewPager);
    }

    @AfterInject
    void afterInject() {
        dictionaries = gameService.getDictionaries();
        titles = new String[dictionaries.size()];
        for(int i = 0; i<dictionaries.size(); i++) {
            titles[i] = dictionaries.get(i).getName();
        }
    }

    @AfterViews
    void afterViews() {
        for(int i = 0; i < titles.length; i++) {
            if(i == 1 || i == 3 || i == 8) {
                categoriesLayer.addView(DictionaryItemView_.build(getActivity()));
            }
            DictionaryItemView dictionaryItemView = DictionaryItemView_.build(getActivity());
            dictionaryItemView.setTitle(i, titles[i]);
            categoriesLayer.addView(dictionaryItemView);
        }
        initializeOptions();
    }

    private void initializeOptions() {
        int dicPosition = getActivity().getIntent().getIntExtra("selectDictionaryPosition", 1);
        int count = getActivity().getIntent().getIntExtra("selectCount", 10);
        long hintDelay = getActivity().getIntent().getLongExtra("selectHintDelay", 5000L);

        onEvent(new SelectGameDictionary(dicPosition));
        putCount(count);
        putHintDelay(hintDelay);
    }

    @Subscribe
    public void onEvent(SelectGameDictionary event) {
        int position = event.getPosition();
        Dictionary dictionary = dictionaries.get(position);
        categoryLabel.setText(selectDictionaryLabel + " : " + dictionary.getName());
        getActivity().getIntent().putExtra("selectDictionaryPosition", position);
    }

    @Click(R.id.play)
    void onClickPlay() {
        int position = getActivity().getIntent().getIntExtra("selectDictionaryPosition", 1);
        Dictionary dictionary = dictionaries.get(position);
        if (dictionary.getDictionaryWords().size() > 0) {
            if(gameService.hasStopped()) {
                viewPager.setCurrentItem(2, Boolean.TRUE);
            }
        } else {
            Toast.makeText(getActivity().getApplicationContext(), selectDictionaryMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Click(R.id.histories)
    void onClickHistories() {
        viewPager.setCurrentItem(GameNextAction.Kind.HISTORY.ordinal(), Boolean.FALSE);
    }

    @Click({R.id.count1, R.id.count2, R.id.count3, R.id.count4, R.id.count5})
    void onClickCount(View view) {
        putCount(Integer.parseInt((String) view.getTag()));
    }

    @Click({R.id.hintDelay1, R.id.hintDelay2, R.id.hintDelay3})
    void onClickHintDelay(View view) {
        putHintDelay(Long.parseLong((String) view.getTag()));
    }

    private void putCount(int count) {
        if(count != -1) {
            countLabel.setText(selectCountLabel + " : " + count);
        } else {
            countLabel.setText(selectCountLabel + " : ALL");
        }

        getActivity().getIntent().putExtra("selectCount", count);
    }

    private void putHintDelay(long hintDelay) {
        hintDelayLabel.setText(selectHintDelayLabel + " : " + (hintDelay / 1000L));
        getActivity().getIntent().putExtra("selectHintDelay", hintDelay);
    }
}

package com.appskimo.app.japanese.ui.frags;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.domain.Dictionary;
import com.appskimo.app.japanese.domain.DictionaryWord;
import com.appskimo.app.japanese.event.RefreshListWord;
import com.appskimo.app.japanese.event.SelectDictionary;
import com.appskimo.app.japanese.service.WordService;
import com.appskimo.app.japanese.support.EventBusObserver;
import com.appskimo.app.japanese.ui.adapter.WordListRecyclerViewAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.IntegerRes;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

@EFragment(R.layout.fragment_list)
public class ListFragment extends Fragment {
    @ViewById(R.id.recyclerView) RecyclerView recyclerView;

    @Bean WordListRecyclerViewAdapter recyclerViewAdapter;
    @Bean WordService wordService;
    @IntegerRes(R.integer.list_kana_column_count) int kanaColumnCount;
    @IntegerRes(R.integer.list_kanji_column_count) int kanjiColumnCount;

    private Dictionary dictionary;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new EventBusObserver.AtStartStop(this));
    }

    @AfterViews
    void afterViews() {
        resetRecyclerViewLayout();
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Subscribe
    public void onEvent(SelectDictionary event) {
        dictionary = wordService.getSelectedDictionary();
        var list = new ArrayList<DictionaryWord>(dictionary.getDictionaryWords());
        if (dictionary.getDictionaryUid() == 10002 || dictionary.getDictionaryUid() == 10003) {
            list.add(36, null);
            list.add(38, null);
            list.add(46, null);
            list.add(47, null);
            list.add(48, null);
            list.add(51, null);
            list.add(52, null);
            list.add(53, null);
            list.add(54, null);

            if (dictionary.getDictionaryUid() == 10003) {
                list.add(66, null);
                list.add(67, null);
            }
        }

        resetRecyclerViewLayout();
        recyclerViewAdapter.reset(list);
        recyclerView.scrollToPosition(0);
    }

    private void resetRecyclerViewLayout() {
        var dictionary = wordService.getSelectedDictionary();
        if(dictionary == null || dictionary.getDictionaryUid() == 10001 || dictionary.getDictionaryUid() == 10002 || dictionary.getDictionaryUid() == 10003) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), kanaColumnCount)); // kanaColumnCount
        } else {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(kanjiColumnCount, StaggeredGridLayoutManager.VERTICAL)); // kanjiColumnCount
        }
    }

    @Subscribe
    public void onEvent(RefreshListWord event) {
        if (event.isAll()) {
            recyclerViewAdapter.notifyDataSetChanged();
        } else {
            if(event.getPosition() > -1 && !(dictionary.getDictionaryUid() == 10001 || dictionary.getDictionaryUid() == 10002 || dictionary.getDictionaryUid() == 10003)) {
                recyclerViewAdapter.notifyItemChanged(event.getPosition());
            }
        }
    }
}

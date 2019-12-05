package com.appskimo.app.japanese.ui.frags;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.domain.DictionaryWord;
import com.appskimo.app.japanese.event.OpenPronounDialog;
import com.appskimo.app.japanese.event.RefreshCardWord;
import com.appskimo.app.japanese.event.RefreshListWord;
import com.appskimo.app.japanese.event.SelectDictionary;
import com.appskimo.app.japanese.service.PrefsService_;
import com.appskimo.app.japanese.service.WordService;
import com.appskimo.app.japanese.support.EventBusObserver;
import com.appskimo.app.japanese.ui.adapter.WordCardRecyclerViewAdapter;
import com.appskimo.app.japanese.ui.dialog.PronounDialog;
import com.appskimo.app.japanese.ui.dialog.PronounDialog_;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.IntegerRes;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@EFragment(R.layout.fragment_card)
public class CardFragment extends Fragment {
    @ViewById(R.id.cardMenu) FloatingActionsMenu cardMenu;
    @ViewById(R.id.recyclerView) RecyclerView recyclerView;
    @ViewById(R.id.skipGuideView) View skipGuideView;
    @ViewById(R.id.completeGuideView) View completeGuideView;
    @ViewById(R.id.mode) FloatingActionButton mode;

    @Bean WordService wordService;
    @Bean WordCardRecyclerViewAdapter recyclerViewAdapter;
    @Pref PrefsService_ prefs;
    @IntegerRes(R.integer.card_column_count) int columnCount;
    @StringRes(R.string.label_card_uncomp) String uncompLabel;
    @StringRes(R.string.label_card_comp) String compLabel;

    private boolean showGuide;
    private boolean showComplete;
    private PronounDialog pronounDialog = PronounDialog_.builder().build();

    private ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public boolean isLongPressDragEnabled() {
            return Boolean.FALSE;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return Boolean.TRUE;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return Boolean.TRUE;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.START) {
                recyclerViewAdapter.onSkip(position);
                hideGuide();
            } else if (direction == ItemTouchHelper.END) {
                DictionaryWord word = recyclerViewAdapter.onComplete(position);
                wordService.setComplete(word);
                EventBus.getDefault().post(new RefreshListWord(true));
                hideGuide();
            }
        }
    });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new EventBusObserver.AtStartStop(this));
    }

    @AfterViews
    void afterViews() {
        initCardListView();
        if (prefs != null && prefs.launchedCount().get() < 5) {
            initGuideIcon();
        }
    }

    private void initCardListView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), columnCount));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewAdapter);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        initWords();
    }

    private void initWords() {
        showComplete = Boolean.FALSE;
        List<DictionaryWord> list = wordService.getSelectedDictionaryWords(showComplete);
        if (list.size() < 1) {
            showComplete = !showComplete;
            list = wordService.getSelectedDictionaryWords(showComplete);
        }
        recyclerViewAdapter.reset(list);
        recyclerView.scrollToPosition(0);
    }

    @Subscribe
    public void onEvent(SelectDictionary event) {
        initWords();
    }

    @Subscribe
    public void onEvent(OpenPronounDialog event) {
        pronounDialog.setPronouns(event.getWord().getPronunciation()).show(getChildFragmentManager(), PronounDialog.TAG);
    }

    @Subscribe
    public void onEvent(RefreshCardWord event) {
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void initGuideIcon() {
        if (!showGuide) {
            showGuide = true;
            skipGuideView.setVisibility(View.VISIBLE);
            completeGuideView.setVisibility(View.VISIBLE);
        }
    }

    @UiThread
    void hideGuide() {
        if (showGuide) {
            showGuide = false;
            YoYo.with(Techniques.SlideOutLeft).duration(1000).playOn(skipGuideView);
            YoYo.with(Techniques.SlideOutRight).duration(1000).playOn(completeGuideView);
        }
    }

    @Click(R.id.reset)
    void onClickReset() {
        wordService.resetCurrentDictionaryWords();
        recyclerViewAdapter.reset(wordService.getSelectedDictionaryWords(false));
        recyclerView.scrollToPosition(0);
        cardMenu.collapse();
    }

    @Click(R.id.mode)
    void onClickMode() {
        showComplete = !showComplete;
        recyclerViewAdapter.reset(wordService.getSelectedDictionaryWords(showComplete));
        recyclerView.scrollToPosition(0);
        cardMenu.collapse();

        if (showComplete) {
            mode.setTitle(uncompLabel);
        } else {
            mode.setTitle(compLabel);
        }
    }

    @Click(R.id.shuffle)
    void onClickShuffle() {
        List<DictionaryWord> words = wordService.getSelectedDictionaryWords(showComplete);
        Collections.shuffle(words, new Random(System.nanoTime()));
        recyclerViewAdapter.reset(words);
        recyclerView.scrollToPosition(0);
        cardMenu.collapse();
    }
}

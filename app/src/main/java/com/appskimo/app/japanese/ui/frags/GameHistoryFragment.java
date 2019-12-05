package com.appskimo.app.japanese.ui.frags;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.service.GameService;
import com.appskimo.app.japanese.ui.adapter.GameRecordRecyclerViewAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.IntegerRes;

@EFragment(R.layout.fragment_game_history)
public class GameHistoryFragment extends Fragment {
    @ViewById(R.id.recyclerView) RecyclerView recyclerView;

    @Bean GameRecordRecyclerViewAdapter recyclerViewAdapter;
    @Bean GameService gameService;
    @IntegerRes(R.integer.rank_column_count) int columnCount;

    @AfterViews
    void afterViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.reset(gameService.getTopRank());
    }
}

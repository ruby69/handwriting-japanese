package com.appskimo.app.japanese.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.appskimo.app.japanese.domain.GameRecord;
import com.appskimo.app.japanese.ui.view.GameRecordItemView;
import com.appskimo.app.japanese.ui.view.GameRecordItemView_;

import org.androidannotations.annotations.EBean;

@EBean
public class GameRecordRecyclerViewAdapter extends CommonRecyclerViewAdapter<GameRecord, GameRecordRecyclerViewAdapter.ViewHolder> {
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.gameRecordItemView.setRecord(position, items.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ViewHolder(GameRecordItemView_.build(viewGroup.getContext()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        GameRecordItemView gameRecordItemView;
        public ViewHolder(View itemView) {
            super(itemView);
            gameRecordItemView = (GameRecordItemView) itemView;
        }
    }
}
package com.appskimo.app.japanese.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.appskimo.app.japanese.domain.DictionaryWord;
import com.appskimo.app.japanese.ui.view.WordListItemView;
import com.appskimo.app.japanese.ui.view.WordListItemView_;

import org.androidannotations.annotations.EBean;

@EBean
public class WordListRecyclerViewAdapter extends CommonRecyclerViewAdapter<DictionaryWord, WordListRecyclerViewAdapter.ViewHolder> {
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.wordListItemView.setRecord(position, items.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ViewHolder(WordListItemView_.build(viewGroup.getContext()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        WordListItemView wordListItemView;

        public ViewHolder(View itemView) {
            super(itemView);
            wordListItemView = (WordListItemView) itemView;
        }
    }
}
package com.appskimo.app.japanese.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.appskimo.app.japanese.domain.DictionaryWord;
import com.appskimo.app.japanese.ui.view.WordCardItemView;
import com.appskimo.app.japanese.ui.view.WordCardItemView_;

import org.androidannotations.annotations.EBean;

@EBean
public class WordCardRecyclerViewAdapter extends CommonRecyclerViewAdapter<DictionaryWord, WordCardRecyclerViewAdapter.ViewHolder> {
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.wordCardItemView.setWord(items.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ViewHolder(WordCardItemView_.build(viewGroup.getContext()));
    }

    public void onSkip(int position) {
        items.add(onComplete(position));
        notifyItemInserted(items.size() - 1);
    }

    public DictionaryWord onComplete(int position) {
        notifyItemRemoved(position);
        return items.remove(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        WordCardItemView wordCardItemView;
        public ViewHolder(View itemView) {
            super(itemView);
            wordCardItemView = (WordCardItemView) itemView;
        }
    }
}
package com.appskimo.app.japanese.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.appskimo.app.japanese.ui.view.PronounItemView;
import com.appskimo.app.japanese.ui.view.PronounItemView_;

import org.androidannotations.annotations.EBean;

@EBean
public class PronounRecyclerViewAdapter extends CommonRecyclerViewAdapter<String, PronounRecyclerViewAdapter.ViewHolder> {

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.pronounItemView.setPronoun(items.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ViewHolder(PronounItemView_.build(viewGroup.getContext()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        PronounItemView pronounItemView;
        public ViewHolder(View itemView) {
            super(itemView);
            pronounItemView = (PronounItemView) itemView;
        }
    }
}
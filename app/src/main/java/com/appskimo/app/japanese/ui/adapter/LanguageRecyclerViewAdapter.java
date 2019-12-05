package com.appskimo.app.japanese.ui.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.appskimo.app.japanese.domain.SupportLanguage;
import com.appskimo.app.japanese.ui.view.LanguageItemView;
import com.appskimo.app.japanese.ui.view.LanguageItemView_;

import org.androidannotations.annotations.EBean;

@EBean
public class LanguageRecyclerViewAdapter extends CommonRecyclerViewAdapter<SupportLanguage, LanguageRecyclerViewAdapter.ViewHolder> {

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.languageItemView.setLanguage(items.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ViewHolder(LanguageItemView_.build(viewGroup.getContext()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LanguageItemView languageItemView;
        public ViewHolder(View itemView) {
            super(itemView);
            languageItemView = (LanguageItemView) itemView;
        }
    }
}
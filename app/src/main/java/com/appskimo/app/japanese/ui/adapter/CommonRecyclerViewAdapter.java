package com.appskimo.app.japanese.ui.adapter;

import android.os.Handler;
import android.os.Looper;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class CommonRecyclerViewAdapter<M, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    final List<M> items = new ArrayList<>();

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        handler.post(() -> {
            items.clear();
            notifyDataSetChanged();
        });
    }

    public void add(final List<M> items) {
        handler.post(() -> {
            if (items != null) {
                CommonRecyclerViewAdapter.this.items.addAll(items);
                notifyDataSetChanged();
            }
        });
    }

    public void reset(final List<M> items) {
        handler.post(() -> {
            CommonRecyclerViewAdapter.this.items.clear();
            if (items != null) {
                CommonRecyclerViewAdapter.this.items.addAll(items);
            }
            notifyDataSetChanged();
        });
    }
}
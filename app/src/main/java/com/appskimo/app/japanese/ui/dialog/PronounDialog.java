package com.appskimo.app.japanese.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.ui.adapter.PronounRecyclerViewAdapter;
import com.appskimo.app.japanese.ui.adapter.PronounRecyclerViewAdapter_;

import org.androidannotations.annotations.EFragment;

import java.util.Arrays;

@EFragment
public class PronounDialog extends CommonDialog {
    public static final String TAG = "PronounDialog";

    private RecyclerView recyclerView;
    private PronounRecyclerViewAdapter recyclerViewAdapter;
    private String pronouns;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(init(inflater.inflate(R.layout.dialog_simple_selector, null)));
        return builder.create();
    }

    private View init(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            recyclerViewAdapter = PronounRecyclerViewAdapter_.getInstance_(getActivity());
            if (recyclerViewAdapter != null && pronouns != null) {
                recyclerViewAdapter.reset(Arrays.asList(pronouns.split(", ")));
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }

    public PronounDialog setPronouns(String pronouns) {
        this.pronouns = pronouns;
        return this;
    }
}
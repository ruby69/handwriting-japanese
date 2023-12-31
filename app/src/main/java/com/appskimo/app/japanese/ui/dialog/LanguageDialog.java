package com.appskimo.app.japanese.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.appskimo.app.japanese.R;
import com.appskimo.app.japanese.domain.SupportLanguage;
import com.appskimo.app.japanese.ui.adapter.LanguageRecyclerViewAdapter;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;

@EFragment
public class LanguageDialog extends CommonDialog {
    public static final String TAG = "LanguageDialog";

    private RecyclerView recyclerView;
    @Bean LanguageRecyclerViewAdapter recyclerViewAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        var builder = new AlertDialog.Builder(getActivity());
        var inflater = getActivity().getLayoutInflater();
        builder.setView(init(inflater.inflate(R.layout.dialog_simple_selector, null)));
        return builder.create();
    }

    private View init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        var dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            var list = new ArrayList<SupportLanguage>();
            for (var sl : SupportLanguage.values()) {
                if (sl != SupportLanguage.id) {
                    list.add(sl);
                }
            }
            recyclerViewAdapter.reset(list);

            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }
}
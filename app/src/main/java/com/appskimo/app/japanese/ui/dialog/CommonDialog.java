package com.appskimo.app.japanese.ui.dialog;

import android.content.DialogInterface;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public abstract class CommonDialog extends DialogFragment {
    protected boolean shown;

    @Override
    public void show(FragmentManager manager, String tag) {
        if (shown) return;
        super.show(manager, tag);
        shown = Boolean.TRUE;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        shown = Boolean.FALSE;
        super.onDismiss(dialog);
    }

    @SuppressWarnings("unused")
    public boolean isShown() {
        return shown;
    }
}

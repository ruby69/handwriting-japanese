package com.appskimo.app.japanese.ui.dialog;

import androidx.appcompat.app.AlertDialog;

public class LinkDialog extends WebViewDialog {
    public static final String TAG = "LinkDialog";
    private String url;

    @Override
    public void onStart() {
        super.onStart();
        var dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            webView.loadUrl(url);
        }
    }

    public LinkDialog setUrl(String url) {
        this.url = url;
        return this;
    }
}

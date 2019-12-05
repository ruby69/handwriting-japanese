package com.appskimo.app.japanese.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;

import com.appskimo.app.japanese.R;

public class WebViewDialog extends CommonDialog {
    protected WebView webView;
    protected ProgressBar progressView;

    protected WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressView.setVisibility(View.GONE);
        }
    };
    protected WebChromeClient webChromeClient = new WebChromeClient();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(init(inflater.inflate(R.layout.dialog_web_view, null)));
        return builder.create();
    }

    private View init(View view) {
        progressView = (ProgressBar) view.findViewById(R.id.progressView);
        webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);
        webView.requestFocus();
        return view;
    }
}
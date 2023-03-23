package com.blitz.hiddenwebview;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebViewEventListener extends WebViewClient {
    private static final String TAG = WebViewController.TAG;
    private final WebViewActivity webViewActivity;

    private boolean hasError;
    private int requestId;

    public void reset(int scriptId) {
        Log.d(TAG, "CustomWebViewClient.reset scriptId = " + scriptId);

        this.requestId = scriptId;
        this.hasError = false;
    }

    public void reset() {
        reset(-1);
    }

    public WebViewEventListener(WebViewActivity webViewActivity) {
        super();

        Log.d(TAG, "WebViewEventListener");

        this.webViewActivity = webViewActivity;
        reset();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d(TAG, "WebViewEventListener.shouldOverrideUrlLoading");

        webViewActivity.onPageLoading(url, requestId);
        return true;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Log.d(TAG, "WebViewEventListener.shouldOverrideUrlLoading");

        webViewActivity.onPageLoading(request.getUrl().toString(), requestId);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        Log.d(TAG, "WebViewEventListener.onPageFinished");

        webViewActivity.onPageFinished(url, requestId);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        Log.d(TAG, "WebViewEventListener.onReceivedError");

        onReceivedErrorHandler(failingUrl, description);
    }

    @Override
    @TargetApi(android.os.Build.VERSION_CODES.M)
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError rerr) {
        Log.d(TAG, "WebViewEventListener.onReceivedError");

        onReceivedErrorHandler(request.getUrl().toString(), rerr.getDescription().toString());
    }

    protected void onReceivedErrorHandler(String failingUrl, String description) {
        Log.d(TAG, "WebViewEventListener.onReceivedErrorHandler");

        if (!this.hasError) {
            this.hasError = true;
            webViewActivity.onReceivedError(failingUrl, description, requestId);
        }
    }
}

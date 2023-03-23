package com.blitz.hiddenwebview;

import android.annotation.SuppressLint;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;


public class WebViewChromeClient extends WebChromeClient {
    private static final String TAG = WebViewController.TAG;
    private final WebViewActivity webViewActivity;
    private int scriptId;

    public WebViewChromeClient(WebViewActivity webViewActivity) {
        Log.d(TAG, "WebViewChromeClient.CustomWebChromeClient");

        this.webViewActivity = webViewActivity;
        reset(-1);
    }

    public void reset(int scriptId) {
        this.scriptId = scriptId;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public boolean onConsoleMessage(ConsoleMessage msg) {
        Log.d(TAG, "WebViewChromeClient.onConsoleMessage: " + msg.message());

        if (msg.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
            String errorMessage = String.format("js:%d: %s", msg.lineNumber(), msg.message());
            Log.e(TAG, "WebViewChromeClient.onConsoleMessage errorMessage: " + errorMessage);
            webViewActivity.onScriptFailed(errorMessage, scriptId);
            return true;
        }
        return false;
    }
}

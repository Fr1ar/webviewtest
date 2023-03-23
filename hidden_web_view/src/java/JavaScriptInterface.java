package com.blitz.hiddenwebview;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;

interface JavaScriptHandler {
    void postMessage(String message);
}

class JavaScriptInterface {
    private static final String TAG = WebViewController.TAG;

    private final Activity activity;
    private final JavaScriptHandler handler;

    JavaScriptInterface(Activity activity, JavaScriptHandler handler) {
        Log.d(TAG, "JavaScriptInterface");

        this.activity = activity;
        this.handler = handler;
    }

    @JavascriptInterface
    public void postMessage(String message) {
        Log.d(TAG, "JavaScriptInterface.postMessage, message = " + message);

        this.activity.runOnUiThread(() -> {
            handler.postMessage(message);
        });
    }
}
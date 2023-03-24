package com.blitz.hiddenwebview;

import android.util.Log;

public class WebViewController {
    public static final String TAG = "HiddenWebView";

    private interface Runnable {
        void runOnUiThread(WebViewActivity activity);
    }

    protected WebViewActivity getActivity() {
        return ApplicationController.instance.getWebViewActivity();
    }

    protected void runOnUiThread(Runnable runnable) {
        WebViewActivity activity = getActivity();
        activity.runOnUiThread(() -> {
            runnable.runOnUiThread(activity);
        });
    }

    public int isInUse() {
        WebViewActivity activity = getActivity();
        return activity.isWebViewVisible() ? 1 : 0;
    }

    public void executeScript(String js, int id) {
        Log.d(TAG, "WebViewController.executeScript: " + js);

        runOnUiThread((activity) -> {
            activity.executeScript(js, id);
        });
    }

    public void addJavascriptChannel(String channel, int id) {
        Log.d(TAG, "WebViewController.addJavascriptChannel, channel = " + channel);

        runOnUiThread((activity) -> {
            activity.addJavascriptChannel(channel);
        });
    }

    public void loadGame(String gamePath, int id) {
        Log.d(TAG, "WebViewController.loadGame, gamePath = " + gamePath);

        runOnUiThread((activity) -> {
            activity.loadGame(gamePath, id);
        });
    }

    public void loadWebPage(String gamePath, int id) {
        Log.d(TAG, "WebViewController.loadWebPage, gamePath = " + gamePath);

        runOnUiThread((activity) -> {
            activity.loadWebPage(gamePath, id);
        });
    }

    public void changeVisibility(int visible) {
        Log.d(TAG, "WebViewController.changeVisibility visible = " + visible);

        runOnUiThread((activity) -> {
            activity.changeVisibility(visible);
        });
    }

    public void setDebugEnabled(int flag) {
        Log.d(TAG, "WebViewController.setDebugEnabled flag = " + flag);

        runOnUiThread((activity) -> {
            activity.setDebugEnabled(flag);
        });
    }

    public void setTouchInterceptor(double x, double y, double width, double height) {
        Log.d(TAG, "WebViewController.setTouchInterceptor x = " + x + ", y = " + y +
                ", width = " + width + ", height = " + height);

        runOnUiThread((activity) -> {
            activity.setTouchInterceptor(x, y, width, height);
        });
    }

    public void setPositionAndSize(double x, double y, double width, double height) {
        Log.d(TAG, "WebViewController.setPositionAndSize width = " + width +
                ", height = " + height + ", x = " + x + ", y = " + y);

        runOnUiThread((activity) -> {
            activity.setPositionAndSize(x, y, width, height);
        });
    }

    public void acceptTouchEvents(int accept) {
        Log.d(TAG, "WebViewController.acceptTouchEvents accept = " + accept);

        runOnUiThread((activity) -> {
            activity.acceptTouchEvents(accept);
        });
    }

    public void centerWebView() {
        Log.d(TAG, "WebViewController.centerWebView");

        runOnUiThread((activity) -> {
            activity.centerWebView();
        });
    }

    public void matchScreenSize() {
        Log.d(TAG, "WebViewController.matchScreenSize");

        runOnUiThread((activity) -> {
            activity.matchScreenSize();
        });
    }
}
package com.blitz.hiddenwebview;

import android.util.Log;

public class DefoldWebViewInterface {
    public native void onScriptFinished(String result, int id);
    public native void onScriptCallback(String type, String payload);

    private static final String TAG = "HiddenWebViewLog";

    public void executeScript(String js, int id) {
        Log.d(TAG, "DefoldWebViewInterface.executeScript: " + js);

        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(() -> {
            Log.d(TAG, "DefoldWebViewInterface.executeScript: runOnUiThread()");
            FakeWebViewActivity.executeScript(js, id);
        });
    }

    public void loadGame(String gamePath, int id) {
        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(() -> {
            Log.d(TAG, "DefoldWebViewInterface.loadGame: runOnUiThread()");
            FakeWebViewActivity.loadGame(gamePath, id);
        });
    }

    public void loadWebPage(String gamePath, int id) {
        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(() -> {
            Log.d(TAG, "DefoldWebViewInterface.loadWebPage: runOnUiThread()");
            FakeWebViewActivity.loadWebPage(gamePath, id);
        });
    }

    public void changeVisibility(int visible) {
        Log.d(TAG, "DefoldWebViewInterface.changeVisibility visible = " + visible);

        FakeWebViewActivity.defoldWebViewInterface = this;

        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(() -> {
            Log.d(TAG, "DefoldWebViewInterface.changeVisibility: runOnUiThread()");
            FakeWebViewActivity.changeVisibility(visible);
        });
    }

    public void setDebugEnabled(int flag) {
        Log.d(TAG, "DefoldWebViewInterface.setDebugEnabled flag = " + flag);

        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(() -> {
            Log.d(TAG, "DefoldWebViewInterface.setDebugEnabled: runOnUiThread()");
            FakeWebViewActivity.setDebugEnabled(flag);
        });
    }

    public void setTouchInterceptor(double x, double y, double width, double height) {
        Log.d(TAG, "DefoldWebViewInterface.setTouchInterceptor");

        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(() -> {
            Log.d(TAG, "DefoldWebViewInterface.setTouchInterceptor: runOnUiThread()");
            FakeWebViewActivity.setTouchInterceptor(x, y, width, height);
        });
    }

    public void setPositionAndSize(double x, double y, double width, double height) {
        Log.d(TAG, "DefoldWebViewInterface.setPositionAndSize width = " + width + 
            ", height = " + height + ", x = " + x + ", y = " + y);

        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(() -> {
            Log.d(TAG, "DefoldWebViewInterface.setPositionAndSize: runOnUiThread()");
            FakeWebViewActivity.setPositionAndSize(x, y, width, height);
        });
    }

    public void acceptTouchEvents(int accept) {
        Log.d(TAG, "DefoldWebViewInterface.acceptTouchEvents");

        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(() -> {
            Log.d(TAG, "DefoldWebViewInterface.acceptTouchEvents: runOnUiThread()");
            FakeWebViewActivity.acceptTouchEvents(accept);
        });
    }

    public int isInUse() {
        Log.d(TAG, "DefoldWebViewInterface.isInUse");

        return FakeWebViewActivity.webViewActive ? 1 : 0;
    }

    public void centerWebView() {
        Log.d(TAG, "DefoldWebViewInterface.centerWebView");

        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(FakeWebViewActivity::centerWebView);
    }

    public void matchScreenSize() {
        Log.d(TAG, "DefoldWebViewInterface.matchScreenSize");

        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(FakeWebViewActivity::matchScreenSize);
    }
}
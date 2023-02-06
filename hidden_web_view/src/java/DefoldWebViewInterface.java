package com.blitz.hiddenwebview;

public class DefoldWebViewInterface {
    public native void onScriptFinished(String result, int id);
    public native void onScriptCallback(String type, String payload);

    public void executeScript(String js, int id) {
        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(() -> {
            FakeWebViewActivity.executeScript(js, id);
        });
    }

    public void loadGame(String gamePath, int id) {
        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(() -> {
            FakeWebViewActivity.loadGame(gamePath, id);
        });
    }

    public void loadWebPage(String gamePath, int id) {
        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(() -> {
            FakeWebViewActivity.loadWebPage(gamePath, id);
        });
    }

    public void changeVisibility(int visible) {
        FakeWebViewActivity.defoldWebViewInterface = this;

        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(() -> {
            FakeWebViewActivity.changeVisibility(visible);
        });
    }

    public void setDebugEnabled(int flag) {
        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(() -> {
            FakeWebViewActivity.setDebugEnabled(flag);
        });
    }

    public void setTouchInterceptor(double width, double height, double x, double y) {
        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(() -> {
            FakeWebViewActivity.setTouchInterceptor(height, width, x, y);
        });
    }

    public void setPositionAndSize(double width, double height, double x, double y) {
        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(() -> {
            FakeWebViewActivity.setPositionAndSize(height, width, x, y);
        });
    }

    public void acceptTouchEvents(int accept) {
        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(() -> {
            FakeWebViewActivity.acceptTouchEvents(accept);
        });
    }

    public int isInUse() {
        return FakeWebViewActivity.webViewActive ? 1 : 0;
    }

    public void centerWebView() {
        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(FakeWebViewActivity::centerWebView);
    }

    public void matchScreenSize() {
        CurrentActivityAwareApplication.currentlyOpenedActivity.runOnUiThread(FakeWebViewActivity::matchScreenSize);
    }
}
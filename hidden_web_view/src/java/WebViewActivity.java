package com.blitz.hiddenwebview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Size;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.graphics.Point;
import android.view.Display;
import android.util.Log;
import android.content.res.Configuration;


/**
 * Класс окна (Activity), в котором создается WebView для рендера HTML игры
 */
public class WebViewActivity extends Activity {
    private static final String TAG = WebViewController.TAG;

    /**
     * Контейнер, в котором создается WebView
     */
    private FrameLayout mainContainer;

    /**
     * WebView, в котором рендерится HTML игры
     */
    private WebView htmlGameView;

    /**
     * View-перехватчик пользовательского ввода, создается поверх DefoldActivity
     */
    private TouchInterceptorView touchInterceptorView;

    /**
     * Параметры расположения View-перехватчика в DefoldActivity
     */
    private static class Bounds {
        public double x = 0.0;
        public double y = 0.0;
        public double width = 1.0;
        public double height = 1.0;
    }
    private final Bounds bounds = new Bounds();

    /**
     * Этот класс обрабатывает Javascript ошибки в htmlGameView и передает их через JNI в lua код
     */
    private WebViewChromeClient webViewChromeClient;

    /**
     * Этот класс обрабатывает состояния HTML страницы в htmlGameView и передает их через JNI в lua код
     */
    private WebViewEventListener webViewEventListener;

    /**
     * Включен ли отладочный режим
     */
    private boolean isDebugEnabled = false;


    /**
     * Функции-обработчики, которые вызываются из native кода
     */
    public native void onPageLoading(String url, int id);
    public native void onPageFinished(String url, int id);
    public native void onReceivedError(String url, String errorMessage, int id);
    public native void onScriptFailed(String errorMessage, int id);
    private native void onScriptFinished(String result, int id);
    private native void onScriptCallback(String type, String payload);


    protected void openDefoldActivity() {
        openActivity(ApplicationController.DEFOLD_ACTIVITY);
    }

    protected void openWebViewActivity() {
        openActivity(ApplicationController.WEBVIEW_ACTIVITY);
    }

    protected WebViewActivity getWebViewActivity() {
        return ApplicationController.instance.getWebViewActivity();
    }

    protected Activity getDefoldActivity() {
        return ApplicationController.instance.getDefoldActivity();
    }

    protected Size getScreenSize() {
        WindowManager wm = getDefoldActivity().getWindowManager();
        Display display = wm.getDefaultDisplay();
        Point pt = new Point();
        display.getSize(pt);
        return new Size(pt.x, pt.y);
    }

    protected int dpToPx(double dp) {
        return (int) dp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "WebViewActivity.onCreate");

        initSystemUIVisibilityListener();
        updateFullscreenMode();

        createWebViewContainer();

        openDefoldActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "WebViewActivity.onResume");

        updateFullscreenMode();
    }

    protected void initSystemUIVisibilityListener() {
        // Это нужно, чтобы не показывалась белая/черная полоса под стандартным
        // всплывающим меню "Navigation Bar" (там где кнопки "Back", "Home" и "Overview")
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (visibility -> {
                    if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0) {
                        updateFullscreenMode();
                    }
                });
    }

    public void updateFullscreenMode() {
        Log.d(TAG, "WebViewActivity.updateFullscreenMode");

        Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(layoutParams);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Log.d(TAG, "WebViewActivity.onWindowFocusChanged");

        if (hasFocus) {
            updateFullscreenMode();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG, "WebViewActivity.onConfigurationChanged");

        if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
            updateFullscreenMode();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void createWebView() {
        Log.d(TAG, "WebViewActivity.createWebView");

        if (htmlGameView != null) {
            destroyWebView();
        }

        htmlGameView = new WebView(this);
        htmlGameView.setBackgroundColor(getBackgroundColor());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        params.gravity = Gravity.CENTER;
        htmlGameView.setLayoutParams(params);

        WebSettings webSettings = htmlGameView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        htmlGameView.setFocusable(true);
        htmlGameView.setFocusableInTouchMode(true);
        htmlGameView.setClickable(true);
        htmlGameView.requestFocus(View.FOCUS_DOWN);
        htmlGameView.setHapticFeedbackEnabled(true);
        htmlGameView.requestFocusFromTouch();

        webViewChromeClient = new WebViewChromeClient(this);
        webViewEventListener = new WebViewEventListener(this);

        htmlGameView.setWebChromeClient(webViewChromeClient);
        htmlGameView.setWebViewClient(webViewEventListener);

        mainContainer.addView(htmlGameView, params);
    }

    public void destroyWebView() {
        Log.d(TAG, "WebViewActivity.destroyWebView");

        if (htmlGameView == null) {
            return;
        }

        removeTouchInterceptorView();

        if (isWebViewActive()) {
            mainContainer.removeView(htmlGameView);
        }

        htmlGameView = null;
    }

    public boolean isWebViewActive() {
        return htmlGameView != null && htmlGameView.getWindowToken() != null;
    }

    public WebView getHtmlGameView() {
        return htmlGameView;
    }

    public void setPositionAndSize(double x, double y, double width, double height) {
        Log.d(TAG, "WebViewActivity.setPositionAndSize x = " + x + ", y = " + y +
                ", width = " + width + ", height = " + height);

        if (htmlGameView == null) {
            return;
        }

        Size screenSize = getScreenSize();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) htmlGameView.getLayoutParams();
        params.width = dpToPx(screenSize.getWidth() * width);
        params.height = dpToPx(screenSize.getHeight() * height);
        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.setMargins(dpToPx(screenSize.getWidth() * x), dpToPx(screenSize.getHeight() * y), 0, 0);

        htmlGameView.setLayoutParams(params);
        htmlGameView.requestLayout();
    }

    public void executeScript(String js, int id) {
        Log.d(TAG, "WebViewActivity.executeScript: " + js);

        if (htmlGameView == null) {
            return;
        }

        htmlGameView.evaluateJavascript(js, value -> onScriptFinished(value, id));
    }

    public void addJavascriptChannel(String channel) {
        Log.d(TAG, "WebViewActivity.addJavascriptChannel: " + channel);

        if (htmlGameView == null) {
            return;
        }

        JavaScriptInterface js = new JavaScriptInterface(
                getWebViewActivity(),
                (message) -> {
                    Log.d(TAG, "WebViewActivity.addJavascriptChannel channel = " + channel + ", message = " + message);
                    onScriptCallback(channel, message);
                }
        );

        htmlGameView.addJavascriptInterface(js, channel);
    }

    public void loadGame(String gamePath, int id) {
        Log.d(TAG, "WebViewActivity.loadGame");

        if (htmlGameView == null) {
            return;
        }

        webViewEventListener.reset(id);
        webViewChromeClient.reset(id);

        htmlGameView.loadUrl("http://localhost:8808/" + gamePath);
    }

    public void loadWebPage(String gamePath, int id) {
        Log.d(TAG, "WebViewActivity.loadWebPage");

        if (htmlGameView == null) {
            return;
        }

        webViewEventListener.reset(id);
        webViewChromeClient.reset(id);

        htmlGameView.loadUrl(gamePath);
    }

    public void setDebugEnabled(int flag) {
        isDebugEnabled = (flag != 0);
        WebView.setWebContentsDebuggingEnabled(isDebugEnabled);
    }

    protected WindowManager.LayoutParams getTouchInterceptorViewParams() {
        Size screenSize = getScreenSize();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = dpToPx(screenSize.getWidth() * bounds.x);
        params.y = dpToPx(screenSize.getHeight() * bounds.y);
        params.width = dpToPx(screenSize.getWidth() * bounds.width);
        params.height = dpToPx(screenSize.getHeight() * bounds.height);
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.alpha = 0.4f;
        params.format = PixelFormat.TRANSLUCENT;
        return params;
    }

    protected boolean isTouchInterceptorViewActive() {
        return touchInterceptorView != null && touchInterceptorView.getWindowToken() != null;
    }

    protected void addTouchInterceptorView() {
        WindowManager wm = getDefoldActivity().getWindowManager();
        touchInterceptorView = new TouchInterceptorView(getDefoldActivity(), getWebViewActivity());
        touchInterceptorView.setBackground(new ColorDrawable(isDebugEnabled ? Color.LTGRAY : Color.TRANSPARENT));
        touchInterceptorView.setDebugEnabled(isDebugEnabled);
        WindowManager.LayoutParams params = getTouchInterceptorViewParams();
        wm.addView(touchInterceptorView, params);
    }

    protected void removeTouchInterceptorView() {
        WindowManager wm = getDefoldActivity().getWindowManager();
        if (isTouchInterceptorViewActive()) {
            wm.removeViewImmediate(touchInterceptorView);
        }
        touchInterceptorView = null;
    }

    public void setTouchInterceptor(double x, double y, double width, double height) {
        Log.d(TAG, "WebViewActivity.setTouchInterceptor x = " + x + ", y = " + y +
                ", width = " + width + ", height = " + height);

        bounds.x = x;
        bounds.y = y;
        bounds.width = width;
        bounds.height = height;

        if (isTouchInterceptorViewActive()) {
            removeTouchInterceptorView();
            addTouchInterceptorView();
        }
    }

    public void acceptTouchEvents(int accept) {
        Log.d(TAG, "WebViewActivity.acceptTouchEvents accept = " + accept);

        if (htmlGameView == null) {
            return;
        }

        removeTouchInterceptorView();
        if (accept != 0) {
            addTouchInterceptorView();
        }
    }

    public void changeVisibility(int visible) {
        Log.d(TAG, "WebViewActivity.changeVisibility visible = " + visible);

        if (htmlGameView == null) {
            return;
        }

        boolean isWebViewVisible = (visible != 0);
        htmlGameView.setVisibility(isWebViewVisible ? View.VISIBLE : View.GONE);

        if (isTouchInterceptorViewActive()) {
            touchInterceptorView.setVisibility(isWebViewVisible ? View.VISIBLE : View.GONE);
        }
    }

    public boolean isWebViewVisible() {
        return htmlGameView != null && htmlGameView.getVisibility() == View.VISIBLE;
    }

    public void centerWebView() {
        Log.d(TAG, "WebViewActivity.centerWebView");

        if (htmlGameView == null) {
            return;
        }

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) htmlGameView.getLayoutParams();
        params.gravity = Gravity.CENTER;
        params.setMargins(0, 0, 0, 0);

        htmlGameView.setLayoutParams(params);
        htmlGameView.requestLayout();
    }

    public void matchScreenSize() {
        Log.d(TAG, "WebViewActivity.matchScreenSize");

        if (htmlGameView == null) {
            return;
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );

        params.gravity = Gravity.CENTER;

        htmlGameView.setLayoutParams(params);
        htmlGameView.requestLayout();
    }

    private void createWebViewContainer() {
        Log.d(TAG, "WebViewActivity.createWebViewContainer");

        mainContainer = new FrameLayout(this);
        mainContainer.setBackgroundColor(getBackgroundColor());
        setContentView(mainContainer);
    }

    protected int getBackgroundColor() {
        return getResources().getColor(android.R.color.black, getResources().newTheme());
    }

    protected void openActivity(String activityName) {
        Log.d(TAG, "WebViewActivity.openActivity activityName = " + activityName);

        try {
            Intent intent = new Intent(this, Class.forName(activityName));
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e(TAG, "WebViewActivity.openActivity failed: " + e.getMessage());
        }
    }

    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "WebViewActivity.onNewIntent");

        String action = intent.getAction();
        if (action == null) {
            super.onNewIntent(intent);
            return;
        }

        if (action.equals(Intent.ACTION_MAIN)) {
            openDefoldActivity();
        } else {
            super.onNewIntent(intent);
        }
    }
}



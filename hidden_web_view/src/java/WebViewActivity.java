package com.blitz.hiddenwebview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.graphics.Point;
import android.view.Display;
import android.util.Log;
import android.os.Build;
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
    private WindowManager.LayoutParams touchInterceptorViewParams;

    /**
     * Этот класс обрабатывает Javascript ошибки в htmlGameView и передает их через JNI в lua код
     */
    private WebViewChromeClient webViewChromeClient;

    /**
     * Этот класс обрабатывает состояния HTML страницы в htmlGameView и передает их через JNI в lua код
     */
    private WebViewEventListener webViewEventListener;

    /**
     * Показывается ли htmlGameView в данный момент
     */
    private boolean isWebViewVisible = false;

    /**
     * Функции-обработчики, которые вызываются из native кода
     */
    public native void onPageLoading(String url, int id);
    public native void onPageFinished(String url, int id);
    public native void onReceivedError(String url, String errorMessage, int id);
    public native void onScriptFailed(String errorMessage, int id);
    private native void onScriptFinished(String result, int id);
    private native void onScriptCallback(String type, String payload);


    protected WebViewActivity getWebViewActivity() {
        return CurrentActivityAwareApplication.instance.getWebViewActivity();
    }

    protected Activity getDefoldActivity() {
        return CurrentActivityAwareApplication.instance.getDefoldActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "WebViewActivity.onCreate starts");

        setDefaultContainer();
        openDefoldActivity();

        initWebView();
        updateFullscreenMode();

        Log.d(TAG, "WebViewActivity.onCreate ends");
    }

    protected void updateFullscreenMode() {
        Log.d(TAG, "WebViewActivity.updateFullscreenMode");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            Log.d(TAG, "WebViewActivity.updateFullscreenMode KITKAT");

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            Log.d(TAG, "WebViewActivity.updateFullscreenMode VERSION_CODES.P");

            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(layoutParams);
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

        Log.d(TAG, "WebViewActivity.onConfigurationChanged");

        super.onConfigurationChanged(newConfig);
        if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
            updateFullscreenMode();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {

        Log.d(TAG, "WebViewActivity.initWebView");

        htmlGameView = new WebView(this);

        htmlGameView.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark, getResources().newTheme()));

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

        htmlGameView.setVisibility(View.GONE);

        mainContainer.addView(htmlGameView, params);

        Log.d(TAG, "WebViewActivity.initWebView view added");
    }

    public WebView getHtmlGameView() {
        return htmlGameView;
    }

    public void acceptTouchEvents(int accept) {
        Log.d(TAG, "WebViewActivity.acceptTouchEvents");

        if (touchInterceptorView == null) {
            return;
        }

        touchInterceptorView.isAcceptingTouchEvents = accept == 1;

        WindowManager wm = getDefoldActivity().getWindowManager();

        if (accept == 1) {
            Log.d(TAG, "WebViewActivity.acceptTouchEvents accept == 1");

            wm.removeViewImmediate(touchInterceptorView);
            wm.addView(touchInterceptorView, touchInterceptorViewParams);
        } else {
            Log.d(TAG, "WebViewActivity.acceptTouchEvents accept != 1");

            wm.removeViewImmediate(touchInterceptorView);
        }
    }

    private int dpToPx(double dp) {
        return (int) dp;
    }

    public void executeScript(String js, int id) {

        Log.d(TAG, "WebViewActivity.executeScript: " + js);

        htmlGameView.evaluateJavascript(js, value -> onScriptFinished(value, id));
    }

    public void addJavascriptChannel(String channel) {
        Log.d(TAG, "WebViewActivity.addJavascriptChannel: " + channel);

        JavaScriptInterface js = new JavaScriptInterface(
                getWebViewActivity(),
                (message) -> {
                    Log.d(TAG, "WebViewActivity.JavaScriptInterface channel = " + channel + ", message = " + message);
                    onScriptCallback(channel, message);
                }
        );

        htmlGameView.addJavascriptInterface(js, channel);
    }

    public void loadGame(String gamePath, int id) {

        Log.d(TAG, "WebViewActivity.loadGame");

        webViewEventListener.reset(id);
        webViewChromeClient.reset(id);

        htmlGameView.loadUrl("http://localhost:8808/" + gamePath);

        // String strData = "<html><head></head><body><div style='color:red'>HELLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO</div></body></html>";
        // String strData = "<html>\r\n<head>\r\n\t<style>\r\n\t*,html,body {\r\n\t\twidth: 100%;\r\n\t\theight: 100%;\r\n\t\tmin-width: 100%;\r\n\t\tmin-height: 100%;\r\n\t\tpadding: 0;\r\n\t\tmargin: 0;\r\n\t}\r\n\tcanvas {\r\n\t\tbackground: white;\r\n\t}\r\n\t</style>\r\n\t<script>\r\n\tvar local = true\r\n\tfunction makeParams(params) {\r\n\t\treturn JSON.stringify({\r\n\t\t\t\"is_local\" : local,\r\n\t\t\t\"params\" : params\r\n\t\t});\r\n\t}\r\n    function blitzOnSceneLoaded() {\r\n\t\tconsole.log('OnSceneLoaded');\r\n\t\tif (window._OnSceneLoaded !== undefined) {\r\n\t\t\twindow._OnSceneLoaded.postMessage(makeParams('0'));\r\n\t\t}\r\n\t}\r\n\r\n\t// https://bencentra.com/code/2014/12/05/html5-canvas-touch-events.html\r\n\r\n\tfunction init() {\r\n\t\t// Set up the canvas\r\n\t\tvar canvas = document.getElementById(\"canvas\");\r\n\t\tvar ctx = canvas.getContext(\"2d\");\r\n\r\n\t\t// Get a regular interval for drawing to the screen\r\n\t\twindow.requestAnimFrame = (function (callback) {\r\n\t\t\treturn window.requestAnimationFrame ||\r\n\t\t\t\t\t\twindow.webkitRequestAnimationFrame ||\r\n\t\t\t\t\t\twindow.mozRequestAnimationFrame ||\r\n\t\t\t\t\t\twindow.oRequestAnimationFrame ||\r\n\t\t\t\t\t\twindow.msRequestAnimationFrame ||\r\n\t\t\t\t\t\tfunction (callback) {\r\n\t\t\t\t\t\t\twindow.setTimeout(callback, 1000/60);\r\n\t\t\t\t\t\t};\r\n\t\t})();\r\n\r\n\t\tfunction resizeCanvas() {\r\n\t\t\tcanvas.width = window.innerWidth;\r\n\t\t\tcanvas.height = window.innerHeight;\r\n\r\n\t\t\t/**\r\n\t\t\t * Your drawings need to be inside this function otherwise they will be reset when\r\n\t\t\t * you resize the browser window and the canvas goes will be cleared.\r\n\t\t\t */\r\n\t\t\trenderCanvas();\r\n\r\n\t\t\tctx.font = \"36px serif\";\r\n\t\t\tctx.fillText(\"THIS IS WEBVIEW\", 25, 150);\r\n\t\t}\r\n\t    resizeCanvas();\r\n\t    window.addEventListener('resize', resizeCanvas, false);\r\n\r\n\t\t// Set up mouse events for drawing\r\n\t\tvar drawing = false;\r\n\t\tvar mousePos = { x:0, y:0 };\r\n\t\tvar lastPos = mousePos;\r\n\r\n\t\tcanvas.addEventListener(\"mousedown\", function (e) {\r\n\t\t\tdrawing = true;\r\n\t\t\tlastPos = getMousePos(canvas, e);\r\n\t\t}, false);\r\n\t\tcanvas.addEventListener(\"mouseup\", function (e) {\r\n\t\t\tdrawing = false;\r\n\t\t}, false);\r\n\t\tcanvas.addEventListener(\"mousemove\", function (e) {\r\n\t\t\tmousePos = getMousePos(canvas, e);\r\n\t\t}, false);\r\n\r\n\t\t// Set up touch events for mobile, etc\r\n\t\tcanvas.addEventListener(\"touchstart\", function (e) {\r\n\t\t\tmousePos = getTouchPos(canvas, e);\r\n\t\t\tvar touch = e.touches[0];\r\n\t\t\tvar mouseEvent = new MouseEvent(\"mousedown\", {\r\n\t\t\t\tclientX: touch.clientX,\r\n\t\t\t\tclientY: touch.clientY\r\n\t\t\t});\r\n\t\t\tcanvas.dispatchEvent(mouseEvent);\r\n\t\t}, false);\r\n\t\tcanvas.addEventListener(\"touchend\", function (e) {\r\n\t\t\tvar mouseEvent = new MouseEvent(\"mouseup\", {});\r\n\t\t\tcanvas.dispatchEvent(mouseEvent);\r\n\t\t}, false);\r\n\t\tcanvas.addEventListener(\"touchmove\", function (e) {\r\n\t\t\tvar touch = e.touches[0];\r\n\t\t\tvar mouseEvent = new MouseEvent(\"mousemove\", {\r\n\t\t\t\tclientX: touch.clientX,\r\n\t\t\t\tclientY: touch.clientY\r\n\t\t\t});\r\n\t\t\tcanvas.dispatchEvent(mouseEvent);\r\n\t\t}, false);\r\n\r\n\t\t// Prevent scrolling when touching the canvas\r\n\t\tdocument.body.addEventListener(\"touchstart\", function (e) {\r\n\t\t\tif (e.target == canvas) {\r\n\t\t\t\te.preventDefault();\r\n\t\t\t}\r\n\t\t}, {passive: false});\r\n\t\tdocument.body.addEventListener(\"touchend\", function (e) {\r\n\t\t\tif (e.target == canvas) {\r\n\t\t\t\te.preventDefault();\r\n\t\t\t}\r\n\t\t}, {passive: false});\r\n\t\tdocument.body.addEventListener(\"touchmove\", function (e) {\r\n\t\t\tif (e.target == canvas) {\r\n\t\t\t\te.preventDefault();\r\n\t\t\t}\r\n\t\t}, {passive: false});\r\n\r\n\t\t// Get the position of the mouse relative to the canvas\r\n\t\tfunction getMousePos(canvasDom, mouseEvent) {\r\n\t\t\tvar rect = canvasDom.getBoundingClientRect();\r\n\t\t\treturn {\r\n\t\t\t\tx: mouseEvent.clientX - rect.left,\r\n\t\t\t\ty: mouseEvent.clientY - rect.top\r\n\t\t\t};\r\n\t\t}\r\n\r\n\t\t// Get the position of a touch relative to the canvas\r\n\t\tfunction getTouchPos(canvasDom, touchEvent) {\r\n\t\t\tvar rect = canvasDom.getBoundingClientRect();\r\n\t\t\treturn {\r\n\t\t\t\tx: touchEvent.touches[0].clientX - rect.left,\r\n\t\t\t\ty: touchEvent.touches[0].clientY - rect.top\r\n\t\t\t};\r\n\t\t}\r\n\r\n\t\t// Draw to the canvas\r\n\t\tfunction renderCanvas() {\r\n\t\t\tif (drawing) {\r\n\t\t\t\tctx.moveTo(lastPos.x, lastPos.y);\r\n\t\t\t\tctx.lineTo(mousePos.x, mousePos.y);\r\n\t\t\t\tctx.stroke();\r\n\t\t\t\tlastPos = mousePos;\r\n\t\t\t}\r\n\t\t}\r\n\r\n\t\tfunction clearCanvas() {\r\n\t\t\tcanvas.width = canvas.width;\r\n\t\t}\r\n\r\n\t\t// Allow for animation\r\n\t\t(function drawLoop () {\r\n\t\t\trequestAnimFrame(drawLoop);\r\n\t\t\trenderCanvas();\r\n\t\t})();\r\n\t}\r\n\r\n\t// document.addEventListener(\"click\", function(event) {\r\n\tdocument.addEventListener(\"DOMContentLoaded\", function(event) {\r\n\t\tinit();\r\n\t\tblitzOnSceneLoaded();\r\n\t});\r\n\t</script>\r\n</head>\r\n<body>\r\n\t<canvas id=\"canvas\">\r\n\t  Your browser does not support canvas element.\r\n\t</canvas>\r\n</body>\r\n</html>";
        // htmlGameView.loadData(strData, "text/html", "UTF-8");
    }

    public void loadWebPage(String gamePath, int id) {

        Log.d(TAG, "WebViewActivity.loadWebPage");

        webViewEventListener.reset(id);
        webViewChromeClient.reset(id);

        htmlGameView.loadUrl(gamePath);
    }

    public void setDebugEnabled(int flag) {
        WebView.setWebContentsDebuggingEnabled(flag == 1);
    }

    public void changeVisibility(int visible) {

        Log.d(TAG, "WebViewActivity.changeVisibility visible = " + visible);

        isWebViewVisible = (visible != 0);
        htmlGameView.setVisibility(isWebViewVisible ? View.VISIBLE : View.GONE);

        if (visible == 0) {
            htmlGameView.goBack();

            WindowManager wm = getDefoldActivity().getWindowManager();
            wm.removeViewImmediate(touchInterceptorView);
        }
    }

    public boolean isWebViewVisible() {
        return isWebViewVisible;
    }

    public void setTouchInterceptor(double x, double y, double width, double height) {
        Log.d(TAG, "WebViewActivity.setTouchInterceptor");

        WindowManager wm = getDefoldActivity().getWindowManager();

        if (touchInterceptorView != null) {
            wm.removeViewImmediate(touchInterceptorView);
        }

        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        touchInterceptorView = new TouchInterceptorView(getDefoldActivity());
        touchInterceptorView.setBackground(new ColorDrawable(Color.RED)); // Color.TRANSPARENT

        touchInterceptorViewParams = new WindowManager.LayoutParams();
        touchInterceptorViewParams.gravity = Gravity.CENTER;
        touchInterceptorViewParams.x = dpToPx(screenWidth * x);
        touchInterceptorViewParams.y = dpToPx(screenHeight * y);
        touchInterceptorViewParams.width = dpToPx(screenWidth * width);
        touchInterceptorViewParams.height = dpToPx(screenHeight * height);
        touchInterceptorViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        touchInterceptorViewParams.alpha = 0.5f;
        touchInterceptorViewParams.format = PixelFormat.TRANSLUCENT;

        wm.addView(touchInterceptorView, touchInterceptorViewParams);
    }

    public void setPositionAndSize(double x, double y, double width, double height) {
        Log.d(TAG, "WebViewActivity.setPositionAndSize height = " + height + ", width = " + width +
                ", x = " + x + ", y = " + y);

        Display display = getDefoldActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        Log.d(TAG, "WebViewActivity.setPositionAndSize screenWidth = " + screenWidth +
                ", screenHeight = " + screenHeight);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) htmlGameView.getLayoutParams();
        params.width = dpToPx(screenWidth * width);
        params.height = dpToPx(screenHeight * height);
        params.gravity = Gravity.TOP | Gravity.START;
        params.setMargins(dpToPx(screenWidth * x), dpToPx(screenHeight * y), 0, 0);

        htmlGameView.setLayoutParams(params);
        htmlGameView.requestLayout();
    }

    public void centerWebView() {
        Log.d(TAG, "WebViewActivity.centerWebView");

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) htmlGameView.getLayoutParams();
        params.gravity = Gravity.CENTER;
        params.setMargins(0, 0, 0, 0);

        htmlGameView.setLayoutParams(params);
        htmlGameView.requestLayout();
    }

    public void matchScreenSize() {
        Log.d(TAG, "WebViewActivity.matchScreenSize");

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );

        params.gravity = Gravity.CENTER;

        htmlGameView.setLayoutParams(params);
        htmlGameView.requestLayout();
    }

    private void setDefaultContainer() {
        Log.d(TAG, "WebViewActivity.setDefaultContainer");

        mainContainer = new FrameLayout(this);
        mainContainer.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright, getResources().newTheme()));
        setContentView(mainContainer);
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

    protected void openDefoldActivity() {
        openActivity(CurrentActivityAwareApplication.DEFOLD_ACTIVITY);
    }

    protected void openWebViewActivity() {
        openActivity(CurrentActivityAwareApplication.WEBVIEW_ACTIVITY);
    }

    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "WebViewActivity.onNewIntent");

        String action = intent.getAction();
        if (action == null) {
            Log.w(TAG, "WebViewActivity.onNewIntent action is null");
            super.onNewIntent(intent);
            return;
        }

        if (action.equals(Intent.ACTION_MAIN)) {
            Log.d(TAG, "WebViewActivity.onNewIntent Intent.ACTION_MAIN");
            openDefoldActivity();
        } else {
            Log.d(TAG, "WebViewActivity.onNewIntent onNewIntent");

            super.onNewIntent(intent);
        }
    }
}



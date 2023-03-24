package com.blitz.hiddenwebview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Size;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


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

        initWebViewFullscreenMode();
        initWebViewContainer();
        initWebView();

        openDefoldActivity();
    }

    protected void initWebViewFullscreenMode() {
        Log.d(TAG, "WebViewActivity.initWebViewFullscreenMode");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
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
            initWebViewFullscreenMode();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG, "WebViewActivity.onConfigurationChanged");

        if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
            initWebViewFullscreenMode();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        Log.d(TAG, "WebViewActivity.initWebView");

        htmlGameView = new WebView(this);
        htmlGameView.setBackgroundColor(getBlackColor());

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
    }

    public void setPositionAndSize(double x, double y, double width, double height) {
        Log.d(TAG, "WebViewActivity.setPositionAndSize x = " + x + ", y = " + y +
                ", width = " + width + ", height = " + height);

        Size screenSize = getScreenSize();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) htmlGameView.getLayoutParams();
        params.width = dpToPx(screenSize.getWidth() * width);
        params.height = dpToPx(screenSize.getHeight() * height);
        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.setMargins(dpToPx(screenSize.getWidth() * x), dpToPx(screenSize.getHeight() * y), 0, 0);

        htmlGameView.setLayoutParams(params);
        htmlGameView.requestLayout();
    }

    public WebView getHtmlGameView() {
        return htmlGameView;
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
                    Log.d(TAG, "WebViewActivity.addJavascriptChannel channel = " + channel + ", message = " + message);
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

        // String strData = "<html>\r\n<head>\r\n\t<style>\r\n\t*,html,body {\r\n\t\twidth: 100%;\r\n\t\theight: 100%;\r\n\t\tmin-width: 100%;\r\n\t\tmin-height: 100%;\r\n\t\tpadding: 0;\r\n\t\tmargin: 0;\r\n\t}\r\n\tcanvas {\r\n\t\tbackground: white;\r\n\t}\r\n\t</style>\r\n\t<script>\r\n\tvar local = true\r\n\tfunction makeParams(params) {\r\n\t\treturn JSON.stringify({\r\n\t\t\t\"is_local\" : local,\r\n\t\t\t\"params\" : params\r\n\t\t});\r\n\t}\r\n    function blitzOnSceneLoaded() {\r\n\t\tconsole.log('OnSceneLoaded');\r\n\t\tif (window._OnSceneLoaded !== undefined) {\r\n\t\t\twindow._OnSceneLoaded.postMessage(makeParams('0'));\r\n\t\t}\r\n\t}\r\n\r\n\t// https://bencentra.com/code/2014/12/05/html5-canvas-touch-events.html\r\n\r\n\tfunction init() {\r\n\t\t// Set up the canvas\r\n\t\tvar canvas = document.getElementById(\"canvas\");\r\n\t\tvar ctx = canvas.getContext(\"2d\");\r\n\r\n\t\t// Get a regular interval for drawing to the screen\r\n\t\twindow.requestAnimFrame = (function (callback) {\r\n\t\t\treturn window.requestAnimationFrame ||\r\n\t\t\t\t\t\twindow.webkitRequestAnimationFrame ||\r\n\t\t\t\t\t\twindow.mozRequestAnimationFrame ||\r\n\t\t\t\t\t\twindow.oRequestAnimationFrame ||\r\n\t\t\t\t\t\twindow.msRequestAnimationFrame ||\r\n\t\t\t\t\t\tfunction (callback) {\r\n\t\t\t\t\t\t\twindow.setTimeout(callback, 1000/60);\r\n\t\t\t\t\t\t};\r\n\t\t})();\r\n\r\n\t\tfunction resizeCanvas() {\r\n\t\t\tcanvas.width = window.innerWidth;\r\n\t\t\tcanvas.height = window.innerHeight;\r\n\r\n\t\t\t/**\r\n\t\t\t * Your drawings need to be inside this function otherwise they will be reset when\r\n\t\t\t * you resize the browser window and the canvas goes will be cleared.\r\n\t\t\t * /\r\n\t\t\trenderCanvas();\r\n\r\n\t\t\tctx.font = \"36px serif\";\r\n\t\t\tctx.fillText(\"THIS IS WEBVIEW\", 25, 150);\r\n\t\t}\r\n\t    resizeCanvas();\r\n\t    window.addEventListener('resize', resizeCanvas, false);\r\n\r\n\t\t// Set up mouse events for drawing\r\n\t\tvar drawing = false;\r\n\t\tvar mousePos = { x:0, y:0 };\r\n\t\tvar lastPos = mousePos;\r\n\r\n\t\tcanvas.addEventListener(\"mousedown\", function (e) {\r\n\t\t\tdrawing = true;\r\n\t\t\tlastPos = getMousePos(canvas, e);\r\n\t\t}, false);\r\n\t\tcanvas.addEventListener(\"mouseup\", function (e) {\r\n\t\t\tdrawing = false;\r\n\t\t}, false);\r\n\t\tcanvas.addEventListener(\"mousemove\", function (e) {\r\n\t\t\tmousePos = getMousePos(canvas, e);\r\n\t\t}, false);\r\n\r\n\t\t// Set up touch events for mobile, etc\r\n\t\tcanvas.addEventListener(\"touchstart\", function (e) {\r\n\t\t\tmousePos = getTouchPos(canvas, e);\r\n\t\t\tvar touch = e.touches[0];\r\n\t\t\tvar mouseEvent = new MouseEvent(\"mousedown\", {\r\n\t\t\t\tclientX: touch.clientX,\r\n\t\t\t\tclientY: touch.clientY\r\n\t\t\t});\r\n\t\t\tcanvas.dispatchEvent(mouseEvent);\r\n\t\t}, false);\r\n\t\tcanvas.addEventListener(\"touchend\", function (e) {\r\n\t\t\tvar mouseEvent = new MouseEvent(\"mouseup\", {});\r\n\t\t\tcanvas.dispatchEvent(mouseEvent);\r\n\t\t}, false);\r\n\t\tcanvas.addEventListener(\"touchmove\", function (e) {\r\n\t\t\tvar touch = e.touches[0];\r\n\t\t\tvar mouseEvent = new MouseEvent(\"mousemove\", {\r\n\t\t\t\tclientX: touch.clientX,\r\n\t\t\t\tclientY: touch.clientY\r\n\t\t\t});\r\n\t\t\tcanvas.dispatchEvent(mouseEvent);\r\n\t\t}, false);\r\n\r\n\t\t// Prevent scrolling when touching the canvas\r\n\t\tdocument.body.addEventListener(\"touchstart\", function (e) {\r\n\t\t\tif (e.target == canvas) {\r\n\t\t\t\te.preventDefault();\r\n\t\t\t}\r\n\t\t}, {passive: false});\r\n\t\tdocument.body.addEventListener(\"touchend\", function (e) {\r\n\t\t\tif (e.target == canvas) {\r\n\t\t\t\te.preventDefault();\r\n\t\t\t}\r\n\t\t}, {passive: false});\r\n\t\tdocument.body.addEventListener(\"touchmove\", function (e) {\r\n\t\t\tif (e.target == canvas) {\r\n\t\t\t\te.preventDefault();\r\n\t\t\t}\r\n\t\t}, {passive: false});\r\n\r\n\t\t// Get the position of the mouse relative to the canvas\r\n\t\tfunction getMousePos(canvasDom, mouseEvent) {\r\n\t\t\tvar rect = canvasDom.getBoundingClientRect();\r\n\t\t\treturn {\r\n\t\t\t\tx: mouseEvent.clientX - rect.left,\r\n\t\t\t\ty: mouseEvent.clientY - rect.top\r\n\t\t\t};\r\n\t\t}\r\n\r\n\t\t// Get the position of a touch relative to the canvas\r\n\t\tfunction getTouchPos(canvasDom, touchEvent) {\r\n\t\t\tvar rect = canvasDom.getBoundingClientRect();\r\n\t\t\treturn {\r\n\t\t\t\tx: touchEvent.touches[0].clientX - rect.left,\r\n\t\t\t\ty: touchEvent.touches[0].clientY - rect.top\r\n\t\t\t};\r\n\t\t}\r\n\r\n\t\t// Draw to the canvas\r\n\t\tfunction renderCanvas() {\r\n\t\t\tif (drawing) {\r\n\t\t\t\tctx.moveTo(lastPos.x, lastPos.y);\r\n\t\t\t\tctx.lineTo(mousePos.x, mousePos.y);\r\n\t\t\t\tctx.stroke();\r\n\t\t\t\tlastPos = mousePos;\r\n\t\t\t}\r\n\t\t}\r\n\r\n\t\tfunction clearCanvas() {\r\n\t\t\tcanvas.width = canvas.width;\r\n\t\t}\r\n\r\n\t\t// Allow for animation\r\n\t\t(function drawLoop () {\r\n\t\t\trequestAnimFrame(drawLoop);\r\n\t\t\trenderCanvas();\r\n\t\t})();\r\n\t}\r\n\r\n\t// document.addEventListener(\"click\", function(event) {\r\n\tdocument.addEventListener(\"DOMContentLoaded\", function(event) {\r\n\t\tinit();\r\n\t\tblitzOnSceneLoaded();\r\n\t});\r\n\t</script>\r\n</head>\r\n<body>\r\n\t<canvas id=\"canvas\">\r\n\t  Your browser does not support canvas element.\r\n\t</canvas>\r\n</body>\r\n</html>";
        // htmlGameView.loadData(strData, "text/html", "UTF-8");
    }

    public void loadWebPage(String gamePath, int id) {
        Log.d(TAG, "WebViewActivity.loadWebPage");

        webViewEventListener.reset(id);
        webViewChromeClient.reset(id);

        htmlGameView.loadUrl(gamePath);
    }

    public void setDebugEnabled(int flag) {
        isDebugEnabled = (flag != 0);
        WebView.setWebContentsDebuggingEnabled(isDebugEnabled);
    }

    protected WindowManager.LayoutParams getTouchInterceptorViewParams(double x, double y, double width, double height) {
        Size screenSize = getScreenSize();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER;
        params.x = dpToPx(screenSize.getWidth() * x);
        params.y = dpToPx(screenSize.getHeight() * y);
        params.width = dpToPx(screenSize.getWidth() * width);
        params.height = dpToPx(screenSize.getHeight() * height);
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.alpha = 0.3f;
        params.format = PixelFormat.TRANSLUCENT;
        return params;
    }

    protected boolean isTouchInterceptorViewActive() {
        return touchInterceptorView != null && touchInterceptorView.getWindowToken() != null;
    }

    protected void addTouchInterceptorView() {
        WindowManager wm = getDefoldActivity().getWindowManager();
        touchInterceptorView = new TouchInterceptorView(getDefoldActivity(), getWebViewActivity());
        touchInterceptorView.setBackground(new ColorDrawable(isDebugEnabled ? Color.GREEN : Color.TRANSPARENT));
        wm.addView(touchInterceptorView, touchInterceptorViewParams);
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

        touchInterceptorViewParams = getTouchInterceptorViewParams(x, y, width, height);

        removeTouchInterceptorView();
        addTouchInterceptorView();
    }

    public void acceptTouchEvents(int accept) {
        Log.d(TAG, "WebViewActivity.acceptTouchEvents accept = " + accept);

        if (isTouchInterceptorViewActive()) {
            touchInterceptorView.setAcceptingTouchEvents(accept != 0);
        }
    }

    public void changeVisibility(int visible) {
        Log.d(TAG, "WebViewActivity.changeVisibility visible = " + visible);

        boolean isWebViewVisible = (visible != 0);
        htmlGameView.setVisibility(isWebViewVisible ? View.VISIBLE : View.GONE);

        if (!isWebViewVisible) {
            htmlGameView.goBack();
            removeTouchInterceptorView();
        }
    }

    public boolean isWebViewVisible() {
        return htmlGameView.getVisibility() == View.VISIBLE;
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

    private void initWebViewContainer() {
        Log.d(TAG, "WebViewActivity.setDefaultContainer");

        mainContainer = new FrameLayout(this);
        mainContainer.setBackgroundColor(getBlackColor());
        setContentView(mainContainer);
    }

    protected int getBlackColor() {
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



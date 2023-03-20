package com.blitz.hiddenwebview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.webkit.JavascriptInterface;
import android.graphics.Point;
import android.view.Display;
import android.util.Log;
import android.os.Build;
import android.content.res.Configuration;

class TouchInterceptorView extends View {
    public boolean isAcceptingTouchEvents = true;

    public TouchInterceptorView(Context context) {
        super(context);
    }

    public TouchInterceptorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchInterceptorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isAcceptingTouchEvents) {
            dispatchModifiedMotionEvent(event);
        }
        return false;
    }

    protected boolean dispatchModifiedMotionEvent(MotionEvent event) {
        int[] location = new int[2];
        getLocationOnScreen(location);
        int offsetX = location[0];
        int offsetY = location[1];
        
        MotionEvent hackedEvent = MotionEvent.obtain(event.getDownTime(),
            event.getEventTime(), event.getAction(), event.getX() + offsetX,
            event.getY() + offsetY, event.getMetaState());

        boolean result = FakeWebViewActivity.htmlGameView.dispatchTouchEvent(hackedEvent);
        hackedEvent.recycle();
        return result;
    }
}

public class FakeWebViewActivity extends Activity {
    // это WebView живет все время сколько живет приложение
    // поэтому утечки памяти от хранения в статичной переменной не будет
    // так же храним как static, чтобы избежать уничтожения самой view
    @SuppressLint("StaticFieldLeak")
    static WebView htmlGameView;
    FrameLayout mainContainer;
    static TouchInterceptorView touchInterceptorView;
    static WindowManager.LayoutParams touchInterceptorViewParams;
    static DefoldWebViewInterface defoldWebViewInterface;

    static CustomWebChromeClient customWebChromeClient;
    static CustomWebViewClient customWebViewClient;

    public static final String TAG = "HiddenWebViewLog";

    //private static final long ACTIVITY_DETECT_DELAY_MS = 10;
    public static boolean webViewActive = false;

    public native void onPageLoading(String url, int id);
    public native void onPageFinished(String url, int id);
    public native void onReceivedError(String url, String errorMessage, int id);
    public native void onScriptFailed(String errorMessage, int id);

    private static class CustomWebChromeClient extends WebChromeClient {
        private final FakeWebViewActivity webviewJNI;
        private int scriptId;

        public CustomWebChromeClient(FakeWebViewActivity webviewJNI) {

            Log.d(FakeWebViewActivity.TAG, "CustomWebChromeClient.CustomWebChromeClient");

            this.webviewJNI = webviewJNI;
            reset(-1);
        }

        public void reset(int scriptId) {
            this.scriptId = scriptId;
        }

        @SuppressLint("DefaultLocale")
        @Override
        public boolean onConsoleMessage(ConsoleMessage msg) {
            Log.d(FakeWebViewActivity.TAG, "CustomWebChromeClient.onConsoleMessage: " + msg.message());

            if (msg.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
                String errorMessage = String.format("js:%d: %s", msg.lineNumber(), msg.message());
                Log.d(FakeWebViewActivity.TAG, "CustomWebChromeClient.onConsoleMessage errorMessage: " + errorMessage);
                webviewJNI.onScriptFailed(errorMessage, scriptId);
                return true;
            }
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static class CustomWebViewClient extends WebViewClient {
        private final FakeWebViewActivity webviewJNI;

        private boolean hasError;
        private int requestId;

        public void reset(int scriptId) {


            Log.d(FakeWebViewActivity.TAG, "CustomWebViewClient.reset");

            this.requestId = scriptId;
            this.hasError = false;
        }

        public CustomWebViewClient(FakeWebViewActivity webviewJNI) {
            super();


            Log.d(FakeWebViewActivity.TAG, "CustomWebViewClient");


            this.webviewJNI = webviewJNI;
            reset();
        }

        public void reset() {

            Log.d(FakeWebViewActivity.TAG, "CustomWebChromeClient.reset");

            this.hasError = false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.d(FakeWebViewActivity.TAG, "CustomWebChromeClient.shouldOverrideUrlLoading");

            webviewJNI.onPageLoading(url, requestId);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            Log.d(FakeWebViewActivity.TAG, "CustomWebChromeClient.onPageFinished");

            webviewJNI.onPageFinished(url, requestId);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            Log.d(FakeWebViewActivity.TAG, "CustomWebChromeClient.onReceivedError");

            if (!this.hasError) {
                this.hasError = true;
                webviewJNI.onReceivedError(failingUrl, description, requestId);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "FakeWebViewActivity.onCreate starts");

        setDefaultContainer();
        openDefoldMainSurfaceActivity();


        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(
            new Runnable() {
                public void run() {
                    // openWebViewActivity();
                }
            },
        5000);


        initWebView();
        updateFullscreenMode();

        Log.d(TAG, "FakeWebViewActivity.onCreate ends");
    }

    private void updateFullscreenMode() {
        Log.d(TAG, "FakeWebViewActivity.updateFullscreenMode");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            Log.d(TAG, "FakeWebViewActivity.updateFullscreenMode KITKAT");

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            Log.d(TAG, "FakeWebViewActivity.updateFullscreenMode VERSION_CODES.P");

            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(layoutParams);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Log.d(TAG, "FakeWebViewActivity.onWindowFocusChanged");

        if (hasFocus) {
            updateFullscreenMode();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        Log.d(TAG, "FakeWebViewActivity.onConfigurationChanged");

        super.onConfigurationChanged(newConfig);
        if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
            updateFullscreenMode();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {

        Log.d(TAG, "FakeWebViewActivity.initWebView");

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

        webSettings.setAppCacheEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        htmlGameView.setFocusable(true);
        htmlGameView.setFocusableInTouchMode(true);
        htmlGameView.setClickable(true);
        htmlGameView.requestFocus(View.FOCUS_DOWN);
        htmlGameView.setHapticFeedbackEnabled(true);
        htmlGameView.requestFocusFromTouch();

        htmlGameView.addJavascriptInterface(
            new JavaScriptInterface(
                this, 
                (type, data) -> {
                        
                    Log.d(TAG, "JavaScriptInterface(type, data)");

                    defoldWebViewInterface.onScriptCallback(type, data);
                }
            ), 
            "defoldHandler"
        );

        customWebChromeClient = new CustomWebChromeClient(this);
        customWebViewClient = new CustomWebViewClient(this);

        htmlGameView.setWebChromeClient(customWebChromeClient);
        htmlGameView.setWebViewClient(customWebViewClient);

        htmlGameView.setVisibility(View.GONE);

        mainContainer.addView(htmlGameView, params);

        Log.d(TAG, "FakeWebViewActivity.initWebView view added");
    }

    public static void acceptTouchEvents(int accept) {
        Log.d(TAG, "FakeWebViewActivity.acceptTouchEvents");

        if (touchInterceptorView == null) {
            return;
        }

        touchInterceptorView.isAcceptingTouchEvents = accept == 1;

        WindowManager wm = CurrentActivityAwareApplication.currentlyOpenedActivity.getWindowManager();

        if (accept == 1) {
            Log.d(TAG, "FakeWebViewActivity.acceptTouchEvents accept == 1");

            wm.removeViewImmediate(touchInterceptorView);
            wm.addView(touchInterceptorView, touchInterceptorViewParams);
        } else {
            Log.d(TAG, "FakeWebViewActivity.acceptTouchEvents accept != 1");

            wm.removeViewImmediate(touchInterceptorView);
        }
    }

    private static int dpToPx(double dp) {
        return (int) dp;

        /*
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                (float) dp,
                CurrentActivityAwareApplication.currentlyOpenedActivity.getResources().getDisplayMetrics()
        );
        */
    }

    public static void executeScript(String js, int id) {
        
        Log.d(TAG, "FakeWebViewActivity.executeScript: " + js);

        htmlGameView.evaluateJavascript(js, value -> defoldWebViewInterface.onScriptFinished(value, id));
    }

    public static void loadGame(String gamePath, int id) {

        Log.d(TAG, "FakeWebViewActivity.loadGame");

        customWebViewClient.reset(id);
        customWebChromeClient.reset(id);

        htmlGameView.loadUrl("http://localhost:8808/" + gamePath);

        // String strData = "<html><head></head><body><div style='color:red'>HELLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO</div></body></html>";
        // String strData = "<html>\r\n<head>\r\n\t<style>\r\n\t*,html,body {\r\n\t\twidth: 100%;\r\n\t\theight: 100%;\r\n\t\tmin-width: 100%;\r\n\t\tmin-height: 100%;\r\n\t\tpadding: 0;\r\n\t\tmargin: 0;\r\n\t}\r\n\tcanvas {\r\n\t\tbackground: white;\r\n\t}\r\n\t</style>\r\n\t<script>\r\n\t// https://bencentra.com/code/2014/12/05/html5-canvas-touch-events.html\r\n\r\n\tfunction init() {\r\n\t\t// Set up the canvas\r\n\t\tvar canvas = document.getElementById(\"canvas\");\r\n\t\tvar ctx = canvas.getContext(\"2d\");\r\n\r\n\t\t// Get a regular interval for drawing to the screen\r\n\t\twindow.requestAnimFrame = (function (callback) {\r\n\t\t\treturn window.requestAnimationFrame ||\r\n\t\t\t\t\t\twindow.webkitRequestAnimationFrame ||\r\n\t\t\t\t\t\twindow.mozRequestAnimationFrame ||\r\n\t\t\t\t\t\twindow.oRequestAnimationFrame ||\r\n\t\t\t\t\t\twindow.msRequestAnimationFrame ||\r\n\t\t\t\t\t\tfunction (callback) {\r\n\t\t\t\t\t\t\twindow.setTimeout(callback, 1000/60);\r\n\t\t\t\t\t\t};\r\n\t\t})();\r\n\r\n\t\tfunction resizeCanvas() {\r\n\t\t\tcanvas.width = window.innerWidth;\r\n\t\t\tcanvas.height = window.innerHeight;\r\n\r\n\t\t\t/**\r\n\t\t\t * Your drawings need to be inside this function otherwise they will be reset when\r\n\t\t\t * you resize the browser window and the canvas goes will be cleared.\r\n\t\t\t */\r\n\t\t\trenderCanvas();\r\n\r\n\t\t\tctx.font = \"36px serif\";\r\n\t\t\tctx.fillText(\"THIS IS WEBVIEW\", 25, 150);\r\n\t\t}\r\n\t    resizeCanvas();\r\n\t    window.addEventListener('resize', resizeCanvas, false);\r\n\r\n\t\t// Set up mouse events for drawing\r\n\t\tvar drawing = false;\r\n\t\tvar mousePos = { x:0, y:0 };\r\n\t\tvar lastPos = mousePos;\r\n\r\n\t\tcanvas.addEventListener(\"mousedown\", function (e) {\r\n\t\t\tdrawing = true;\r\n\t\t\tlastPos = getMousePos(canvas, e);\r\n\t\t}, false);\r\n\t\tcanvas.addEventListener(\"mouseup\", function (e) {\r\n\t\t\tdrawing = false;\r\n\t\t}, false);\r\n\t\tcanvas.addEventListener(\"mousemove\", function (e) {\r\n\t\t\tmousePos = getMousePos(canvas, e);\r\n\t\t}, false);\r\n\r\n\t\t// Set up touch events for mobile, etc\r\n\t\tcanvas.addEventListener(\"touchstart\", function (e) {\r\n\t\t\tmousePos = getTouchPos(canvas, e);\r\n\t\t\tvar touch = e.touches[0];\r\n\t\t\tvar mouseEvent = new MouseEvent(\"mousedown\", {\r\n\t\t\t\tclientX: touch.clientX,\r\n\t\t\t\tclientY: touch.clientY\r\n\t\t\t});\r\n\t\t\tcanvas.dispatchEvent(mouseEvent);\r\n\t\t}, false);\r\n\t\tcanvas.addEventListener(\"touchend\", function (e) {\r\n\t\t\tvar mouseEvent = new MouseEvent(\"mouseup\", {});\r\n\t\t\tcanvas.dispatchEvent(mouseEvent);\r\n\t\t}, false);\r\n\t\tcanvas.addEventListener(\"touchmove\", function (e) {\r\n\t\t\tvar touch = e.touches[0];\r\n\t\t\tvar mouseEvent = new MouseEvent(\"mousemove\", {\r\n\t\t\t\tclientX: touch.clientX,\r\n\t\t\t\tclientY: touch.clientY\r\n\t\t\t});\r\n\t\t\tcanvas.dispatchEvent(mouseEvent);\r\n\t\t}, false);\r\n\r\n\t\t// Prevent scrolling when touching the canvas\r\n\t\tdocument.body.addEventListener(\"touchstart\", function (e) {\r\n\t\t\tif (e.target == canvas) {\r\n\t\t\t\te.preventDefault();\r\n\t\t\t}\r\n\t\t}, {passive: false});\r\n\t\tdocument.body.addEventListener(\"touchend\", function (e) {\r\n\t\t\tif (e.target == canvas) {\r\n\t\t\t\te.preventDefault();\r\n\t\t\t}\r\n\t\t}, {passive: false});\r\n\t\tdocument.body.addEventListener(\"touchmove\", function (e) {\r\n\t\t\tif (e.target == canvas) {\r\n\t\t\t\te.preventDefault();\r\n\t\t\t}\r\n\t\t}, {passive: false});\r\n\r\n\t\t// Get the position of the mouse relative to the canvas\r\n\t\tfunction getMousePos(canvasDom, mouseEvent) {\r\n\t\t\tvar rect = canvasDom.getBoundingClientRect();\r\n\t\t\treturn {\r\n\t\t\t\tx: mouseEvent.clientX - rect.left,\r\n\t\t\t\ty: mouseEvent.clientY - rect.top\r\n\t\t\t};\r\n\t\t}\r\n\r\n\t\t// Get the position of a touch relative to the canvas\r\n\t\tfunction getTouchPos(canvasDom, touchEvent) {\r\n\t\t\tvar rect = canvasDom.getBoundingClientRect();\r\n\t\t\treturn {\r\n\t\t\t\tx: touchEvent.touches[0].clientX - rect.left,\r\n\t\t\t\ty: touchEvent.touches[0].clientY - rect.top\r\n\t\t\t};\r\n\t\t}\r\n\r\n\t\t// Draw to the canvas\r\n\t\tfunction renderCanvas() {\r\n\t\t\tif (drawing) {\r\n\t\t\t\tctx.moveTo(lastPos.x, lastPos.y);\r\n\t\t\t\tctx.lineTo(mousePos.x, mousePos.y);\r\n\t\t\t\tctx.stroke();\r\n\t\t\t\tlastPos = mousePos;\r\n\t\t\t}\r\n\t\t}\r\n\r\n\t\tfunction clearCanvas() {\r\n\t\t\tcanvas.width = canvas.width;\r\n\t\t}\r\n\r\n\t\t// Allow for animation\r\n\t\t(function drawLoop () {\r\n\t\t\trequestAnimFrame(drawLoop);\r\n\t\t\trenderCanvas();\r\n\t\t})();\r\n\t}\r\n\r\n\t// document.addEventListener(\"click\", function(event) {\r\n\tdocument.addEventListener(\"DOMContentLoaded\", function(event) {\r\n\t\tinit();\r\n\t});\r\n\t</script>\r\n</head>\r\n<body>\r\n\t<canvas id=\"canvas\">\r\n\t  Your browser does not support canvas element.\r\n\t</canvas>\r\n</body>\r\n</html>";
        // htmlGameView.loadData(strData, "text/html", "UTF-8");
    }

    public static void loadWebPage(String gamePath, int id) {

        Log.d(TAG, "FakeWebViewActivity.loadWebPage");

        customWebViewClient.reset(id);
        customWebChromeClient.reset(id);

        htmlGameView.loadUrl(gamePath);
    }

    public static void setDebugEnabled(int flag) {
        WebView.setWebContentsDebuggingEnabled(flag == 1);
    }

    public static void changeVisibility(int visible) {

        Log.d(TAG, "FakeWebViewActivity.changeVisibility visible = " + visible);

        webViewActive = visible == 1;
        htmlGameView.setVisibility(webViewActive ? View.VISIBLE : View.GONE);

        if (visible == 0) {
            htmlGameView.goBack();

            WindowManager wm = CurrentActivityAwareApplication.currentlyOpenedActivity.getWindowManager();
            wm.removeViewImmediate(touchInterceptorView);
        }
    }

    public static void setTouchInterceptor(double x, double y, double width, double height) {
        Log.d(TAG, "FakeWebViewActivity.setTouchInterceptor");

        if (touchInterceptorView != null) {
            WindowManager wm = CurrentActivityAwareApplication.currentlyOpenedActivity.getWindowManager();
            wm.removeViewImmediate(touchInterceptorView);
        }

        Display display = CurrentActivityAwareApplication.currentlyOpenedActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        touchInterceptorView = new TouchInterceptorView(CurrentActivityAwareApplication.currentlyOpenedActivity);
        touchInterceptorView.setBackground(new ColorDrawable(Color.RED)); // Color.TRANSPARENT

        touchInterceptorViewParams = new WindowManager.LayoutParams();
        touchInterceptorViewParams.gravity = Gravity.CENTER; // Gravity.TOP | Gravity.START;
        touchInterceptorViewParams.x = dpToPx(screenWidth * x);
        touchInterceptorViewParams.y = dpToPx(screenHeight * y);
        touchInterceptorViewParams.width = dpToPx(screenWidth * width);
        touchInterceptorViewParams.height = dpToPx(screenHeight * height);
        touchInterceptorViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        touchInterceptorViewParams.alpha = 0.5f;
        touchInterceptorViewParams.format = PixelFormat.TRANSLUCENT;

        WindowManager wm = CurrentActivityAwareApplication.currentlyOpenedActivity.getWindowManager();
        wm.addView(touchInterceptorView, touchInterceptorViewParams);
    }

    public static void setPositionAndSize(double x, double y, double width, double height) {
        Log.d(TAG, "FakeWebViewActivity.setPositionAndSize height = " + height + ", width = " + width +
            ", x = " + x + ", y = " + y);

        Display display = CurrentActivityAwareApplication.currentlyOpenedActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        Log.d(TAG, "FakeWebViewActivity.setPositionAndSize screenWidth = " + screenWidth + 
            ", screenHeight = " + screenHeight);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) htmlGameView.getLayoutParams();
        params.width = dpToPx(screenWidth * width);
        params.height = dpToPx(screenHeight * height);
        params.gravity = Gravity.TOP | Gravity.START;
        params.setMargins(dpToPx(screenWidth * x), dpToPx(screenHeight * y), 0, 0);

        htmlGameView.setLayoutParams(params);
        htmlGameView.requestLayout();
    }

    public static void centerWebView() {
        Log.d(TAG, "FakeWebViewActivity.centerWebView");

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) htmlGameView.getLayoutParams();
        params.gravity = Gravity.CENTER;
        params.setMargins(0, 0, 0, 0);

        htmlGameView.setLayoutParams(params);
        htmlGameView.requestLayout();
    }

    public static void matchScreenSize() {
        Log.d(TAG, "FakeWebViewActivity.matchScreenSize");

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );

        params.gravity = Gravity.CENTER;

        htmlGameView.setLayoutParams(params);
        htmlGameView.requestLayout();
    }

    private void setDefaultContainer() {
        Log.d(TAG, "FakeWebViewActivity.setDefaultContainer");

        mainContainer = new FrameLayout(this);
        mainContainer.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright, getResources().newTheme()));
        setContentView(mainContainer);
    }

    private void openDefoldMainSurfaceActivity() {
        Log.d(TAG, "FakeWebViewActivity.openDefoldMainSurfaceActivity");

        try {
            Intent intent = new Intent(this, Class.forName("com.dynamo.android.DefoldActivity"));
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } catch (Throwable e) {
            e.printStackTrace();

            Log.e(TAG, "FakeWebViewActivity.openDefoldMainSurfaceActivity failed: " + e.getMessage());
        }
    }

    private void openWebViewActivity() {
        Log.d(TAG, "FakeWebViewActivity.openWebViewActivity");

        try {
            Intent intent = new Intent(this, Class.forName("com.blitz.hiddenwebview.FakeWebViewActivity"));
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } catch (Throwable e) {
            e.printStackTrace();

            Log.e(TAG, "FakeWebViewActivity.openWebViewActivity failed: " + e.getMessage());
        }
    }


    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "FakeWebViewActivity.onNewIntent");

        String action = intent.getAction();
        if (action == null) {
            Log.w(TAG, "FakeWebViewActivity.onNewIntent action is null");
            super.onNewIntent(intent);
            return;
        }

        if (action.equals(Intent.ACTION_MAIN)) {
            Log.d(TAG, "FakeWebViewActivity.onNewIntent Intent.ACTION_MAIN");
            openDefoldMainSurfaceActivity();
        } else {
            Log.d(TAG, "FakeWebViewActivity.onNewIntent onNewIntent");

            super.onNewIntent(intent);
        }
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onStart() {

        Log.d(FakeWebViewActivity.TAG, "FakeWebViewActivity.ClickableViewAccessibility.onStart");

        super.onStart();

        // не работает с DefoldActivity
        // НО! работает с NativeActivity

        //(new Handler()).postDelayed(() -> {
        //    try {
        //        Activity topLevelActivity = CurrentActivityAwareApplication.currentlyOpenedActivity;
        //        final View viewGroup = topLevelActivity.getWindow().getDecorView();
        //
        //        viewGroup.setOnTouchListener((View v, MotionEvent event) -> {
        //            if (webViewActive) {
        //                htmlGameView.onTouchEvent(event);
        //            }
        //
        //            return false;
        //        });
        //    } catch (Exception e) {
        //        e.printStackTrace();
        //    }
        //}, ACTIVITY_DETECT_DELAY_MS);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}


interface JavaScriptHandler {
  void handle(String messageType, String payload);
}

class JavaScriptInterface {
  private Activity сontext;
  private JavaScriptHandler handler;

  JavaScriptInterface(Activity activity, JavaScriptHandler handler) {

    Log.d(FakeWebViewActivity.TAG, "JavaScriptInterface");

    сontext = activity;
    this.handler = handler;
  }

  @JavascriptInterface
  public void handle(String messageType, String payload) {

    Log.d(FakeWebViewActivity.TAG, "JavaScriptInterface.handle");

    this.сontext.runOnUiThread(() -> {
      handler.handle(messageType, payload);
    });
  }
}




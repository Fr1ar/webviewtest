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
            FakeWebViewActivity.htmlGameView.dispatchTouchEvent(event);
        }
        return false;
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
            this.webviewJNI = webviewJNI;
            reset(-1);
        }

        public void reset(int scriptId) {
            this.scriptId = scriptId;
        }

        @SuppressLint("DefaultLocale")
        @Override
        public boolean onConsoleMessage(ConsoleMessage msg) {
            if (msg.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
                webviewJNI.onScriptFailed(String.format("js:%d: %s", msg.lineNumber(), msg.message()), scriptId);
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
            this.requestId = scriptId;
            this.hasError = false;
        }

        public CustomWebViewClient(FakeWebViewActivity webviewJNI) {
            super();
            this.webviewJNI = webviewJNI;
            reset();
        }

        public void reset() {
            this.hasError = false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webviewJNI.onPageLoading(url, requestId);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            webviewJNI.onPageFinished(url, requestId);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (!this.hasError) {
                this.hasError = true;
                webviewJNI.onReceivedError(failingUrl, description, requestId);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDefaultContainer();
        openDefoldMainSurfaceActivity();

        initWebView();
        updateFullscreenMode();
    }

    private void updateFullscreenMode() {
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
        if (hasFocus) {
            updateFullscreenMode();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
            updateFullscreenMode();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        htmlGameView = new WebView(this);

        htmlGameView.setBackgroundColor(getResources().getColor(android.R.color.white, getResources().newTheme()));

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
    }

    public static void acceptTouchEvents(int accept) {
        if (touchInterceptorView == null) {
            return;
        }

        touchInterceptorView.isAcceptingTouchEvents = accept == 1;

        WindowManager wm = CurrentActivityAwareApplication.currentlyOpenedActivity.getWindowManager();

        if (accept == 1) {
            wm.removeViewImmediate(touchInterceptorView);
            wm.addView(touchInterceptorView, touchInterceptorViewParams);
        } else {
            wm.removeViewImmediate(touchInterceptorView);
        }
    }

    private static float dpToPx(double dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                (float) dp,
                CurrentActivityAwareApplication.currentlyOpenedActivity.getResources().getDisplayMetrics()
        );
    }

    public static void executeScript(String js, int id) {
        htmlGameView.evaluateJavascript(js, value -> defoldWebViewInterface.onScriptFinished(value, id));
    }

    public static void loadGame(String gamePath, int id) {
        customWebViewClient.reset(id);
        customWebChromeClient.reset(id);

        htmlGameView.loadUrl("http://localhost:8808/" + gamePath);
    }

    public static void loadWebPage(String gamePath, int id) {
        customWebViewClient.reset(id);
        customWebChromeClient.reset(id);

        htmlGameView.loadUrl(gamePath);
    }

    public static void setDebugEnabled(int flag) {
        WebView.setWebContentsDebuggingEnabled(flag == 1);
    }

    public static void changeVisibility(int visible) {
        webViewActive = visible == 1;
        htmlGameView.setVisibility(webViewActive ? View.VISIBLE : View.GONE);

        if (visible == 0) {
            htmlGameView.goBack();

            WindowManager wm = CurrentActivityAwareApplication.currentlyOpenedActivity.getWindowManager();
            wm.removeViewImmediate(touchInterceptorView);
        }
    }

    public static void setTouchInterceptor(double height, double width, double x, double y) {
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
        touchInterceptorView.setBackground(new ColorDrawable(Color.TRANSPARENT));

        touchInterceptorViewParams = new WindowManager.LayoutParams();
        touchInterceptorViewParams.gravity = Gravity.TOP | Gravity.START;
        touchInterceptorViewParams.x = (int) dpToPx(screenWidth * x);
        touchInterceptorViewParams.y = (int) dpToPx(screenHeight * y);
        touchInterceptorViewParams.width = (int) dpToPx(screenWidth * width);
        touchInterceptorViewParams.height = (int) dpToPx(screenHeight * height);
        touchInterceptorViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        touchInterceptorViewParams.alpha = 0.0f;
        touchInterceptorViewParams.format = PixelFormat.TRANSLUCENT;

        WindowManager wm = CurrentActivityAwareApplication.currentlyOpenedActivity.getWindowManager();
        wm.addView(touchInterceptorView, touchInterceptorViewParams);
    }

    public static void setPositionAndSize(double height, double width, double x, double y) {
        Display display = CurrentActivityAwareApplication.currentlyOpenedActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) htmlGameView.getLayoutParams();
        params.height = (int)(screenHeight * height);
        params.width = (int)(screenWidth * width);
        params.gravity = Gravity.TOP | Gravity.START;
        params.setMargins((int)(screenWidth * x), (int)(screenHeight * y), 0, 0);

        htmlGameView.setLayoutParams(params);
        htmlGameView.requestLayout();
    }

    public static void centerWebView() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) htmlGameView.getLayoutParams();
        params.gravity = Gravity.CENTER;
        params.setMargins(0, 0, 0, 0);

        htmlGameView.setLayoutParams(params);
        htmlGameView.requestLayout();
    }

    public static void matchScreenSize() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );

        params.gravity = Gravity.CENTER;

        htmlGameView.setLayoutParams(params);
        htmlGameView.requestLayout();
    }

    private void setDefaultContainer() {
        mainContainer = new FrameLayout(this);
        mainContainer.setBackgroundColor(getResources().getColor(android.R.color.black, getResources().newTheme()));
        setContentView(mainContainer);
    }

    private void openDefoldMainSurfaceActivity() {
        try {
            Intent intent = new Intent(this, Class.forName("com.dynamo.android.DefoldActivity"));
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MAIN)) {
            openDefoldMainSurfaceActivity();
        } else {
            super.onNewIntent(intent);
        }
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onStart() {
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
    сontext = activity;
    this.handler = handler;
  }

  @JavascriptInterface
  public void handle(String messageType, String payload) {
    this.сontext.runOnUiThread(() -> {
      handler.handle(messageType, payload);
    });
  }
}

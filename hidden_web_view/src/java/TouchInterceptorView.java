package com.blitz.hiddenwebview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebView;
import androidx.appcompat.widget.AppCompatTextView;

class TouchInterceptorView extends AppCompatTextView {
    private static final String TAG = WebViewController.TAG;
    private static final String NotificationText =
            "Touch interceptor is visible in debug mode";

    protected WebViewActivity webViewActivity;

    /**
     * Конструкторы по умолчанию
     */
    public TouchInterceptorView(Context context) {
        super(context);
    }
    public TouchInterceptorView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }
    public TouchInterceptorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TouchInterceptorView(Activity defoldActivity, WebViewActivity webViewActivity) {
        super(defoldActivity);

        this.webViewActivity = webViewActivity;

        setTextSize(10);
        setTextColor(Color.BLACK);
        setIncludeFontPadding(true);
        setPadding(20, 20, 20, 20);
    }

    @SuppressLint("SetTextI18n")
    public void setDebugEnabled(boolean isDebugEnabled) {
        setText(isDebugEnabled ? NotificationText : "");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d(TAG, "dispatchTouchEvent: x = " + event.getX() + ", y = " + event.getY());

        if (this.webViewActivity != null) {
            return dispatchModifiedMotionEvent(event);
        }
        return false;
    }

    protected Point getScreenOffset() {
        int[] location = new int[2];
        getLocationOnScreen(location);
        return new Point(location[0], location[1]);
    }

    protected boolean dispatchModifiedMotionEvent(MotionEvent event) {
        WebView htmlGameView = webViewActivity.getHtmlGameView();
        if (htmlGameView == null) {
            return false;
        }
        if (!htmlGameView.isShown()) {
            return false;
        }

        // Этот код должен пересчитывать координаты нажатия с учетом того,
        // что webview находится на экране не в координатах (0, 0)
        // Но почему то это код крашится если приходит несколько нажатий одновременно
        // Чем чинить это, проще будет вычитать смещение уже в самом webview

        /*
        Point offset = getScreenOffset();
        MotionEvent newEvent = MotionEvent.obtain(event.getDownTime(), event.getEventTime(),
                event.getAction(), event.getX() + offset.x,
                event.getY() + offset.y, event.getMetaState());
        boolean result = htmlGameView.dispatchTouchEvent(newEvent);
        newEvent.recycle();
        return result;
        */

        return htmlGameView.dispatchTouchEvent(event);
    }
}

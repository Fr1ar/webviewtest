package com.blitz.hiddenwebview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

class TouchInterceptorView extends View {
    private static final String TAG = WebViewController.TAG;

    protected WebViewActivity webViewActivity;
    protected boolean isAcceptingTouchEvents = true;

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
    }

    public void setAcceptingTouchEvents(boolean isAcceptingTouchEvents) {
        this.isAcceptingTouchEvents = isAcceptingTouchEvents;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d(TAG, "dispatchTouchEvent: x = " + event.getX() + ", y = " + event.getY());

        if (isAcceptingTouchEvents && this.webViewActivity != null) {
            return dispatchModifiedMotionEvent(event);
        }
        return false;
    }

    protected boolean dispatchModifiedMotionEvent(MotionEvent event) {
        WebView htmlGameView = webViewActivity.getHtmlGameView();
        if (htmlGameView == null) {
            return false;
        }
        if (!htmlGameView.isShown()) {
            return false;
        }

        Point offset = getScreenOffset();
        MotionEvent modifiedEvent = MotionEvent.obtain(event.getDownTime(),
                event.getEventTime(), event.getAction(), event.getX() + offset.x,
                event.getY() + offset.y, event.getMetaState());

        Log.d(TAG, "TouchInterceptorView.modifiedEvent: x = " +
                modifiedEvent.getX() + ", y = " + modifiedEvent.getY());

        boolean result = htmlGameView.dispatchTouchEvent(modifiedEvent);
        modifiedEvent.recycle();
        return result;
    }

    protected Point getScreenOffset() {
        int[] location = new int[2];
        getLocationOnScreen(location);
        return new Point(location[0], location[1]);
    }
}

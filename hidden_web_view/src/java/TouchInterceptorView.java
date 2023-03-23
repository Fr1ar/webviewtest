package com.blitz.hiddenwebview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

class TouchInterceptorView extends View {
    private static final String TAG = WebViewController.TAG;

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
        Log.d(TAG, "dispatchTouchEvent: x = " + event.getX() + ", y = " + event.getY());

        if (isAcceptingTouchEvents) {
            return dispatchModifiedMotionEvent(event);
        }
        return false;
    }

    protected boolean dispatchModifiedMotionEvent(MotionEvent event) {
        WebViewActivity webViewActivity = CurrentActivityAwareApplication.instance.getWebViewActivity();
        WebView htmlGameView = webViewActivity.getHtmlGameView();

        if (htmlGameView == null) {
            return false;
        }

        if (!htmlGameView.isShown()) {
            return false;
        }

        int[] location = new int[2];
        getLocationOnScreen(location);
        int offsetX = location[0];
        int offsetY = location[1];

        MotionEvent hackedEvent = MotionEvent.obtain(event.getDownTime(),
                event.getEventTime(), event.getAction(), event.getX() + offsetX,
                event.getY() + offsetY, event.getMetaState());
        
        Log.d(TAG, "TouchInterceptorView.hackedEvent: x = " +
                hackedEvent.getX() + ", y = " + hackedEvent.getY());

        boolean result = htmlGameView.dispatchTouchEvent(hackedEvent);
        hackedEvent.recycle();
        return result;
    }
}

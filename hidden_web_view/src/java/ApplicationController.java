package com.blitz.hiddenwebview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.View;
import android.util.Log;


public class CurrentActivityAwareApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    public static CurrentActivityAwareApplication instance;

    protected WebViewActivity webViewActivity;
    protected Activity defoldActivity;

    private static final String TAG = WebViewController.TAG;

    public static final String DEFOLD_ACTIVITY = "com.dynamo.android.DefoldActivity";
    public static final String WEBVIEW_ACTIVITY = "com.blitz.hiddenwebview.WebViewActivity";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "Application.onCreated");
        instance = this;
        setupActivityListener();
    }

    public WebViewActivity getWebViewActivity() {
        return webViewActivity;
    }

    public Activity getDefoldActivity() {
        return defoldActivity;
    }

    private void setupActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.d(TAG, "Application.onActivityCreated: " + getActivityName(activity));
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.d(TAG, "Application.onActivityStarted: " + getActivityName(activity));
                assignActivity(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.d(TAG, "Application.onActivityResumed: " + getActivityName(activity));
                assignActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.d(TAG, "Application.onActivityPaused: " + getActivityName(activity));
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.d(TAG, "Application.onActivityStopped: " + getActivityName(activity));
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                Log.d(TAG, "Application.onActivitySaveInstanceState: " + getActivityName(activity));
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d(TAG, "Application.onActivityDestroyed: " + getActivityName(activity));
            }

            private void assignActivity(Activity activity) {
                if (getActivityName(activity).equals(WEBVIEW_ACTIVITY)) {
                    webViewActivity = (WebViewActivity) activity;
                } else if (getActivityName(activity).equals(DEFOLD_ACTIVITY)) {
                    defoldActivity = activity;
                }
            }

            private String getActivityName(Activity activity) {
                return activity.getClass().getName();
            }

            private void makeTransparent(Activity activity) {
                String activityName = getActivityName(activity);
                Log.d(TAG, "CurrentActivityAwareApplication.makeTransparent: " + activityName);

                Window window = activity.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                View view = window.getDecorView();
                view.setBackgroundColor(Color.TRANSPARENT);
                activity.findViewById(android.R.id.content).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
            }
        });
    }
}

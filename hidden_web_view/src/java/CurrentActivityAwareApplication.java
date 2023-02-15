package com.blitz.hiddenwebview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;


public class CurrentActivityAwareApplication extends Application {
    // это активити живет все время сколько живет приложение
    // поэтому утечки памяти от хранения в статичной переменной не будет
    @SuppressLint("StaticFieldLeak")
    static Activity currentlyOpenedActivity;

    private static final String TAG = "HiddenWebViewLog";

    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.d(TAG, "CurrentActivityAwareApplication.onCreated");
        setupActivityListener();
    }
    
    private void setupActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                String activityName = getActivityName(activity);
                Log.d(TAG, "CurrentActivityAwareApplication.onActivityCreated: " + activityName);
            }
            
            @Override
            public void onActivityStarted(Activity activity) {
                String activityName = getActivityName(activity);
                Log.d(TAG, "CurrentActivityAwareApplication.onActivityStarted: " + activityName);

                currentlyOpenedActivity = activity;
            }
            
            private String getActivityName(Activity activity) {
                return activity.getClass().getName();
            }

            private void makeTransparent(Activity activity) {
                String activityName = getActivityName(activity);
                Log.d(TAG, "CurrentActivityAwareApplication.makeTransparent: " + activityName);
                
                int color = Color.TRANSPARENT;
                Window window = activity.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(color));
                window.getDecorView().setBackgroundColor(color);

                View view = activity.findViewById(android.R.id.content);
                view.setBackgroundDrawable(new ColorDrawable(color));


                View view2 = activity.findViewById(android.R.id.content);
                view2.setBackgroundColor(color);
                view2.getRootView().setBackgroundColor(color);

                View view3 = window.getDecorView().findViewById(android.R.id.content);
                view3.setBackgroundColor(color);
            }
            
            @Override
            public void onActivityResumed(Activity activity) {
                String activityName = getActivityName(activity);
                Log.d(TAG, "CurrentActivityAwareApplication.onActivityResumed: " + activityName);

                currentlyOpenedActivity = activity;

                if (activityName.contains("Defold")) {
                    makeTransparent(activity);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                String activityName = getActivityName(activity);
                Log.d(TAG, "CurrentActivityAwareApplication.onActivityPaused: " + activityName);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                String activityName = getActivityName(activity);
                Log.d(TAG, "CurrentActivityAwareApplication.onActivityStopped: " + activityName);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                String activityName = getActivityName(activity);
                Log.d(TAG, "CurrentActivityAwareApplication.onActivitySaveInstanceState: " + activityName);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                String activityName = getActivityName(activity);
                Log.d(TAG, "CurrentActivityAwareApplication.onActivityDestroyed: " + activityName);
            }
        });
    }
}

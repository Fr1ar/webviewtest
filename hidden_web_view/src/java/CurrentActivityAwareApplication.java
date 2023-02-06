package com.blitz.hiddenwebview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;

public class CurrentActivityAwareApplication extends Application {
    // это активити живет все время сколько живет приложение
    // поэтому утечки памяти от хранения в статичной переменной не будет
    @SuppressLint("StaticFieldLeak")
    static Activity currentlyOpenedActivity;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("HIDDENWEBVIEW", "CurrentActivityAwareApplication.onCreate");

        setupActivityListener();
    }

    private void setupActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                currentlyOpenedActivity = activity;

                if (activity.getClass().getName().contains("Defold")) {
                    activity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    activity.getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
                    activity.findViewById(android.R.id.content).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                currentlyOpenedActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }
}

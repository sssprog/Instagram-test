package com.sssprog.instagramtest;

import android.content.Context;
import android.os.Build;

public class Config {

    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final boolean LOGS_ENABLED = DEBUG;

    private static AppComponent appComponent;

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static void initAppComponent(Context context, boolean isInTestMode) {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(context, isInTestMode))
                .build();
    }

    public static AppComponent appComponent() {
        return appComponent;
    }

}

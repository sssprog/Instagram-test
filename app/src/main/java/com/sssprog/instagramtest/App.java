package com.sssprog.instagramtest;

import android.app.Application;

import com.sssprog.instagramtest.utils.Prefs;

public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Config.initAppComponent(this);
        Prefs.init(this);
    }

    public static App getInstance() {
        return instance;
    }

}

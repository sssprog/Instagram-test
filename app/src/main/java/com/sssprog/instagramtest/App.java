package com.sssprog.instagramtest;

import android.app.Application;

import com.sssprog.instagramtest.utils.LogHelper;
import com.sssprog.instagramtest.utils.Prefs;

public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        LogHelper.i("-tag-", "app onCreate");
        instance = this;
        Config.initAppComponent(this, false);
        Prefs.init(this);
    }

    public static App getInstance() {
        return instance;
    }

}

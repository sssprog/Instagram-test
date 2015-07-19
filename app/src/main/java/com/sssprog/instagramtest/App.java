package com.sssprog.instagramtest;

import android.app.Application;

import com.sssprog.instagramtest.api.InstagramClient;
import com.sssprog.instagramtest.utils.Prefs;

public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Config.initAppComponent(this);
        Prefs.init(this);
        InstagramClient.getInstance().init(getString(R.string.instagram_client_id),
                getString(R.string.instagram_client_secret), getString(R.string.instagram_callback_url));
    }

    public static App getInstance() {
        return instance;
    }

}

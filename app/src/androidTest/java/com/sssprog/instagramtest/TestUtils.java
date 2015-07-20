package com.sssprog.instagramtest;

import android.content.Context;

import com.sssprog.instagramtest.utils.Prefs;

import java.sql.SQLException;

public class TestUtils {

    public static void setup(Context context) {
        Prefs.init(context);
        Prefs.clear();
        Config.initAppComponent(context, true);
        try {
            Config.appComponent().database().clearAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setDexCachePath(Context context) {
        System.setProperty("dexmaker.dexcache", context.getCacheDir().getPath());
    }

}

package com.sssprog.instagramtest.utils;

import android.util.Log;

import com.sssprog.instagramtest.Config;


public class LogHelper {
	
	public static void i(String tag, String message) {
		if (Config.LOGS_ENABLED) {
            Log.i(tag, message);
        }
	}

	public static void d(String tag, String message) {
		if (Config.LOGS_ENABLED) {
            Log.d(tag, message);
        }
	}

	public static void w(String tag, String message) {
		if (Config.LOGS_ENABLED) {
            Log.w(tag, message);
        }
	}

    public static void w(String tag, String message, Throwable e) {
        if (Config.LOGS_ENABLED) {
            Log.w(tag, message, e);
        }
    }

	public static void e(String tag, String message) {
		if (Config.LOGS_ENABLED) {
            Log.e(tag, message);
        }
	}

    public static String getTag(Class<?> clazz) {
        return "IT::" + clazz.getSimpleName();
    }

    public static void printStackTrace(Throwable e) {
        if (Config.LOGS_ENABLED) {
            e.printStackTrace();
        }
    }

}

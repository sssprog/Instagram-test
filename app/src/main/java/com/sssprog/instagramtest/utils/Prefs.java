package com.sssprog.instagramtest.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.sssprog.instagramtest.R;


public class Prefs {
	
    private static Context context;
	private static SharedPreferences settings = null;
	private static SharedPreferences.Editor editor = null;
	private static Gson gson = new Gson();

	public static void init(Context context) {
        Prefs.context = context;
		settings = PreferenceManager.getDefaultSharedPreferences(context);
		editor = settings.edit();
		setDefaults();
	}

	private static void setDefaults() {
		if (!contains(R.string.pref_current_user_name)) {
			putString(R.string.pref_current_user_name, "self");
		}
		if (!contains(R.string.pref_current_user_id)) {
			putString(R.string.pref_current_user_id, "self");
		}
	}

	public static void putString(int key, String value) {
		editor.putString(getKey(key), value).apply();
	}

	public static String getString(int key) {
		return settings.getString(getKey(key), null);
	}
	
	public static void putInt(int key, Integer value) {
		editor.putInt(getKey(key), value).apply();
	}

	public static int getInt(int key) {
		return settings.getInt(getKey(key), 0);
	}
	
	public static void putBoolean(int key, Boolean value) {
		editor.putBoolean(getKey(key), value).apply();
	}

	public static boolean getBoolean(int key) {
		return settings.getBoolean(getKey(key), false);
	}
	
	public static void putFloat(int key, Float value) {
		editor.putFloat(getKey(key), value).apply();
	}

	public static float getFloat(int key) {
		return settings.getFloat(getKey(key), 0);
	}
	
	public static void putLong(int key, long value) {
		editor.putLong(getKey(key), value).apply();
	}

	public static long getLong(int key) {
		return settings.getLong(getKey(key), 0);
	}
	
	public static boolean contains(int key) {
		return settings.contains(getKey(key));
	}
	
	public static void remove(int key) {
		editor.remove(getKey(key)).apply();
	}

    public static void clear() {
        editor.clear().apply();
    }

    public static String getKey(int key) {
        return context.getString(key);
    }

    public static SharedPreferences getSharedPreferences() {
        return settings;
    }

	public static void putObject(int key, Object object) {
		putString(key, gson.toJson(object));
	}

	public static <T> T getObject(int key, Class<T> cls) {
		return gson.fromJson(getString(key), cls);
	}

	public static String getUserName() {
		return getString(R.string.pref_current_user_name);
	}

	public static String getUserId() {
		return getString(R.string.pref_current_user_id);
	}

}

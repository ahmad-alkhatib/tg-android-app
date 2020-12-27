package app.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import static app.preferences.PreferenceKeys.DEFAULT_PREF_NAME;

public class PreferencesWrapper {

    private final SharedPreferences mPref;

    public PreferencesWrapper(Context context) {
        mPref = context.getSharedPreferences(DEFAULT_PREF_NAME, Context.MODE_PRIVATE);
    }

    boolean getBoolean(String key, boolean defaultValue) {
        return mPref.getBoolean(key, defaultValue);
    }

    void putBoolean(String key, boolean value) {
        mPref.edit().putBoolean(key, value).apply();
    }

    boolean putBoolean(String key, boolean value, boolean commit) {
        if (commit) {
            return mPref.edit().putBoolean(key, value).commit();
        }
        return getBoolean(key, value);
    }

    void putInt(String key, int value) {
        mPref.edit().putInt(key, value).apply();
    }

    int getInt(String key, int defaultValue) {
        return mPref.getInt(key, defaultValue);
    }

    void putString(String key, String value) {
        mPref.edit().putString(key, value).apply();
    }

    String getString(String key, String defaultValue) {
        return mPref.getString(key, defaultValue);
    }

    void putLong(String key, long value) {
        mPref.edit().putLong(key, value).apply();
    }

    long getLong(String key, long defaultValue) {
        return mPref.getLong(key, defaultValue);
    }

    void clear() {
        mPref.getAll().clear();
    }

}

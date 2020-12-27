package app.preferences

import app.preferences.PreferenceKeys.KEY_ID
import app.preferences.PreferenceKeys.KEY_LANGUAGE
import app.preferences.PreferenceKeys.KEY_LOGIN
import app.preferences.PreferenceKeys.KEY_TOKEN


class Preferences(private val preferencesWrapper: PreferencesWrapper) {

    var token: String
        get() = preferencesWrapper.getString(KEY_TOKEN, "")
        set(value) = preferencesWrapper.putString(KEY_TOKEN, value)

    var language: String
        get() = preferencesWrapper.getString(KEY_LANGUAGE, "en")
        set(value) = preferencesWrapper.putString(KEY_LANGUAGE, value)

    var id: String
        get() = preferencesWrapper.getString(KEY_ID, "")
        set(value) = preferencesWrapper.putString(KEY_ID, value)

    var login: Boolean
        get() = preferencesWrapper.getBoolean(KEY_LOGIN, false)
        set(value) {
            preferencesWrapper.putBoolean(KEY_LOGIN, value)
            if (value.not()) {
                id = ""
                token = ""
            }
        }
}
	

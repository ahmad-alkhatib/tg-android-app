package app.utilities

import android.content.Context
import android.provider.Settings

object AppUtils {
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: ""
    }
}
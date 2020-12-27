package com.tolerancegate.ui.splash

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.LocaleList
import java.util.*

class LocaleHelper(base: Context?) : ContextWrapper(base) {
    companion object {
        fun updateLocale(context: Context?, language: String?): ContextWrapper {
            var context = context
            context?.let {
                val localeToSwitchTo = Locale(language)
                val resources = it.resources
                val configuration = resources.configuration
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val localeList = LocaleList(localeToSwitchTo)
                    LocaleList.setDefault(localeList)
                    configuration.setLocales(localeList)
                } else {
                    configuration.locale = localeToSwitchTo
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    context = it.createConfigurationContext(configuration)
                } else {
                    resources.updateConfiguration(configuration, resources.displayMetrics)
                }
            }
            return LocaleHelper(context)
        }
    }
}
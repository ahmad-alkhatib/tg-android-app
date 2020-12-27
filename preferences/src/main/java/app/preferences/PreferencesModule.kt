package app.preferences

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PreferencesModule {

    @Singleton
    @Provides
    internal fun providesPreferences(context: Context): Preferences {
        return Preferences(providesPreferencesWrapper(context))
    }

    @Singleton
    @Provides
    internal fun providesPreferencesWrapper(context: Context): PreferencesWrapper {
        return PreferencesWrapper(context)
    }
}
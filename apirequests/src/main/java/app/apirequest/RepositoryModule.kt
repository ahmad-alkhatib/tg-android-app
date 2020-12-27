package app.apirequest

import android.content.Context
import app.apirequest.network.NetworkUtils
import dagger.Module
import dagger.Provides
import app.preferences.Preferences
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Module
    companion object {
        @Provides
        fun provideBaseRepository(
            context: Context,
            preferences: Preferences
        ): BaseRepository {
            return BaseRepository(
                provideApiClient(
                    context,
                    preferences
                )
            )
        }

        @Provides
        fun provideApiClient(
            context: Context,
            preferences: Preferences
        ): APIClient {
            return APIClient(
                context,
                preferences
            )
        }

        @Singleton
        @Provides
        fun providesNetworkUtils(context: Context): NetworkUtils {
            return NetworkUtils(context)
        }
    }

}
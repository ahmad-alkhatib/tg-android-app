package app.apirequest

import android.content.Context
import app.apirequest.interceptors.APIInterceptor
import app.apirequests.R
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import app.preferences.Preferences
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class APIClient @Inject constructor(
    private val context: Context,
    private val preferences: Preferences
) {

    companion object {
        const val TIMEOUT = 20
    }

    fun setup(): WebService {
        val okHttpBuilder =
            OkHttpClient.Builder().connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)


        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY


        okHttpBuilder.addInterceptor(httpLoggingInterceptor)
        okHttpBuilder.addInterceptor(
            APIInterceptor(
                preferences
            )
        )

        val retrofit = Retrofit.Builder().baseUrl(context.getString(R.string.api_base_url))
            .client(okHttpBuilder.build())
            .addConverterFactory(GsonConverterFactory.create(Gson())).build()
        return retrofit.create(WebService::class.java)
    }

}
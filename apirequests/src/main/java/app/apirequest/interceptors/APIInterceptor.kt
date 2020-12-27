package app.apirequest.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import app.preferences.Preferences

class APIInterceptor constructor(
    private val preferences: Preferences
) : Interceptor {

    companion object {
        const val TOKEN_KEY = "Authorization"
        const val SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        if (preferences.token.isNotEmpty()) {
            requestBuilder.addHeader(TOKEN_KEY, preferences.token)
        }
        return chain.proceed(requestBuilder.build())
    }
}
package app.apirequest.parse

import android.content.Context
import app.apirequests.R
import app.di.scope.ApplicationScoped
import com.parse.Parse
import javax.inject.Inject

@ApplicationScoped
class ParseWrapper @Inject constructor(private val context: Context) {
    fun initialize() {
        // Parse - Integration
        Parse.initialize(
            Parse.Configuration.Builder(context)
                .applicationId(context.getString(R.string.parse_application_id))
                .clientKey(context.getString(R.string.parse_client_key))
                .server(context.getString(R.string.parse_server_path))
                .build()
        )
    }
}
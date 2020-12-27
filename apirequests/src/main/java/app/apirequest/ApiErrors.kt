package app.apirequest

/**
 * Define all error types from server
 */
object ApiErrors {
    const val CODE_OVERLOAD = 500
    const val CODE_MAINTENANCE = 503
    const val CODE_BAD_REQUEST = 400
    const val CODE_NOT_FOUND = 404
    const val CODE_ACCESS_DENIED = 401
    const val CODE_NETWORK = 501
    const val CODE_NETWORK_TIME_OUT = 502

    const val RESPONSE_CODE_LOGIN_FAILED = 210
    const val RESPONSE_CODE_REGISTER_FAILED = 390

    const val CODE_EXCEPTION = 506

    const val KEY_STRING_OVERLOAD = "network_error"
    const val KEY_STRING_MAINTENANCE = "server_maintenance"
    const val KEY_STRING_BAD_REQUEST = "unexpected_error"
    const val KEY_STRING_NOT_FOUND = "not_found"
    const val KEY_STRING_ACCESS_DENIED = "access_denied"
    const val KEY_STRING_NETWORK = "not_found"
    const val KEY_STRING_NETWORK_TIME_OUT = "access_denied"

    const val KEY_STRING_EXCEPTION = "unknown_error"
}

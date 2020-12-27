package app.apirequest


import app.apirequest.ApiErrors.CODE_EXCEPTION
import app.apirequest.ApiErrors.CODE_MAINTENANCE
import app.apirequest.ApiErrors.CODE_NETWORK
import app.apirequest.ApiErrors.CODE_NETWORK_TIME_OUT
import app.apirequest.ApiErrors.CODE_OVERLOAD
import app.apirequest.ApiErrors.KEY_STRING_EXCEPTION
import app.apirequest.ApiErrors.KEY_STRING_MAINTENANCE
import app.apirequest.ApiErrors.KEY_STRING_NETWORK
import app.apirequest.ApiErrors.KEY_STRING_NETWORK_TIME_OUT
import app.apirequest.ApiErrors.KEY_STRING_OVERLOAD
import com.parse.ParseException

class ApiResponse<T>(
    private val status: Status, val data: T?,
    val error: ErrorResponse?
) {

    val isSuccessful: Boolean
        get() = status == Status.SUCCESS

    enum class Status {
        SUCCESS,
        ERROR
    }

    data class ErrorResponse(var message: String, var errorCode: Int)

    data class Error(var errorCode: Int, var source: String, var type: String)

    companion object {

        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(
                Status.SUCCESS,
                data,
                null
            )
        }

        fun error(exception: ParseException): ApiResponse<*> {
            if (exception == null) {
                return technicalError()
            } else if (exception.code == CODE_OVERLOAD) {
                return overloadError()
            } else if (exception.code == CODE_MAINTENANCE) {
                return serverMaintenance()
            }
            return try {
                val errorResponseBody = exception.message
                if (errorResponseBody.isNullOrEmpty().not()) {
                    ApiResponse(Status.ERROR, null, ErrorResponse(errorResponseBody!!, exception.code))
                } else technicalError()
            } catch (e: Exception) {
                e.printStackTrace()
                technicalError()
            }

        }

        fun technicalError(): ApiResponse<*> {
            return error(
                KEY_STRING_EXCEPTION,
                CODE_EXCEPTION
            )
        }

        fun networkError(): ApiResponse<*> {
            return error(
                KEY_STRING_NETWORK,
                CODE_NETWORK
            )
        }

        fun timeoutError(): ApiResponse<*> {
            return error(
                KEY_STRING_NETWORK_TIME_OUT,
                CODE_NETWORK_TIME_OUT
            )
        }

        private fun overloadError(): ApiResponse<*> {
            return error(
                KEY_STRING_OVERLOAD,
                CODE_OVERLOAD
            )
        }

        private fun serverMaintenance(): ApiResponse<*> {
            return error(
                KEY_STRING_MAINTENANCE,
                CODE_MAINTENANCE
            )
        }

        private fun badRequest(): ApiResponse<*> {
            return error(
                KEY_STRING_MAINTENANCE,
                CODE_MAINTENANCE
            )
        }

        private fun error(error: String, errorCode: Int): ApiResponse<*> {
            return ApiResponse(
                Status.ERROR,
                null,
                ErrorResponse(error, errorCode)
            )
        }
    }
}

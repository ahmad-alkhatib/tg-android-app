package app.apirequest.parse

import androidx.lifecycle.MutableLiveData
import app.apirequest.ApiResponse
import app.di.scope.ApplicationScoped
import app.preferences.Preferences
import com.parse.*
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject


@ApplicationScoped
class ParseApiManager @Inject constructor(
    private val parseWrapper: ParseWrapper,
    private val preferences: Preferences,
    private val parseQueryProvider: ParseQueryProvider
) {
    private val userId = ""
    fun initialize() {
        parseWrapper.initialize()
    }

    internal fun processError(t: Throwable): ApiResponse<*> {
        return when (t) {
            is ParseException -> ApiResponse.error(t)
            is UnknownHostException -> ApiResponse.networkError()
            is SocketTimeoutException -> ApiResponse.timeoutError()
            else -> ApiResponse.technicalError()
        }
    }

    fun loginUser(username: String, password: String): MutableLiveData<ApiResponse<ParseUser>> {
        val data = MutableLiveData<ApiResponse<ParseUser>>()
        ParseUser.logInInBackground(username, password) { user, e ->
            if (user != null) {
                // Hooray! The user is logged in.
                data.postValue(ApiResponse.success(user))
            } else {
                // failed. Look at the ParseException to see what happened.
                data.postValue(processError(e) as ApiResponse<ParseUser>)
            }
        }
        return data
    }

    fun registerNewUser(newUser: ParseUser): MutableLiveData<ApiResponse<ParseUser>> {
        val data = MutableLiveData<ApiResponse<ParseUser>>()
        newUser.signUpInBackground {
            if (it == null) {
                data.postValue(ApiResponse.success(ParseUser()))
            } else {
                data.postValue(processError(it) as ApiResponse<ParseUser>)
            }
        }
        return data
    }

    fun updateUser(user: ParseUser): MutableLiveData<ApiResponse<ParseUser>> {
        val data = MutableLiveData<ApiResponse<ParseUser>>()
        user.saveInBackground {
            if (it == null) {
                data.postValue(ApiResponse.success(ParseUser()))
            } else {
                data.postValue(processError(it) as ApiResponse<ParseUser>)
            }
        }
        return data
    }

    fun saveOrUpdateObject(parseObject: ParseObject): MutableLiveData<ApiResponse<ParseObject>> {
        val data = MutableLiveData<ApiResponse<ParseObject>>()
        parseObject.saveInBackground {
            if (it == null) {
                data.postValue(ApiResponse.success(ParseUser()))
            } else {
                data.postValue(processError(it) as ApiResponse<ParseObject>)
            }
        }
        return data
    }

    fun forgotPassword(email: String): MutableLiveData<ApiResponse<ParseUser>> {
        val data = MutableLiveData<ApiResponse<ParseUser>>()
        ParseUser.requestPasswordResetInBackground(email) {
            if (it == null) {
                data.postValue(ApiResponse.success(ParseUser()))
            } else {
                data.postValue(processError(it) as ApiResponse<ParseUser>)
            }
        }
        return data
    }

    fun findObjectsInBG(query: ParseQuery<ParseObject>): MutableLiveData<ApiResponse<List<ParseObject>>> {
        val data = MutableLiveData<ApiResponse<List<ParseObject>>>()
        query.findInBackground { dataList, e ->
            if (e == null) {
                data.postValue(ApiResponse.success(dataList))
            } else {
                data.postValue(processError(e) as ApiResponse<List<ParseObject>>)
            }
        }
        return data
    }

    fun findFirstObjectsInBG(query: ParseQuery<ParseObject>): MutableLiveData<ApiResponse<ParseObject>> {
        val data = MutableLiveData<ApiResponse<ParseObject>>()
        query.getFirstInBackground { obj, e ->
            if (e == null) {
                data.postValue(ApiResponse.success(obj))
            } else {
                data.postValue(processError(e) as ApiResponse<ParseObject>)
            }
        }
        return data
    }

    fun saveParseObjects(parseObjects: MutableList<ParseObject>): MutableLiveData<ApiResponse<ParseUser>> {
        val data = MutableLiveData<ApiResponse<ParseUser>>()
        ParseObject.saveAllInBackground(parseObjects) {
            if (it == null) {
                data.postValue(ApiResponse.success(ParseUser()))
            } else {
                data.postValue(processError(it) as ApiResponse<ParseUser>)
            }
        }
        return data
    }

    fun getHomeScreenData(userID: String, roomName: String): MutableLiveData<ApiResponse<List<ParseObject>>> {
        val data = MutableLiveData<ApiResponse<List<ParseObject>>>()
        val params: HashMap<String, String?> = HashMap()
        params["UserId"] = userID
        params["RoomName"] = roomName
        ParseCloud.callFunctionInBackground("GetHomeScreen", params, FunctionCallback<List<ParseObject>> { result, e ->
            if (e == null) {
                data.postValue(ApiResponse.success(result))
            } else {
                data.postValue(processError(e) as ApiResponse<List<ParseObject>>)
            }

        })

        return data
    }

}
package app.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.apirequest.ApiResponse
import app.apirequest.parse.ParseApiManager
import app.apirequest.parse.ParseQueryProvider
import app.core.listeners.SingleLiveEvent
import app.preferences.Preferences
import com.parse.ParseObject
import com.parse.ParseUser
import java.io.File
import javax.inject.Inject


open class GlobalViewModel(
    private val preferences: Preferences,
    private val parseApiManager: ParseApiManager,
    private val parseQueryProvider: ParseQueryProvider,
    private val globalData: GlobalData
) : ViewModel() {

    private var statusBarHeight = 84
    private var bottomNotchSpace = 0
    val fileSelectionLiveData = SingleLiveEvent<File>()
    val courseListUpdated = SingleLiveEvent<Boolean>()
    val selectedUserId = MutableLiveData<String>()
    val selectedCourseId = MutableLiveData<String>()

    fun findDocumentListForUser(accountType: String): MutableLiveData<ApiResponse<List<ParseObject>>> {
        return parseApiManager.findObjectsInBG(parseQueryProvider.queryForSignUpRequirements(accountType))
    }

    fun saveParseObjects(parseObjects: MutableList<ParseObject>): MutableLiveData<ApiResponse<ParseUser>>{
        return parseApiManager.saveParseObjects(parseObjects)
    }

    @Suppress("UNCHECKED_CAST")
    class GlobalViewModelFactory @Inject
    constructor(
        private val preferences: Preferences,
        private val parseApiManager: ParseApiManager,
        private val parseQueryProvider: ParseQueryProvider,
        private val globalData: GlobalData
    ) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val key = "GlobalViewModel"
            return if (hashMapViewModel.containsKey(key)) {
                getViewModel(key) as T
            } else {
                addViewModel(key, GlobalViewModel(preferences, parseApiManager, parseQueryProvider, globalData))
                getViewModel(key) as T
            }
        }
    }

    fun getAppLanguage() = preferences.language

    fun setAppLanguage(language: String) {
        preferences.language = language
    }

    fun getStatusBarHeight() = statusBarHeight

    fun setStatusBarHeight(statusBarHeight: Int) {
        this.statusBarHeight = statusBarHeight
    }

    fun getBottomNotchSpace() = bottomNotchSpace

    fun setBottomNotchSpace(bottomNotchSpace: Int) {
        this.bottomNotchSpace = bottomNotchSpace
    }

    // Need single instance through all activities
    // https://github.com/android/architecture-components-samples/issues/29
    companion object {
        val hashMapViewModel = HashMap<String, ViewModel>()
        fun addViewModel(key: String, viewModel: ViewModel) {
            hashMapViewModel[key] = viewModel
        }

        fun getViewModel(key: String): ViewModel? {
            return hashMapViewModel[key]
        }
    }
}
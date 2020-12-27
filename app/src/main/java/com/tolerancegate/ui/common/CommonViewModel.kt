package com.tolerancegate.ui.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.apirequest.ApiResponse
import app.apirequest.parse.ParseApiManager
import app.apirequest.parse.ParseQueryProvider
import app.preferences.Preferences
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import javax.inject.Inject

class CommonViewModel(
    private val preferences: Preferences,
    private val parseApiManager: ParseApiManager,
    private val parseQueryProvider: ParseQueryProvider
) : ViewModel() {

    fun registerNewUser(newUser: ParseUser): MutableLiveData<ApiResponse<ParseUser>> {
        return parseApiManager.registerNewUser(newUser)
    }

    fun updateUser(user: ParseUser): MutableLiveData<ApiResponse<ParseUser>> {
        return parseApiManager.updateUser(user)
    }

    fun forgotPassword(email: String): MutableLiveData<ApiResponse<ParseUser>> {
        return parseApiManager.forgotPassword(email)
    }

    fun findAllInstructors(query: ParseQuery<ParseObject>): MutableLiveData<ApiResponse<List<ParseObject>>> {
        return parseApiManager.findObjectsInBG(parseQueryProvider.queryForAllInstructors(query))
    }

    fun findUserBookmarks(objectIds: List<String>): MutableLiveData<ApiResponse<List<ParseObject>>> {
        return parseApiManager.findObjectsInBG(parseQueryProvider.queryForGetUserBookmarks(objectIds))
    }

    fun findUserConversation(user: ParseUser): MutableLiveData<ApiResponse<List<ParseObject>>> {
        return parseApiManager.findObjectsInBG(parseQueryProvider.queryForGetUserConversation(user))
    }

    fun getUserPTDetails(userId: String): MutableLiveData<ApiResponse<ParseObject>> {
        return parseApiManager.findFirstObjectsInBG(parseQueryProvider.queryForGetUserPTDetails(userId))
    }

    fun getCourseDetails(courseId: String): MutableLiveData<ApiResponse<ParseObject>> {
        return parseApiManager.findFirstObjectsInBG(parseQueryProvider.queryForGetCourseDetails(courseId))
    }

    fun getCourseDetailsForUpdate(courseId: String): MutableLiveData<ApiResponse<ParseObject>> {
        return parseApiManager.findFirstObjectsInBG(parseQueryProvider.queryForGetCourseDetailsForUpdate(courseId))
    }

    fun saveOrUpdateObject(parseObject: ParseObject): MutableLiveData<ApiResponse<ParseObject>> {
        return parseApiManager.saveOrUpdateObject(parseObject)
    }

    fun findCoursesList(query: ParseQuery<ParseObject>): MutableLiveData<ApiResponse<List<ParseObject>>> {
        return parseApiManager.findObjectsInBG(parseQueryProvider.queryForGetCourses(query))
    }

    @Suppress("UNCHECKED_CAST")
    class CommonViewModelFactory @Inject
    constructor(
        private val preferences: Preferences,
        private val parseApiManager: ParseApiManager,
        private val parseQueryProvider: ParseQueryProvider
    ) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val key = CommonViewModel::class.java.simpleName
            return if (hashMapViewModel.containsKey(key)) {
                getViewModel(key) as T
            } else {
                addViewModel(key, CommonViewModel(preferences, parseApiManager, parseQueryProvider))
                getViewModel(key) as T
            }
        }
    }

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
package com.tolerancegate.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.apirequest.ApiResponse
import app.apirequest.parse.ParseApiManager
import app.apirequest.parse.ParseQueryProvider
import app.preferences.Preferences
import com.parse.ParseObject
import com.parse.ParseQuery
import javax.inject.Inject

class HomeViewModel(
    private val preferences: Preferences,
    private val parseApiManager: ParseApiManager,
    private val parseQueryProvider: ParseQueryProvider
) : ViewModel() {

    fun getHomeData(userID: String, roomName: String): MutableLiveData<ApiResponse<List<ParseObject>>> {
        return parseApiManager.getHomeScreenData(userID, roomName)
    }

    fun findPrivateTutoringObjects(objectIds: List<String>): MutableLiveData<ApiResponse<List<ParseObject>>> {
        return parseApiManager.findObjectsInBG(parseQueryProvider.queryForPTObjectDetails(objectIds))
    }

    fun findUserBookmarks(objectIds: List<String>): MutableLiveData<ApiResponse<List<ParseObject>>> {
        return parseApiManager.findObjectsInBG(parseQueryProvider.queryForGetUserBookmarks(objectIds, true))
    }

    fun findUserFollowing(objectIds: List<String>): MutableLiveData<ApiResponse<List<ParseObject>>> {
        return parseApiManager.findObjectsInBG(parseQueryProvider.queryForGetUserFollowing(objectIds))
    }

    fun findUserFollowers(objectId: String): MutableLiveData<ApiResponse<List<ParseObject>>> {
        return parseApiManager.findObjectsInBG(parseQueryProvider.queryForGetUserFollowers(objectId))
    }

    fun findCoursesList(query : ParseQuery<ParseObject>): MutableLiveData<ApiResponse<List<ParseObject>>> {
        return parseApiManager.findObjectsInBG(parseQueryProvider.queryForGetCourses(query))
    }

    fun findAllInstitutions(query : ParseQuery<ParseObject>): MutableLiveData<ApiResponse<List<ParseObject>>> {
        return parseApiManager.findObjectsInBG(parseQueryProvider.queryForAllInstitutions(query))
    }

    fun findInstitutionType(): MutableLiveData<ApiResponse<List<ParseObject>>> {
        return parseApiManager.findObjectsInBG(parseQueryProvider.queryForInstitutionType())
    }

    fun getCurrentUserObj(userId: String): MutableLiveData<ApiResponse<ParseObject>> {
        return parseApiManager.findFirstObjectsInBG(parseQueryProvider.queryForGetUserDetails(userId))
    }

    @Suppress("UNCHECKED_CAST")
    class HomeViewModelFactory @Inject
    constructor(
        private val preferences: Preferences,
        private val parseApiManager: ParseApiManager,
        private val parseQueryProvider: ParseQueryProvider
    ) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val key = HomeViewModel::class.java.simpleName
            return if (hashMapViewModel.containsKey(key)) {
                getViewModel(key) as T
            } else {
                addViewModel(key, HomeViewModel(preferences, parseApiManager, parseQueryProvider))
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
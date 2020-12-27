package com.tolerancegate.ui.authentication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.apirequest.ApiResponse
import app.apirequest.parse.ParseApiManager
import app.apirequest.parse.ParseQueryProvider
import app.core.listeners.SingleLiveEvent
import app.preferences.Preferences
import app.utilities.Enums.*
import com.parse.ParseObject
import com.parse.ParseUser
import javax.inject.Inject

class AuthViewModel(
    private val preferences: Preferences,
    private val parseApiManager: ParseApiManager,
    private val parseQueryProvider: ParseQueryProvider
) : ViewModel() {

    private var registrationType: SingleLiveEvent<String> = SingleLiveEvent()
    private var registrationForPT: Boolean = false

    fun getRegistrationType() = registrationType

    fun registrationForPT() = registrationForPT

    fun setRegistrationForPT(status: Boolean) {
        registrationForPT = status
    }

    fun loginUser(username: String, password: String): MutableLiveData<ApiResponse<ParseUser>> {
        return parseApiManager.loginUser(username, password)
    }

    fun registerNewUser(newUser: ParseUser): MutableLiveData<ApiResponse<ParseUser>> {
        return parseApiManager.registerNewUser(newUser)
    }

    fun updateUser(user: ParseUser): MutableLiveData<ApiResponse<ParseUser>> {
        return parseApiManager.updateUser(user)
    }

    fun forgotPassword(email: String): MutableLiveData<ApiResponse<ParseUser>> {
        return parseApiManager.forgotPassword(email)
    }

    fun getCurrentUserObj(userId: String): MutableLiveData<ApiResponse<ParseObject>> {
        return parseApiManager.findFirstObjectsInBG(parseQueryProvider.queryForGetCurrentUser(userId))
    }

    @Suppress("UNCHECKED_CAST")
    class AuthenticationViewModelFactory @Inject
    constructor(private val preferences: Preferences,
                private val parseApiManager: ParseApiManager,
                private val parseQueryProvider: ParseQueryProvider) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val key = AuthViewModel::class.java.simpleName
            return if (hashMapViewModel.containsKey(key)) {
                getViewModel(key) as T
            } else {
                addViewModel(key, AuthViewModel(preferences, parseApiManager, parseQueryProvider))
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
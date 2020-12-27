package app.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.core.base.BaseViewModel
import app.core.listeners.SingleLiveEvent
import app.utilities.Enums
import app.utilities.Enums.CommonRootScreen
import app.utilities.Enums.InstructorSearchType
import javax.inject.Inject

class NavigationViewModel @Inject constructor() : BaseViewModel() {

    val titleFlatLiveData = SingleLiveEvent<Boolean>()
    val toolbarBackBtn = SingleLiveEvent<Boolean>()
    val showBottomBar = SingleLiveEvent<Boolean>()
    val screenTitle = SingleLiveEvent<String>()
    val commonRootScreen = SingleLiveEvent<CommonRootScreen>()
    val instructorSearchType = SingleLiveEvent<InstructorSearchType>()
    val userUpdated = SingleLiveEvent<Boolean>()

    fun setTitleFlat(isTitleFlat: Boolean) {
        titleFlatLiveData.postValue(isTitleFlat)
    }

    fun setScreenTitle(title: String) {
        screenTitle.postValue(title)
    }

    fun removeScreenTitle() {
        screenTitle.postValue("")
    }

    fun showBackArrow() {
        toolbarBackBtn.postValue(true)
    }

    fun hideBackArrow() {
        toolbarBackBtn.postValue(false)
    }

    fun showBottomBar() {
        showBottomBar.postValue(true)
    }

    fun hideBottomBar() {
        showBottomBar.postValue(false)
    }

    @Suppress("UNCHECKED_CAST")
    class NavigationViewModelFactory @Inject
    constructor() :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val key = NavigationViewModel::class.java.simpleName
            return if (hashMapViewModel.containsKey(key)) {
                getViewModel(key) as T
            } else {
                addViewModel(key, NavigationViewModel())
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
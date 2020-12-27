package com.tolerancegate.ui.home

import android.os.Bundle
import app.core.base.BaseFragment
import app.core.custom.CustomEditText
import app.uicomponents.extensions.show
import app.utilities.Enums
import app.utilities.Enums.CommonRootScreen
import com.tolerancegate.ui.common.CommonActivity
import com.tolerancegate.ui.common.DialogItemPicker

open class BaseHomeFragment : BaseFragment() {

    lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = (activity as HomeActivity).homeViewModel
    }

    fun startCommonActivity(rootScreen: Enums.CommonRootScreen) {
        activity?.let {
            navigationViewModel.commonRootScreen.postValue(rootScreen)
            CommonActivity.start(it)
        }
    }

    fun switchTab(tabIndex: Int) {
        if(activity is HomeActivity){
            (activity as HomeActivity).switchTab(tabIndex)
        }
    }
}
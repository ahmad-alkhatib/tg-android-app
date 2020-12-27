package com.tolerancegate.ui.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import app.core.base.BaseFragment
import app.core.navigation.NavigationActivity
import app.utilities.Enums.CommonRootScreen.*
import com.tolerancegate.R
import com.tolerancegate.ui.authentication.ChangePasswordFragment
import com.tolerancegate.ui.home.InstructorsFragment
import com.tolerancegate.ui.profile.*
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject


class CommonActivity : NavigationActivity() {

    lateinit var commonViewModel: CommonViewModel

    @Inject
    lateinit var factory: CommonViewModel.CommonViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)
        commonViewModel = ViewModelProviders.of(this, factory)[CommonViewModel::class.java]
    }


    override val numberOfRootFragments: Int
        get() = 1

    override fun getRootFragment(index: Int): BaseFragment {
        return when (navigationViewModel.commonRootScreen.value) {
            SETTING -> AppSettingFragment.newInstance()
            CHANGE_PASSWORD -> ChangePasswordFragment.newInstance()
            INSTRUCTOR_LIST -> InstructorsFragment.newInstance(true)
            USER_BOOKMARK -> BookmarkFragment.newInstance()
            USER_CONVERSATION -> ConversationListFragment.newInstance()
            USER_PT_DETAILS -> UserPTDetailsFragment.newInstance()
            USER_DETAILS -> UserDetailFragment.newInstance()
            MY_COURSES -> MyCoursesFragment.newInstance()
            else -> BaseFragment()
        }
    }

    companion object {
        @JvmStatic
        fun start(activity: Activity) {
            activity.startActivity(Intent(activity, CommonActivity::class.java))
        }
    }

}
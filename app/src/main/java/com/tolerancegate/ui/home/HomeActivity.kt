package com.tolerancegate.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import app.core.base.BaseFragment
import app.core.navigation.NavigationActivity
import app.utilities.Enums.InstructorSearchType.HOME
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavController.Companion.TAB1
import com.ncapdevi.fragnav.FragNavController.Companion.TAB2
import com.ncapdevi.fragnav.FragNavController.Companion.TAB3
import com.ncapdevi.fragnav.FragNavController.Companion.TAB4
import com.ncapdevi.fragnav.FragNavController.Companion.TAB5
import com.tolerancegate.R
import com.tolerancegate.ui.authentication.AuthenticationActivity
import com.tolerancegate.ui.common.DialogAddDocument
import com.tolerancegate.ui.profile.MyProfileFragment
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject

class HomeActivity : NavigationActivity() {

    lateinit var homeViewModel: HomeViewModel

    @Inject
    lateinit var factory: HomeViewModel.HomeViewModelFactory
    private val fragHome = HomeFragment.newInstance()
    private val fragInstitution = InstitutionsFragment.newInstance()
    private val fragInstructor = InstructorsFragment.newInstance()
    private val fragCourses = CoursesFragment.newInstance()
    private val fragMyProfile = MyProfileFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        homeViewModel = ViewModelProviders.of(this, factory)[HomeViewModel::class.java]

        initTabChangeListener()
        headerView.addActionForLeftIcon {
            onBackPressed()
        }

        if (intent.getBooleanExtra(SHOW_DOCUMENT_DIALOG, false)) {
            showDocumentDialog()
        }

        if (intent.getBooleanExtra(START_AUTH_ON_CREATE, false)) {
            openAuthenticationActivity()
        }

        if (userViewModel.hasCurrentUser() && userViewModel.isSocialSignUpNotCompleted()) {
            openAuthenticationActivity()
        }
    }

    private fun openAuthenticationActivity() {
        AuthenticationActivity.start(this)
    }

    private fun onProfileItemClick() {
        if (userViewModel.hasCurrentUser()) {
            fragNavController.switchTab(FragNavController.TAB5)
        } else {
            openAuthenticationActivity()
        }
    }

    fun switchTab(tabIndex: Int) {
        when (tabIndex) {
            TAB2 -> navigationView.selectedItemId = R.id.menu_institutions
            TAB3 -> navigationView.selectedItemId = R.id.menu_instructors
            TAB4 -> navigationView.selectedItemId = R.id.menu_courses
        }
    }

    private fun showDocumentDialog() {
        var accountVerificationDialogFragment = DialogAddDocument.instance()
        accountVerificationDialogFragment.show(supportFragmentManager, "DOCUMENT_DIALOG")
    }

    override val numberOfRootFragments: Int
        get() = 5

    override fun getRootFragment(index: Int): BaseFragment {
        when (index) {
            TAB1 -> return fragHome
            TAB2 -> return fragInstitution
            TAB3 -> return fragInstructor
            TAB4 -> return fragCourses
            TAB5 -> return fragMyProfile
        }
        throw IllegalStateException("Fragment not found")
    }

    private fun initTabChangeListener() {
        navigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> fragNavController.switchTab(FragNavController.TAB1)
                R.id.menu_institutions -> fragNavController.switchTab(TAB2)
                R.id.menu_instructors -> {
                    navigationViewModel.instructorSearchType.postValue(HOME)
                    fragNavController.switchTab(TAB3)
                }
                R.id.menu_courses -> fragNavController.switchTab(TAB4)
                R.id.menu_profile -> onProfileItemClick()
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        if (navigationView != null && userViewModel.hasCurrentUser().not()) {
            //TODO ARK -> Need to to check this logic when show document screen on start up
            navigationView.selectedItemId = R.id.menu_home
        }
    }

    override fun onBackPressed() {
        if (fragNavController.currentStackIndex != FragNavController.TAB1) {
            navigationView.selectedItemId = R.id.menu_home
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        @JvmStatic
        val SHOW_DOCUMENT_DIALOG = "showDocumentDialog"
        val START_AUTH_ON_CREATE = "startAuthenticationOnCreate"
        fun start(activity: Activity, showDocumentDialog: Boolean = false, startAuth: Boolean = false) {
            val intent = Intent(activity, HomeActivity::class.java)
            intent.putExtra(SHOW_DOCUMENT_DIALOG, showDocumentDialog)
            intent.putExtra(START_AUTH_ON_CREATE, startAuth)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
        }
    }
}
package app.core.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import app.core.R
import app.core.base.BaseActivity
import app.uicomponents.dialogs.DialogProvider
import app.core.base.BaseFragment
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavTransactionOptions
import javax.inject.Inject

open class NavigationActivity : BaseActivity(), FragNavController.RootFragmentListener {

    @Inject
    lateinit var dialogProviders: DialogProvider

    val fragNavController: FragNavController = FragNavController(supportFragmentManager, R.id.contentView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_activity)

        fragNavController.apply {
            defaultTransactionOptions = getDefaultTransactions()
            rootFragmentListener = this@NavigationActivity
            initialize(FragNavController.TAB1, savedInstanceState)
        }
    }

    override val numberOfRootFragments: Int get() = 1

    override fun getRootFragment(index: Int) = BaseFragment()

    open fun getDefaultTransactions(): FragNavTransactionOptions {
        return FragNavTransactionOptions.newBuilder().customAnimations(
            R.anim.anim_enter_slide_in,
            R.anim.anim_enter_slide_out,
            R.anim.anim_exit_slide_in,
            R.anim.anim_exit_slide_out
        ).build()
    }

    private fun pushFragment(fragment: Fragment, transactionOptions: FragNavTransactionOptions) {
        if (isLifeCycleAtLeastCreated().not()) {
            return
        }
        fragNavController.pushFragment(fragment, transactionOptions)
    }

    fun pushFragment(fragment: Fragment) {
        pushFragment(fragment, getDefaultTransactions())
    }

    private fun popFragment(transactionOptions: FragNavTransactionOptions) {
        if (isLifeCycleAtLeastCreated().not()) {
            return
        }
        if (fragNavController.isRootFragment)
            finish()
        else
            fragNavController.popFragment(transactionOptions)
    }

    fun popFragment() {
        popFragment(getDefaultTransactions())
    }

    fun popBaseFragment(transactionOptions: FragNavTransactionOptions) {
        if (isLifeCycleAtLeastCreated().not()) {
            return
        }
        val size = fragNavController.currentStack!!.size
        if (size == 0) {
            return
        }
        fragNavController.popFragments(size, transactionOptions)
    }

    fun popUntilBase() {
        if (isLifeCycleAtLeastCreated().not()) {
            return
        }
        val size = fragNavController.currentStack!!.size
        if (size == 0) {
            return
        }
        fragNavController.popFragments(size - 1, getDefaultTransactions())
    }

    fun popFragment(depth: Int, transactionOptions: FragNavTransactionOptions) {
        if (isLifeCycleAtLeastCreated().not()) {
            return
        }
        val size = fragNavController.currentStack!!.size
        if (size < depth) {
            return
        }
        if (size == depth) {
            finish()
            return
        }
        fragNavController.popFragments(depth, transactionOptions)
    }

    fun replaceFragment(fragment: Fragment) {
        replaceFragment(fragment, getDefaultTransactions())
    }

    private fun replaceFragment(fragment: Fragment, fragNavTransactionOptions: FragNavTransactionOptions) {
        if (isLifeCycleAtLeastCreated().not()) {
            return
        }
        fragNavController.replaceFragment(fragment, fragNavTransactionOptions)
    }

    private fun isLifeCycleAtLeastCreated(): Boolean = lifecycle.currentState.isAtLeast(
        Lifecycle.State.CREATED
    )

    override fun onBackPressed() {
        if (fragNavController.isRootFragment)
            super.onBackPressed()
        else
            popFragment(getDefaultTransactions())
    }
}
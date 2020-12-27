package app.core

import app.core.base.BaseActivity
import app.core.base.BaseDialogFragment
import app.core.base.BaseFragment
import app.core.navigation.NavigationActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface CoreModule {

    @ContributesAndroidInjector
    fun contributeBaseActivityInjector(): BaseActivity

    @ContributesAndroidInjector
    fun contributeNavigationActivityInjector(): NavigationActivity

    @ContributesAndroidInjector
    fun contributeBaseFragmentInjector(): BaseFragment

    @ContributesAndroidInjector
    fun contributeBaseDialogFragmentInjector(): BaseDialogFragment

    @ContributesAndroidInjector
    fun contributeProgressDialogFragmentInjector(): ProgressDialogFragment
}
package com.tolerancegate.ui.home

import com.tolerancegate.ui.profile.MyProfileFragment
import com.tolerancegate.ui.profile.UserDetailFragment
import com.tolerancegate.ui.profile.UserPTDetailsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface HomeModule {

    @ContributesAndroidInjector
    fun contributeHomeActivityInjector(): HomeActivity

    @ContributesAndroidInjector
    fun contributeBaseHomeFragmentInjector(): BaseHomeFragment

    @ContributesAndroidInjector
    fun contributeHomeFragmentInjector(): HomeFragment

    @ContributesAndroidInjector
    fun contributeInstitutionsFragmentInjector(): InstitutionsFragment

    @ContributesAndroidInjector
    fun contributeInstructorsFragmentInjector(): InstructorsFragment

    @ContributesAndroidInjector
    fun contributeCoursesFragmentInjector(): CoursesFragment

    @ContributesAndroidInjector
    fun contributeMyProfileFragmentInjector(): MyProfileFragment

    @ContributesAndroidInjector
    fun contributeUserPTDetailsFragmentInjector(): UserPTDetailsFragment

    @ContributesAndroidInjector
    fun contributeUserDetailFragmentInjector(): UserDetailFragment
}
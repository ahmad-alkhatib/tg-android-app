package com.tolerancegate.ui.common

import com.tolerancegate.ui.profile.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface CommonModule {

    @ContributesAndroidInjector
    fun contributeCommonActivityInjector(): CommonActivity

    @ContributesAndroidInjector
    fun contributeAppSettingFragmentInjector(): AppSettingFragment

    @ContributesAndroidInjector
    fun contributePickerDialogFragmentInjector(): DialogItemPicker

    @ContributesAndroidInjector
    fun contributeAccountVerificationDialogFragmentInjector(): DialogAddDocument

    @ContributesAndroidInjector
    fun contributeSelectAndCaptureImageActivityInjector(): FileSelectionActivity

    @ContributesAndroidInjector
    fun contributeDialogListFiltersInjector(): DialogListFilters

    @ContributesAndroidInjector
    fun contributeBookmarkFragmentInjector(): BookmarkFragment

    @ContributesAndroidInjector
    fun contributeConversationListFragmentInjector(): ConversationListFragment

    @ContributesAndroidInjector
    fun contributeDialogUserPermissionInjector(): DialogUserPermission

    @ContributesAndroidInjector
    fun contributeMyCoursesFragmentInjector(): MyCoursesFragment

    @ContributesAndroidInjector
    fun contributeCourseInfoFragmentInjector(): CourseInfoFragment

    @ContributesAndroidInjector
    fun contributeDialogWebPageFragmentInjector(): DialogWebPageFragment

    @ContributesAndroidInjector
    fun contributeAddEditCourseFragmentInjector(): AddEditCourseFragment
}
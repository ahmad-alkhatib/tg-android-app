package com.tolerancegate.ui.authentication

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface AuthModule {

    @ContributesAndroidInjector
    fun contributeAuthenticationActivityInjector(): AuthenticationActivity

    @ContributesAndroidInjector
    fun contributeLoginFragmentInjector(): LoginFragment

    @ContributesAndroidInjector
    fun contributeBaseAuthFragmentInjector(): BaseAuthFragment

    @ContributesAndroidInjector
    fun contributeRegisterFragmentInjector(): RegisterFragment

    @ContributesAndroidInjector
    fun contributeForgotPasswordFragmentInjector(): ForgotPasswordFragment

    @ContributesAndroidInjector
    fun contributeChangePasswordFragmentInjector(): ChangePasswordFragment
}
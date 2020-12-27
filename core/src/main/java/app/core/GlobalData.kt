package app.core

import app.daos.profile.UserProfile
import app.di.scope.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class GlobalData @Inject constructor() {

    private var user: UserProfile?

    fun getUserProfile(): UserProfile? {
        return user
    }

    fun setUserProfile(user: UserProfile?) {
        this.user = user
    }

    fun clearUser() {
        user = UserProfile()
    }

    init {
        user = UserProfile()
    }
}
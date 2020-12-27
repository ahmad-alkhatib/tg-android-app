package app.core

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.apirequest.parse.Property.ACCOUNT_TYPE
import app.apirequest.parse.Property.FULL_NAME
import app.apirequest.parse.Property.HAS_PRIVATE_TUTORING
import app.apirequest.parse.Property.IMAGE
import app.apirequest.parse.Property.IS_SIGN_UP_COMPLETED
import app.apirequest.parse.PropertyValue.FACEBOOK
import app.apirequest.parse.PropertyValue.UPLOADED
import app.uicomponents.extensions.getBool
import app.uicomponents.extensions.getObjectIds
import app.uicomponents.extensions.getStr
import com.parse.ParseFile
import com.parse.ParseUser
import javax.inject.Inject


open class UserViewModel : ViewModel() {

    fun getCurrentUser() = ParseUser.getCurrentUser()

    fun getUserId() = getCurrentUser().objectId

    fun hasCurrentUser() = ParseUser.getCurrentUser() != null

    fun getAccountType() = getCurrentUser().getStr(ACCOUNT_TYPE)

    fun getFullName() = getCurrentUser().getStr(FULL_NAME)

    fun getEmail() = getCurrentUser().email

    fun getImageUrl() = (getCurrentUser().get(IMAGE) as? ParseFile)?.url ?: ""


    fun setStatusToUploaded() {
        getCurrentUser().put("approvalStatus", UPLOADED)
        getCurrentUser().save()
    }

    fun isSocialSignUpNotCompleted(): Boolean {
        val user = getCurrentUser()
        return (user.getBool("isSocialLogin") && user.getBool(IS_SIGN_UP_COMPLETED).not())
    }

    fun isSignUpCompleted(): Boolean {
        val user = getCurrentUser()
        return user.getBool(IS_SIGN_UP_COMPLETED)
    }

    fun shouldChargePT(): Boolean {
        // TODO ARK - at the place of true condition should be check from remoteConfig
        return false
    }

    fun shouldChargeInstructor(): Boolean {
        // TODO ARK - at the place of true condition should be check from remoteConfig
        return true
    }

    fun isActive() = getCurrentUser().getBool("isActive")
    fun isApproved() = getCurrentUser().getBool("isApproved")
    fun isMembershipActive() = getCurrentUser().getBool("isMembershipActive")
    fun isPrivateTutoringActive() = getCurrentUser().getBool("isPrivateTutoringActive")

    fun logout() {
        ParseUser.logOut()
    }

    fun getFollowingIds(): List<String> {
        return getCurrentUser().getObjectIds("followings")
    }

    fun setSocialLogin() {
        getCurrentUser().put("socialLoginType", FACEBOOK)
        getCurrentUser().put("isSocialLogin", true)
        getCurrentUser().save()
    }

    fun isPrivateTutoringEnable(): Boolean {
        return isPrivateTutoringActive() || getCurrentUser().getBool(HAS_PRIVATE_TUTORING)
    }

    fun isContainsInList(userId: String, key: String): Boolean {
        return if (getCurrentUser() != null) {
            getCurrentUser().getObjectIds(key).contains(userId)
        } else {
            false
        }
    }

    fun addInList(userId: String, key: String): MutableLiveData<Boolean> {
        val data = MutableLiveData<Boolean>()
        getCurrentUser().addUnique(key, userId)
        getCurrentUser().saveInBackground {
            data.postValue(it == null)
        }
        return data
    }

    fun removeFromList(userId: String, key: String): MutableLiveData<Boolean> {
        val data = MutableLiveData<Boolean>()
        getCurrentUser().removeAll(key, listOf(userId))
        getCurrentUser().saveInBackground {
            data.postValue(it == null)
        }
        return data
    }

    @Suppress("UNCHECKED_CAST")
    class UserViewModelFactory @Inject
    constructor() :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val key = "GlobalViewModel"
            return if (hashMapViewModel.containsKey(key)) {
                getViewModel(key) as T
            } else {
                addViewModel(key, UserViewModel())
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
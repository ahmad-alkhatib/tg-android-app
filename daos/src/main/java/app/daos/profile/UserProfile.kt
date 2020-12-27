package app.daos.profile

data class UserProfile(
    var birthday: String? = null,
    var email: String? = null,
    var emailVerified: String? = null,
    var gender: String? = null,
    var zip: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var joined: String? = null,
    var smsPhoneNumber: String? = null,
    var id: String? = null,
    var deviceVerified: String? = null
)
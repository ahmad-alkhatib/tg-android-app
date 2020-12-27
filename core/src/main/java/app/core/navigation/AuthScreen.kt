package app.core.navigation

enum class AuthScreen constructor(val menuName: Int) {
    LOGIN(1),
    REGISTER(2),
    FORGOT_PASSWORD(3)
}
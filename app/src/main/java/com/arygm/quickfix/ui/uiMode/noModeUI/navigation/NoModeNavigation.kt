package com.arygm.quickfix.ui.noModeUI.navigation

import com.arygm.quickfix.ui.navigation.TopLevelDestination


object NoModeRoute {
    const val WELCOME = "Welcome"
    const val REGISTER = "Register"
    const val INFO = "Info"
    const val LOGIN = "Login"
    const val PASSWORD = "Password"
}

object NoModeScreen {
    const val WELCOME = "Welcome Screen"
    const val LOGIN = "Login Screen"
    const val INFO = "Info Screen"
    const val PASSWORD = "Password Screen"
    const val REGISTER = "Register Screen"
    const val RESET_PASSWORD = "Reset password Screen"
    const val GOOGLE_INFO = "Google Info Screen"
}

object SharedTopLevelDestinations {
    val WELCOME = TopLevelDestination(route = NoModeRoute.WELCOME, icon = null, textId = "Welcome")
}
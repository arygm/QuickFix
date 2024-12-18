package com.arygm.quickfix.ui.uiMode.noModeUI.navigation

import com.arygm.quickfix.ui.navigation.TopLevelDestination

object NoModeRoute {
  const val WELCOME = "Welcome"
  const val REGISTER = "Register"
  const val LOGIN = "Login"
  const val PASSWORD = "Password"
  const val RESET_PASSWORD = "Reset password"
  const val GOOGLE_INFO = "Google Info"
}

object NoModeScreen {
  const val WELCOME = "Welcome Screen"
  const val LOGIN = "Login Screen"
  const val PASSWORD = "Password Screen"
}

object SharedTopLevelDestinations {
  val WELCOME = TopLevelDestination(route = NoModeRoute.WELCOME, icon = null, textId = "Welcome")
}

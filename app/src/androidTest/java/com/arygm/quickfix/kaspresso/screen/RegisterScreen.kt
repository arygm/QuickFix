package com.arygm.quickfix.kaspresso.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class RegisterScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<RegisterScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("InfoBox") }) {
  val firstName: KNode = child {
    hasTestTag("firstNameInput")
    useUnmergedTree = true
  }
  val birthdate: KNode = child {
    hasTestTag("brithDateText")
    useUnmergedTree = true
  }
}

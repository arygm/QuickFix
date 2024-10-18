package com.arygm.quickfix.kaspresso.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.arygm.quickfix.ressources.C
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class WelcomeScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<WelcomeScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("welcomeBox") }) {
    val registerButton: KNode = child {
        hasTestTag("RegistrationButton")
        useUnmergedTree = true
    }
}
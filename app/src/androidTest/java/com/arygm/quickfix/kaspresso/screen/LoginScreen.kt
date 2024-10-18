package com.arygm.quickfix.kaspresso.screen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.arygm.quickfix.ressources.C
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class LoginScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<LoginScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag(C.Tag.home_content) }) {

    val notification: KNode = child {
        hasTestTag(C.Tag.notification)
        useUnmergedTree = true
    }
    val homeScreenContent: KNode = child { hasTestTag("homeContent") }
    val searchBar: KNode = child {
        hasTestTag(C.Tag.main_container_text_field_custom)
        useUnmergedTree = true
    }
    val textFieldCustom: KNode = child {
        hasTestTag(C.Tag.text_field_custom)
        useUnmergedTree = true
    }
}
package com.arygm.quickfix.kaspresso.element

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.arygm.quickfix.ressources.C
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class QuickFixSearchBarElement(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<QuickFixSearchBarElement>(semanticsProvider = semanticsProvider) {

  val leadingIcon: KNode = child {
    hasTestTag(C.Tag.icon_custom_text_field)
    useUnmergedTree = true
  }
  val textFieldCustom: KNode = child {
    hasTestTag(C.Tag.text_field_custom)
    useUnmergedTree = true
  }
  val placeHolder: KNode = child {
    hasTestTag(C.Tag.place_holder_text_field_custom)
    useUnmergedTree = true
  }
  val clearButton: KNode = child {
    hasTestTag(C.Tag.clear_button_text_field_custom)
    useUnmergedTree = true
  }
}

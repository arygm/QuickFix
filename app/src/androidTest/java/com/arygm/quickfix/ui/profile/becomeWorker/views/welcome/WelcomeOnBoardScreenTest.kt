package com.arygm.quickfix.ui.profile.becomeWorker.views.welcome

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.ui.theme.QuickFixTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class WelcomeOnBoardScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setup() {
    navigationActions = mock()
  }

  @Test
  fun testInitialUI() {
    composeTestRule.setContent { QuickFixTheme { WelcomeOnBoardScreen(navigationActions) } }

    // Check UI elements are displayed
    composeTestRule.onNodeWithTag(C.Tag.welcomeOnBoardScreenStayUserButton).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.welcomeOnBoardScreenSwitchWorkerButton).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.welcomeOnBoardScreenImage).assertIsDisplayed()
    composeTestRule.onNodeWithText("Welcome on board !!").assertIsDisplayed()
  }

  @Test
  fun testInitialNavigationStayUser() {
    composeTestRule.setContent { QuickFixTheme { WelcomeOnBoardScreen(navigationActions) } }
    // Check UI elements are displayed
    composeTestRule.onNodeWithTag(C.Tag.welcomeOnBoardScreenStayUserButton).performClick()
    verify(navigationActions).navigateTo(Screen.PROFILE)
  }

  @Test
  fun testInitialNavigationSwitchWorker() {
    composeTestRule.setContent { QuickFixTheme { WelcomeOnBoardScreen(navigationActions) } }
    // Check UI elements are displayed
    composeTestRule.onNodeWithTag(C.Tag.welcomeOnBoardScreenSwitchWorkerButton).performClick()
    verify(navigationActions).navigateTo(Screen.PROFILE)
  }
}

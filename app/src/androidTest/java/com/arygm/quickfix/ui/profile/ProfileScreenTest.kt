package com.arygm.quickfix.ui.profile

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.arygm.quickfix.R
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class ProfileScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  private val options =
      listOf(
          OptionItem("Settings", IconType.Vector(Icons.Outlined.Settings)) {},
          OptionItem("Activity", IconType.Resource(R.drawable.dashboardvector)) {},
          OptionItem("Set up your business account", IconType.Resource(R.drawable.workvector)) {},
          OptionItem("Account configuration", IconType.Resource(R.drawable.accountsettingsvector)) {
            navigationActions.navigateTo(Screen.ACCOUNT_CONFIGURATION)
            Log.d("userResult", navigationActions.currentRoute())
          },
          OptionItem("Workers network", IconType.Vector(Icons.Outlined.Phone)) {},
          OptionItem("Legal", IconType.Vector(Icons.Outlined.Info)) {})

  @Before
  fun setup() {
    navigationActions = mock(NavigationActions::class.java)
  }

  @Test
  fun profileScreenDisplaysCorrectly() {
    composeTestRule.setContent { ProfileScreen(navigationActions) }

    // Test for profile title and name
    composeTestRule.onNodeWithTag("ProfileTopAppBar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("ProfileTitle").assertTextEquals("Profile")
    composeTestRule.onNodeWithTag("ProfileCard").assertIsDisplayed()

    // Test for Upcoming Activities section
    composeTestRule.onNodeWithTag("UpcomingActivitiesCard").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("UpcomingActivitiesText")
        .assertTextEquals(
            "This isn’t developed yet; but it can display upcoming activities for both a user and worker")

    // Test for Wallet and Help buttons
    composeTestRule.onNodeWithTag("WalletButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("WalletText", useUnmergedTree = true).assertTextEquals("Wallet")
    composeTestRule.onNodeWithTag("HelpButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("HelpText", useUnmergedTree = true).assertTextEquals("Help")

    // Scroll to the Logout Button
    composeTestRule.onNodeWithTag("LogoutButton").performScrollTo()
    composeTestRule.onNodeWithTag("LogoutButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("LogoutText", useUnmergedTree = true).assertTextEquals("Log out")
  }

  @Test
  fun walletButtonClickTest() {
    composeTestRule.setContent { ProfileScreen(navigationActions) }

    composeTestRule.onNodeWithTag("WalletButton").performClick()
  }

  @Test
  fun helpButtonClickTest() {
    composeTestRule.setContent { ProfileScreen(navigationActions) }

    composeTestRule.onNodeWithTag("HelpButton").performClick()
  }

  @Test
  fun optionsAreDisplayedCorrectly() {
    composeTestRule.setContent { ProfileScreen(navigationActions) }

    options.forEach { option ->
      val optionTag = option.label.replace(" ", "") + "Option"
      composeTestRule.onNodeWithTag(optionTag, useUnmergedTree = true).assertIsDisplayed()
      val optionTextTag = option.label.replace(" ", "") + "Text"
      composeTestRule
          .onNodeWithTag(optionTextTag, useUnmergedTree = true)
          .assertTextEquals(option.label)
    }
  }

  @Test
  fun logoutButtonClickTest() {
    composeTestRule.setContent { ProfileScreen(navigationActions) }

    composeTestRule.onNodeWithTag("LogoutButton").performClick()
  }

  @Test
  fun navigateToAccountConfigurationTest() {
    composeTestRule.setContent { ProfileScreen(navigationActions) }

    // Perform click on "Account configuration"
    composeTestRule.onNodeWithTag("AccountconfigurationOption").performClick()

    // Verify that the navigation to Screen.ACCOUNT_CONFIGURATION happened
    verify(navigationActions).navigateTo(Screen.ACCOUNT_CONFIGURATION)
  }

  @Test
  fun navigateToWorkerSetupTest() {
    composeTestRule.setContent { ProfileScreen(navigationActions) }

    // Perform click on "Set up your business account"
    composeTestRule.onNodeWithTag("SetupyourbusinessaccountOption").performClick()

    // Verify that the navigation to Screen.TO_WORKER happened
    verify(navigationActions).navigateTo(Screen.TO_WORKER)
  }
}

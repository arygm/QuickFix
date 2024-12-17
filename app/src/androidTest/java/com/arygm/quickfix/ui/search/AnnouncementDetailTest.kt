package com.arygm.quickfix.ui.search

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.search.AnnouncementDetailScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class AnnouncementDetailTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions
  private lateinit var announcementViewModel: AnnouncementViewModel

  @Before
  fun setup() {
    // Mock NavigationActions and ViewModel
    navigationActions = mock(NavigationActions::class.java)
    announcementViewModel = mock(AnnouncementViewModel::class.java)
  }

  @Test
  fun announcementDetailScreen_displaysTopAppBarWithTitle() {
    composeTestRule.setContent {
      AnnouncementDetailScreen(
          announcementViewModel = announcementViewModel, navigationActions = navigationActions)
    }

    // Verify that the Top App Bar is displayed
    composeTestRule.onNodeWithTag("AnnouncementDetailTopBarTitle").assertIsDisplayed()

    // Verify the Top App Bar Title text
    composeTestRule
        .onNodeWithTag("AnnouncementDetailTopBarTitle")
        .assertIsDisplayed()
        .assert(hasText("Announcement Detail"))
  }

  @Test
  fun announcementDetailScreen_displaysCenteredText() {
    composeTestRule.setContent {
      AnnouncementDetailScreen(
          announcementViewModel = announcementViewModel, navigationActions = navigationActions)
    }

    // Verify that the centered text is displayed
    composeTestRule.onNodeWithTag("AnnoucementDetailTitle").assertIsDisplayed()

    // Verify the centered text content
    composeTestRule
        .onNodeWithTag("AnnoucementDetailTitle")
        .assertIsDisplayed()
        .assert(hasText("Announcement Detail"))
  }

  @Test
  fun announcementDetailScreen_goBackButtonWorks() {
    composeTestRule.setContent {
      AnnouncementDetailScreen(
          announcementViewModel = announcementViewModel, navigationActions = navigationActions)
    }

    // Verify that the Go Back button exists
    composeTestRule.onNodeWithTag("GoBackButton").assertIsDisplayed()

    // Perform a click on the Go Back button
    composeTestRule.onNodeWithTag("GoBackButton").performClick()

    // Verify that navigationActions.goBack() is triggered
    verify(navigationActions).goBack()
  }

  @Test
  fun announcementDetailScreen_fullLayoutRendersCorrectly() {
    composeTestRule.setContent {
      AnnouncementDetailScreen(
          announcementViewModel = announcementViewModel, navigationActions = navigationActions)
    }

    // Verify all key components exist and are displayed
    composeTestRule.onNodeWithTag("AnnouncementDetailScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("GoBackButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AnnouncementDetailTopBarTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AnnoucementDetailTitle").assertIsDisplayed()
  }
}

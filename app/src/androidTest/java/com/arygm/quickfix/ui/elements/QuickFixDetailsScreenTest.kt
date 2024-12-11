package com.arygm.quickfix.ui.elements

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.profile.dataFields.Service
import com.arygm.quickfix.model.quickfix.QuickFix
import com.arygm.quickfix.model.quickfix.Status
import com.google.firebase.Timestamp
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class QuickFixDetailsScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var quickFixMock: QuickFix

  @Before
  fun setup() {
    val includedServices =
        listOf(
            mock(Service::class.java).apply { `when`(name).thenReturn("Initial Consultation") },
            mock(Service::class.java).apply {
              `when`(name).thenReturn("Basic Surface Preparation")
            })

    val addOnServices =
        listOf(
            mock(Service::class.java).apply {
              `when`(name).thenReturn("Extra Coats for added Durability")
            },
            mock(Service::class.java).apply { `when`(name).thenReturn("Premium Paint Upgrade") })

    quickFixMock =
        QuickFix(
            uid = "12345",
            status = Status.PENDING,
            imageUrl = listOf("https://via.placeholder.com/120", "https://via.placeholder.com/120"),
            date = listOf(Timestamp.now()),
            time = Timestamp.now(),
            includedServices = includedServices,
            addOnServices = addOnServices,
            workerId = "Worker Id",
            userId = "User Id",
            chatUid = "chat_12345",
            title = "This is a very long description that will be truncated in the collapsed view.",
            description =
                "This is a very long description that will be truncated in the collapsed view.",
            bill = emptyList(),
            location = Location(latitude = 48.8566, longitude = 2.3522, name = "Paris, France"))
  }

  @Test
  fun quickFixDetailsScreen_displaysTitleAndServices() {
    composeTestRule.setContent {
      QuickFixDetailsScreen(quickFix = quickFixMock, onShowMoreToggle = {}, isExpanded = false)
    }

    // Check the title
    composeTestRule.onNodeWithText("Selected Services").assertIsDisplayed()

    // Check displayed services
    quickFixMock.includedServices.forEach { service ->
      composeTestRule.onNodeWithText(service.name).assertIsDisplayed()
    }
    quickFixMock.addOnServices.forEach { service ->
      composeTestRule.onNodeWithText(service.name).assertIsDisplayed()
    }
  }

  @Test
  fun quickFixDetailsScreen_displaysDescriptionCollapsed() {
    composeTestRule.setContent {
      QuickFixDetailsScreen(quickFix = quickFixMock, onShowMoreToggle = {}, isExpanded = false)
    }

    // Check that the description is truncated
    composeTestRule.onNodeWithText(quickFixMock.title.take(250) + "...").assertIsDisplayed()

    // Check that "Show more" button is displayed
    composeTestRule.onNodeWithText("Show more").assertIsDisplayed()
  }

  @Test
  fun quickFixDetailsScreen_displaysDescriptionExpanded() {
    composeTestRule.setContent {
      QuickFixDetailsScreen(quickFix = quickFixMock, onShowMoreToggle = {}, isExpanded = true)
    }

    // Check that the full description is displayed
    composeTestRule.onNodeWithText(quickFixMock.title).assertIsDisplayed()

    // Check that "Show less" button is displayed
    composeTestRule.onNodeWithText("Show less").assertIsDisplayed()
  }

  @Test
  fun quickFixDetailsScreen_togglesDescription() {
    // Mock pour onShowMoreToggle
    val onShowMoreToggleMock = mock<(Boolean) -> Unit>()

    composeTestRule.setContent {
      QuickFixDetailsScreen(
          quickFix = quickFixMock, onShowMoreToggle = onShowMoreToggleMock, isExpanded = false)
    }

    // Vérifiez que "Show more" est initialement affiché
    composeTestRule.onNodeWithText("Show more").assertIsDisplayed()

    // Simulez un clic sur "Show more"
    composeTestRule.onNodeWithText("Show more").performClick()

    // Vérifiez que onShowMoreToggle a été appelé avec true
    verify(onShowMoreToggleMock).invoke(true)
  }

  @Test
  fun quickFixDetailsScreen_displaysImages() {
    composeTestRule.setContent {
      QuickFixDetailsScreen(quickFix = quickFixMock, onShowMoreToggle = {}, isExpanded = false)
    }

    // Check placeholders for images
    composeTestRule
        .onAllNodesWithText("Image")
        .assertCountEquals(2) // 2 placeholders should be displayed
  }
}

package com.arygm.quickfix.ui.elements

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.arygm.quickfix.R
import com.arygm.quickfix.model.category.Category
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class ScrollableRowTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var services: List<Category>
  private lateinit var onServiceClick: (Category) -> Unit

  @Before
  fun setUp() {
    // Liste des services pour les tests
    services =
        listOf(
            Category(
                name = "Handyman Services",
                description = "Mechanic",
                id = R.drawable.mechanic.toString()),
            Category(
                name = "Gardening", description = "Gardener", id = R.drawable.gardener.toString()),
            Category(
                name = "Electrical Work",
                description = "Electrician",
                id = R.drawable.electrician.toString()),
            Category(name = "Painting", description = "Paint", id = R.drawable.painter.toString()),
            Category(
                name = "Plumbing", description = "Plumber", id = R.drawable.plumber.toString()))

    // Mock de la fonction de clic pour tester les interactions
    onServiceClick = mock()
  }

  @Test
  fun popularServicesRow_isDisplayed() {
    composeTestRule.setContent {
      PopularServicesRow(services = services, onServiceClick = onServiceClick)
    }

    // Vérifie que la rangée `PopularServicesRow` est affichée
    composeTestRule.onNodeWithTag("PopularServicesRow").assertIsDisplayed()
  }

  @Test
  fun popularServicesRow_canScrollThroughServices() {
    composeTestRule.setContent {
      PopularServicesRow(services = services, onServiceClick = onServiceClick)
    }

    val lastService = services.last()

    // Scroll to the last service card within the LazyRow
    composeTestRule
        .onNodeWithTag("PopularServicesRow")
        .performScrollToNode(hasTestTag("ServiceCard_${lastService.description}"))

    // Assert that the last service is displayed
    composeTestRule.onNodeWithTag("ServiceCard_${lastService.description}").assertIsDisplayed()
  }

  @Test
  fun serviceCard_triggersOnClick() {
    // Choix d'un service pour tester le clic
    val testService = services[0]

    composeTestRule.setContent {
      ServiceCard(service = testService, onClick = { onServiceClick(testService) })
    }

    // Clique sur la carte de service et vérifie que le clic est bien invoqué avec le bon service
    composeTestRule.onNodeWithTag("ServiceCard_${testService.description}").performClick()
    verify(onServiceClick).invoke(testService)
  }

  @Test
  fun popularServicesRow_triggersOnClickForEachService() {
    composeTestRule.setContent {
      PopularServicesRow(services = services, onServiceClick = onServiceClick)
    }

    // Click on each service and verify the interaction
    services.forEach { service ->
      // Scroll to the service card within the LazyRow
      composeTestRule
          .onNodeWithTag("PopularServicesRow")
          .performScrollToNode(hasTestTag("ServiceCard_${service.description}"))

      // Perform click on the service card
      composeTestRule
          .onNodeWithTag("ServiceCard_${service.description}")
          .assertIsDisplayed()
          .performClick()

      // Verify the click interaction
      verify(onServiceClick).invoke(service)
    }

    // Verify that there are no unexpected interactions
    verifyNoMoreInteractions(onServiceClick)
  }
}

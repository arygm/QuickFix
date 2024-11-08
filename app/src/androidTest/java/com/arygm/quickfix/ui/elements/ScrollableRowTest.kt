package com.arygm.quickfix.ui.elements

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.arygm.quickfix.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class ScrollableRowTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var services: List<Service>
  private lateinit var onServiceClick: (Service) -> Unit

  @Before
  fun setUp() {
    // Liste des services pour les tests
    services =
        listOf(
            Service("Painter", R.drawable.worker_image),
            Service("Gardener", R.drawable.worker_image),
            Service("Electrician", R.drawable.worker_image),
            Service("Plumber", R.drawable.worker_image),
            Service("Mechanic", R.drawable.worker_image))

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
        .performScrollToNode(hasTestTag("ServiceCard_${lastService.name}"))

    // Assert that the last service is displayed
    composeTestRule.onNodeWithTag("ServiceCard_${lastService.name}").assertIsDisplayed()
  }

  @Test
  fun serviceCard_triggersOnClick() {
    // Choix d'un service pour tester le clic
    val testService = services[0]

    composeTestRule.setContent {
      ServiceCard(service = testService, onClick = { onServiceClick(testService) })
    }

    // Clique sur la carte de service et vérifie que le clic est bien invoqué avec le bon service
    composeTestRule.onNodeWithTag("ServiceCard_${testService.name}").performClick()
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
          .performScrollToNode(hasTestTag("ServiceCard_${service.name}"))

      // Perform click on the service card
      composeTestRule
          .onNodeWithTag("ServiceCard_${service.name}")
          .assertIsDisplayed()
          .performClick()

      // Verify the click interaction
      verify(onServiceClick).invoke(service)
    }

    // Verify that there are no unexpected interactions
    verifyNoMoreInteractions(onServiceClick)
  }
}

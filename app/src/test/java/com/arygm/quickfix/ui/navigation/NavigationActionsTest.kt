package com.arygm.quickfix.ui.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import junit.framework.TestCase.assertEquals
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class NavigationActionsTest {

  private lateinit var navigationDestination: NavDestination
  private lateinit var navHostController: NavHostController
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationDestination = mock(NavDestination::class.java)
    navHostController = mock(NavHostController::class.java)
    navigationActions = NavigationActions(navHostController)
  }

  @Test
  fun navigateToCallsController() {
    navigationActions.navigateTo(TopLevelDestinations.HOME)
    verify(navHostController).navigate(eq(Route.HOME), any<NavOptionsBuilder.() -> Unit>())

    navigationActions.navigateTo(Screen.ACTIVITY)
    verify(navHostController).navigate(Screen.ACTIVITY)
  }

  @Test
  fun goBackCallsController() {
    navigationActions.goBack()
    verify(navHostController).popBackStack()
  }

  @Test
  fun currentRouteWorksWithDestination() {
    `when`(navHostController.currentDestination).thenReturn(navigationDestination)
    `when`(navigationDestination.route).thenReturn(Route.HOME)

    assertThat(navigationActions.currentRoute(), `is`(Route.HOME))
  }

  @Test
  fun `test home route returns 1`() {
    assertEquals(1, getBottomBarId(Route.HOME, true))
    assertEquals(1, getBottomBarId(Route.HOME, false))
  }

  @Test
  fun `test announcement and calendar routes return 2`() {
    assertEquals(2, getBottomBarId(Route.ANNOUNCEMENT, true))
    assertEquals(2, getBottomBarId(Route.ANNOUNCEMENT, false))
    assertEquals(2, getBottomBarId(Route.CALENDAR, true))
    assertEquals(2, getBottomBarId(Route.CALENDAR, false))
  }

  @Test
  fun `test map route returns 3`() {
    assertEquals(3, getBottomBarId(Route.MAP, true))
    assertEquals(3, getBottomBarId(Route.MAP, false))
  }

  @Test
  fun `test activity route returns 3 for user and 4 for others`() {
    assertEquals(3, getBottomBarId(Route.ACTIVITY, true))
    assertEquals(4, getBottomBarId(Route.ACTIVITY, false))
  }

  @Test
  fun `test other route returns 4 for user and 5 for others`() {
    assertEquals(4, getBottomBarId(Route.OTHER, true))
    assertEquals(5, getBottomBarId(Route.OTHER, false))
  }

  @Test
  fun `test unknown route returns -1`() {
    assertEquals(-1, getBottomBarId("unknown_route", true))
    assertEquals(-1, getBottomBarId("unknown_route", false))
  }
}

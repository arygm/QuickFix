package com.arygm.quickfix.ui.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserRoute
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserTopLevelDestinations
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.getBottomBarIdUser
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
  private lateinit var previousBackStackEntry: NavBackStackEntry
  private lateinit var currentBackStackEntry: NavBackStackEntry
  private lateinit var savedStateHandle: SavedStateHandle

  @Before
  fun setUp() {
    navigationDestination = mock(NavDestination::class.java)
    navHostController = mock(NavHostController::class.java)
    navigationActions = NavigationActions(navHostController)
    previousBackStackEntry = mock(NavBackStackEntry::class.java)
    currentBackStackEntry = mock(NavBackStackEntry::class.java)
    savedStateHandle = mock(SavedStateHandle::class.java)
  }

  @Test
  fun navigateToCallsController() {
    navigationActions.navigateTo(UserTopLevelDestinations.HOME)
    verify(navHostController).navigate(eq(UserRoute.HOME), any<NavOptionsBuilder.() -> Unit>())

    navigationActions.navigateTo(UserScreen.PROFILE)
    verify(navHostController).navigate(UserScreen.PROFILE)
  }

  @Test
  fun goBackCallsController() {
    navigationActions.goBack()
    verify(navHostController).popBackStack()
  }

  @Test
  fun currentRouteWorksWithDestination() {
    `when`(navHostController.currentDestination).thenReturn(navigationDestination)
    `when`(navigationDestination.route).thenReturn(UserRoute.HOME)

    assertThat(navigationActions.currentRoute(), `is`(UserRoute.HOME))
  }

  @Test
  fun `test home route returns 1`() {
    assertEquals(1, getBottomBarIdUser(UserRoute.HOME))
    assertEquals(1, getBottomBarIdUser(UserRoute.HOME))
  }

  @Test
  fun `test announcement and calendar routes return 2`() {
    assertEquals(2, getBottomBarIdUser(UserRoute.SEARCH))
    assertEquals(2, getBottomBarIdUser(UserRoute.SEARCH))
  }

  @Test
  fun `test map route returns 3`() {
    assertEquals(3, getBottomBarIdUser(UserRoute.DASHBOARD))
    assertEquals(3, getBottomBarIdUser(UserRoute.DASHBOARD))
  }

  @Test
  fun `test activity route returns 3 for user and 4 for others`() {
    assertEquals(4, getBottomBarIdUser(UserRoute.PROFILE))
    assertEquals(4, getBottomBarIdUser(UserRoute.PROFILE))
  }

  @Test
  fun `test unknown route returns -1`() {
    assertEquals(-1, getBottomBarIdUser("unknown_route"))
    assertEquals(-1, getBottomBarIdUser("unknown_route"))
  }

  @Test
  fun saveToBackStackCallsController() {
    val key = "testKey"
    val value = "testValue"

    `when`(navHostController.previousBackStackEntry).thenReturn(previousBackStackEntry)
    `when`(previousBackStackEntry.savedStateHandle).thenReturn(savedStateHandle)

    navigationActions.saveToBackStack(key, value)

    verify(savedStateHandle).set(key, value)
  }

  @Test
  fun getFromBackStackReturnsCorrectValue() {
    val key = "testKey"
    val expectedValue = "testValue"

    `when`(navHostController.currentBackStackEntry).thenReturn(currentBackStackEntry)
    `when`(currentBackStackEntry.savedStateHandle).thenReturn(savedStateHandle)
    `when`(savedStateHandle.get<Any>(key)).thenReturn(expectedValue)

    val result = navigationActions.getFromBackStack(key)
    assertEquals(expectedValue, result)
  }

  @Test
  fun saveToCurBackStackSetsValueCorrectly() {
    val key = "testKey"
    val value = "testValue"

    `when`(navHostController.currentBackStackEntry).thenReturn(currentBackStackEntry)
    `when`(currentBackStackEntry.savedStateHandle).thenReturn(savedStateHandle)

    navigationActions.saveToCurBackStack(key, value)

    verify(savedStateHandle).set(key, value)
  }

  @Test
  fun saveToCurBackStackRemovesValueWhenNull() {
    val key = "testKey"

    `when`(navHostController.currentBackStackEntry).thenReturn(currentBackStackEntry)
    `when`(currentBackStackEntry.savedStateHandle).thenReturn(savedStateHandle)

    navigationActions.saveToCurBackStack(key, null)

    verify(savedStateHandle).remove<Any>(key)
  }
}

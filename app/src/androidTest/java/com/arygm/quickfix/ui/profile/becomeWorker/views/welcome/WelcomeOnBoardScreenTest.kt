package com.arygm.quickfix.ui.profile.becomeWorker.views.welcome

import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.text.AnnotatedString
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.category.Scale
import com.arygm.quickfix.model.category.Subcategory
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.ui.profile.becomeWorker.views.professional.ProfessionalInfoScreen
import com.arygm.quickfix.ui.theme.QuickFixTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class WelcomeOnBoardScreenTest {
        @get:Rule
        val composeTestRule = createComposeRule()

    private lateinit var navigationActions: NavigationActions

    @Before
    fun setup() {
        navigationActions = mock()
    }

    @Test
    fun testInitialUI() {
        composeTestRule.setContent {
            QuickFixTheme {
                WelcomeOnBoardScreen(navigationActions)
            }
        }

        // Check UI elements are displayed
        composeTestRule.onNodeWithTag(C.Tag.welcomeOnBoardScreenStayUserButton).assertIsDisplayed()
        composeTestRule.onNodeWithTag(C.Tag.welcomeOnBoardScreenSwitchWorkerButton).assertIsDisplayed()
        composeTestRule.onNodeWithTag(C.Tag.welcomeOnBoardScreenImage).assertIsDisplayed()
        composeTestRule.onNodeWithText("Welcome on board !!").assertIsDisplayed()
    }

    @Test
    fun testInitialNavigationStayUser() {
        composeTestRule.setContent {
            QuickFixTheme {
                WelcomeOnBoardScreen(navigationActions)
            }
        }
        // Check UI elements are displayed
        composeTestRule.onNodeWithTag(C.Tag.welcomeOnBoardScreenStayUserButton).performClick()
        verify(navigationActions).navigateTo(Screen.PROFILE)
    }
    @Test
    fun testInitialNavigationSwitchWorker() {
        composeTestRule.setContent {
            QuickFixTheme {
                WelcomeOnBoardScreen(navigationActions)
            }
        }
        // Check UI elements are displayed
        composeTestRule.onNodeWithTag(C.Tag.welcomeOnBoardScreenSwitchWorkerButton).performClick()
        verify(navigationActions).navigateTo(Screen.PROFILE)
    }
}
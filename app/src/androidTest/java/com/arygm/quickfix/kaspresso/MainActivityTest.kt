package com.arygm.quickfix.kaspresso

import android.util.Log
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.MainActivity
import com.arygm.quickfix.kaspresso.screen.RegisterScreen
import com.arygm.quickfix.kaspresso.screen.WelcomeScreen
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.kaspersky.components.kautomator.system.UiSystem.click
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.kaspersky.kaspresso.flakysafety.*
import org.junit.Before
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class MainActivityTest : TestCase() {

    private lateinit var navigationActions: NavigationActions

    @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        // Initialize the navigationActions mock
        navigationActions = Mockito.mock(NavigationActions::class.java)
    }

    @Test
    fun test() = run {
        step("Set up the WelcomeScreen and transit to the register") {
            // Retry the action until it works with a timeout of 10 seconds
                ComposeScreen.onComposeScreen<WelcomeScreen>(composeTestRule) {
                    registerButton {
                        assertIsDisplayed()
                        performClick()
                        // Log the click action
                        Log.d("TestLog", "Register button clicked")
                    }
                }
            composeTestRule.mainClock.advanceTimeBy(1500L)

                ComposeScreen.onComposeScreen<RegisterScreen>(composeTestRule) {
                    firstName{
                        assertIsDisplayed()
                        performClick()

                        // Log the assertion
                        Log.d("TestLog", "First name input displayed")
                        performTextInput("John")
                    }
                }
            }
        }
    }
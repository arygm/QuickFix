package com.arygm.QuickFix

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.assertTextEquals
import com.android.sample.Greeting
import com.android.sample.resources.C
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.junit.Rule

class GreetingSteps {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Given("^I launch the app$")
    fun iLaunchTheApp() {
        // No-op for now
    }

    @When("^I see the Greeting with name \"([^\"]*)\"$")
    fun iSeeTheGreetingWithName(name: String) {
        composeTestRule.setContent {
            Greeting(name = name)
        }
    }

    @Then("^I should see \"([^\"]*)\" on the screen$")
    fun iShouldSeeTheGreeting(expectedText: String) {
        composeTestRule
            .onNodeWithTag(C.Tag.greeting)  // Accessing the Composable by its testTag
            .assertTextEquals(expectedText)
    }
}
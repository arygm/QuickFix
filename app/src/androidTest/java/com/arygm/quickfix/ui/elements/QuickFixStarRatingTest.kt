package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

class QuickFixStarRatingTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun verify_all_stars_are_rendered() {
        composeTestRule.setContent {
            RatingBar(
                rating = 3f,
                modifier = Modifier.height(20.dp)
            )
        }
        // Check that all 5 stars are present
        (0..4).forEach { index ->
            composeTestRule.onNodeWithTag("Star_$index").assertExists()
        }
    }
}
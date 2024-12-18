package com.arygm.quickfix.ui.home

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.home.EarningsWidget
import org.junit.Rule
import org.junit.Test

class EarningsWidgetTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun earningsWidget_showsCorrectValues() {
    val personalBalance = 1234.56
    val earningsThisMonth = 456.78
    val avgSellingPrice = 789.12
    val activeOrders = 3
    val activeOrderValue = 150.50

    composeTestRule.setContent {
      EarningsWidget(
          personalBalance = personalBalance,
          earningsThisMonth = earningsThisMonth,
          avgSellingPrice = avgSellingPrice,
          activeOrders = activeOrders,
          activeOrderValue = activeOrderValue)
    }

    // Test Header
    composeTestRule.onNodeWithTag("EarningsTitle").assertExists().assertTextEquals("Earnings")

    // Test Left Column
    composeTestRule.onNodeWithTag("PersonalBalanceLabel").assertTextEquals("Personal balance")
    composeTestRule.onNodeWithTag("PersonalBalanceValue").assertTextContains("1234.56CHF")

    composeTestRule.onNodeWithTag("AvgSellingPriceLabel").assertTextEquals("Avg. selling price")
    composeTestRule.onNodeWithTag("AvgSellingPriceValue").assertTextContains("789.12CHF")

    // Test Right Column
    composeTestRule.onNodeWithTag("EarningsThisMonthLabel").assertTextEquals("Earning this month")
    composeTestRule.onNodeWithTag("EarningsThisMonthValue").assertTextContains("456.78CHF")

    composeTestRule.onNodeWithTag("ActiveOrdersLabel").assertTextEquals("Active orders")
    composeTestRule
        .onNodeWithTag("ActiveOrdersValue")
        .assertExists()
        .assertTextContains("3 (150.5CHF)")
  }

  @Test
  fun earningsWidget_displaysDivider() {
    composeTestRule.setContent {
      EarningsWidget(
          personalBalance = 0.0,
          earningsThisMonth = 0.0,
          avgSellingPrice = 0.0,
          activeOrders = 0,
          activeOrderValue = 0.0)
    }

    // Check that divider exists
    composeTestRule.onNodeWithTag("EarningsDivider").assertExists()
  }
}

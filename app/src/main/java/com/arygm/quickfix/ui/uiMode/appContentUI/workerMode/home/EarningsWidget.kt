package com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.ui.theme.poppinsTypography
import java.text.DecimalFormat

@Composable
fun EarningsWidget(
    personalBalance: Double,
    earningsThisMonth: Double,
    avgSellingPrice: Double,
    activeOrders: Int,
    activeOrderValue: Double,
) {
  BoxWithConstraints {
    val maxWidth = maxWidth
    val maxHeight = maxHeight
    val currencyFormat = DecimalFormat("######.##CHF")

    Column(
        modifier =
            Modifier.fillMaxWidth()
                .padding(maxWidth * 0.025f)
                .shadow(
                    maxWidth * 0.01f, RoundedCornerShape(maxWidth * 0.05f)) // Shadow size dynamic
                .background(colorScheme.surface, RoundedCornerShape(maxWidth * 0.03f))
                .testTag("EarningsWidget"),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start) {
          // Header Row
          Row(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(horizontal = maxWidth * 0.03f)
                      .padding(top = maxHeight * 0.01f)
                      .testTag("EarningsHeader"),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Earnings",
                    style = poppinsTypography.headlineMedium,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground,
                    modifier = Modifier.testTag("EarningsTitle"))
              }

          HorizontalDivider(
              thickness = 1.dp,
              color = colorScheme.onSurface.copy(alpha = 0.2f),
              modifier = Modifier.padding(vertical = maxHeight * 0.01f).testTag("EarningsDivider"))

          // Content Rows
          Row(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(horizontal = maxWidth * 0.03f)
                      .padding(bottom = maxHeight * 0.01f)
                      .testTag("EarningsContent"),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.testTag("LeftColumn")) {
                  Text(
                      text = "Personal balance",
                      style = poppinsTypography.labelSmall,
                      fontSize = 10.sp,
                      modifier = Modifier.testTag("PersonalBalanceLabel"))
                  Text(
                      text = currencyFormat.format(personalBalance),
                      style = poppinsTypography.headlineMedium,
                      fontSize = 16.sp,
                      color = colorScheme.primary,
                      modifier = Modifier.testTag("PersonalBalanceValue"))

                  Spacer(modifier = Modifier.height(maxHeight * 0.01f))

                  Text(
                      text = "Avg. selling price",
                      style = poppinsTypography.labelSmall,
                      fontSize = 10.sp,
                      modifier = Modifier.testTag("AvgSellingPriceLabel"))
                  Text(
                      text = currencyFormat.format(avgSellingPrice),
                      style = poppinsTypography.headlineMedium,
                      fontSize = 16.sp,
                      color = colorScheme.onBackground,
                      modifier = Modifier.testTag("AvgSellingPriceValue"))
                }

                Column(modifier = Modifier.testTag("RightColumn")) {
                  Text(
                      text = "Earning this month",
                      style = poppinsTypography.labelSmall,
                      fontSize = 10.sp,
                      modifier = Modifier.testTag("EarningsThisMonthLabel"))
                  Text(
                      text = currencyFormat.format(earningsThisMonth),
                      style = poppinsTypography.headlineMedium,
                      color = colorScheme.onBackground,
                      fontSize = 16.sp,
                      modifier = Modifier.testTag("EarningsThisMonthValue"))

                  Spacer(modifier = Modifier.height(maxHeight * 0.01f))

                  Text(
                      text = "Active orders",
                      style = poppinsTypography.labelSmall,
                      fontSize = 10.sp,
                      modifier = Modifier.testTag("ActiveOrdersLabel"))

                  StyledActiveOrders(
                      count = activeOrders,
                      amount = currencyFormat.format(activeOrderValue),
                      modifier = Modifier.testTag("ActiveOrdersValue"))
                }
              }
        }
  }
}

@Composable
fun StyledActiveOrders(count: Int, amount: String, modifier: Modifier = Modifier) {
  val styledText: AnnotatedString = buildAnnotatedString {
    append("$count ")
    addStyle(style = SpanStyle(color = colorScheme.onBackground), start = 0, end = "$count".length)
    append("($amount)")
    addStyle(
        style = SpanStyle(color = Color.Gray),
        start = "$count ".length,
        end = "$count ($amount)".length)
  }

  Text(
      text = styledText,
      style = poppinsTypography.headlineMedium,
      fontSize = 16.sp, // Keep the font size static here unless needed dynamic
      letterSpacing = (-0.5).sp,
      modifier = modifier)
}

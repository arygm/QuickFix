package com.arygm.quickfix.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Carpenter
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.ImagesearchRoller
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.NaturePeople
import androidx.compose.material.icons.outlined.Plumbing
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.TopLevelDestinations
import com.arygm.quickfix.ui.theme.poppinsTypography

@Composable
fun SearchOnBoarding(navigationActions: NavigationActions, isUser: Boolean) {

  var searchQuery by remember { mutableStateOf("") }

  val searchCategories =
      listOf(
          SearchCategory(
              icon = Icons.Outlined.ImagesearchRoller,
              title = "Painting",
              description = "Find skilled painters for residential or commercial projects.",
              onClick = {
                // Search Painters
              }),
          SearchCategory(
              icon = Icons.Outlined.Plumbing,
              title = "Plumbing",
              description = "Connect with expert plumbers for repairs and installations.",
              onClick = {
                // Search Plumbers
              }),
          SearchCategory(
              icon = Icons.Outlined.NaturePeople,
              title = "Gardening",
              description = "Hire professional gardeners for landscaping and maintenance.",
              onClick = {
                // Search Gardeners
              }),
          SearchCategory(
              icon = Icons.Outlined.ElectricalServices,
              title = "Electrical Work",
              description = "Locate certified electricians for safe and efficient service.",
              onClick = {
                // Search Electricians
              }),
          SearchCategory(
              icon = Icons.Outlined.Handyman,
              title = "Handyman Services",
              description = "Get help with various minor home repairs and tasks.",
              onClick = {
                // Search Handyman
              }),
          SearchCategory(
              icon = Icons.Outlined.CleaningServices,
              title = "Cleaning Services",
              description = "Book reliable cleaners for home or office maintenance.",
              onClick = {
                // Search Cleaners
              }),
          SearchCategory(
              icon = Icons.Outlined.Carpenter,
              title = "Carpentry",
              description = "Hire experienced carpenters for woodwork and construction tasks.",
              onClick = {
                // Search Carpenters
              }),
          SearchCategory(
              icon = Icons.Outlined.LocalShipping,
              title = "Moving Services",
              description =
                  "Find professional movers to help with local or long-distance relocation tasks.",
              onClick = {
                // Search Movers
              }))

  BoxWithConstraints {
    val widthRatio = maxWidth / 411
    val heightRatio = maxHeight / 860
    val sizeRatio = minOf(widthRatio, heightRatio)

    // Use Scaffold for the layout structure
    Scaffold(
        containerColor = colorScheme.background,
        topBar = {},
        modifier = Modifier.testTag("SearchScreen"),
        content = { padding ->
          Column(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(padding)
                      .padding(top = 40.dp)
                      .padding(horizontal = 10.dp),
              horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.Center) {
                      QuickFixTextFieldCustom(
                          modifier = Modifier.testTag("searchContent"),
                          showLeadingIcon = { true },
                          leadingIcon = Icons.Outlined.Search,
                          showTrailingIcon = { searchQuery.isNotEmpty() },
                          trailingIcon = {
                            IconButton(
                                onClick = { searchQuery = "" },
                                modifier = Modifier.testTag("clearSearchQueryIcon")) {
                                  Icon(
                                      imageVector = Icons.Filled.Clear,
                                      contentDescription = "Clear search query",
                                      tint = colorScheme.onBackground,
                                  )
                                }
                          },
                          placeHolderText = "Find your perfect fix with QuickFix",
                          value = searchQuery, // Search query
                          onValueChange = {
                            searchQuery = it
                            // @TODO: Implement search functionality
                          },
                          shape = CircleShape,
                          textStyle = poppinsTypography.bodyMedium,
                          textColor = colorScheme.onBackground,
                          placeHolderColor = colorScheme.onBackground,
                          leadIconColor = colorScheme.onBackground,
                          widthField = 320.dp * widthRatio.value,
                          heightField = 40.dp,
                          moveContentHorizontal = 10.dp,
                          moveContentBottom = 0.dp,
                          moveContentTop = 0.dp,
                          sizeIconGroup = 30.dp,
                          spaceBetweenLeadIconText = 0.dp,
                          onClick = true,
                      )
                      Spacer(modifier = Modifier.width(10.dp))
                      QuickFixButton(
                          buttonText = "Cancel",
                          textColor = colorScheme.onSecondaryContainer,
                          buttonColor = colorScheme.background,
                          buttonOpacity = 1f,
                          textStyle = poppinsTypography.labelSmall,
                          onClickAction = {
                            navigationActions.navigateTo(TopLevelDestinations.HOME)
                          },
                          contentPadding = PaddingValues(0.dp),
                      )
                    }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.Start) {
                      Text(
                          text = "Categories",
                          style = poppinsTypography.labelLarge,
                          color = colorScheme.onBackground,
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(searchCategories) { category ->
                          SearchCategoryButton(
                              icon = category.icon,
                              title = category.title,
                              description = category.description,
                              onClick = category.onClick,
                              height = Dp(82 * heightRatio.value),
                              size = (28.dp * sizeRatio.value))
                          Spacer(modifier = Modifier.height(10.dp))
                        }
                      }
                    }
              }
        })
  }
}

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.model.categories.WorkerCategory
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.TopLevelDestinations
import com.arygm.quickfix.ui.theme.poppinsTypography

@Composable
fun SearchOnBoarding(navigationActions: NavigationActions, searchViewModel: SearchViewModel ,isUser: Boolean) {
    val categories = searchViewModel.categories.collectAsState().value
  val itemCategories = remember { categories }
  val expandedStates = remember {
    mutableStateListOf(*BooleanArray(itemCategories.size) { false }.toTypedArray())
  }
  val listState = rememberLazyListState()

  var searchQuery by remember { mutableStateOf("") }

  BoxWithConstraints {
    val widthRatio = maxWidth / 411
    val heightRatio = maxHeight / 860
    val sizeRatio = minOf(widthRatio, heightRatio)

    // Use Scaffold for the layout structure
    Scaffold(
        containerColor = colorScheme.background,
        topBar = {},
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
                      LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
                        itemsIndexed(itemCategories, key = { index, _ -> index }) { index, item ->
                          ExpandableCategoryItem(
                              item = item,
                              isExpanded = expandedStates[index],
                              onExpandedChange = { expandedStates[index] = it },
                          )
                          Spacer(modifier = Modifier.height(10.dp))
                        }
                      }
                    }
              }
        })
  }
}

package com.arygm.quickfix.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography

@Composable
fun CategoryContent(
    navigationActions: NavigationActions,
    searchViewModel: SearchViewModel,
    listState: LazyListState,
    expandedStates: MutableList<Boolean>,
    itemCategories: List<Category>,
    widthRatio: Float,
    heightRatio: Float,
) {
  Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp * widthRatio),
      horizontalAlignment = Alignment.Start) {
        Text(
            text = "Categories",
            style = poppinsTypography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(4.dp * heightRatio))
        LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
          itemsIndexed(itemCategories, key = { index, _ -> index }) { index, item ->
            ExpandableCategoryItem(
                item = item,
                isExpanded = expandedStates[index],
                onExpandedChange = { expandedStates[index] = it },
                searchViewModel = searchViewModel,
                navigationActions = navigationActions,
            )
            Spacer(modifier = Modifier.height(10.dp * heightRatio))
          }
        }
      }
}

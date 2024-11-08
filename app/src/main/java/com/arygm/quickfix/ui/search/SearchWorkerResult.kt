package com.arygm.quickfix.ui.search

import SearchResultTopBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.TopLevelDestinations

class MockNavigationActions(navController: NavHostController) : NavigationActions(navController) {
    override fun goBack() {
        // Mock implementation
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewSearchWorkerResult() {
    val navController = rememberNavController()
    val mockNavigationActions = MockNavigationActions(navController)
    SearchWorkerResult(navigationActions = mockNavigationActions)
}

@Composable
fun SearchWorkerResult(navigationActions: NavigationActions) {
    Scaffold(
        topBar = {
            SearchResultTopBar(
                onBackClick = { navigationActions.goBack() },
                onSearchClick = { /* TODO: Implement search click */ },
            )
        }
    ) { paddingValues ->

    }
}
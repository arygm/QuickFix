package com.arygm.quickfix.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.R
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.elements.PopularServicesRow
import com.arygm.quickfix.ui.elements.QuickFix
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.elements.QuickFixesWidget
import com.arygm.quickfix.ui.elements.Service
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navigationActions: NavigationActions, isUser: Boolean = true) {
  val focusManager = LocalFocusManager.current
  // Sample data for services and quick fixes
  val services =
      listOf(
          Service("Painter", R.drawable.painter),
          Service("Gardener", R.drawable.gardener),
          Service("Electrician", R.drawable.electrician))

  val quickFixes =
      listOf(
          QuickFix("Ramy", "Bathroom painting", "Sat, 12 Oct 2024"),
          QuickFix("Mehdi", "Laying kitchen tiles", "Sun, 13 Oct 2024"),
          QuickFix("Ramy", "Bathroom painting", "Sat, 12 Oct 2024"),
          QuickFix("Mehdi", "Laying kitchen tiles", "Sun, 13 Oct 2024"),
          QuickFix("Moha", "Toilet plumbing", "Mon, 14 Oct 2024"))

  Scaffold(
      modifier =
          Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
              .testTag("HomeScreen"),
      containerColor = colorScheme.background,
      topBar = {
        TopAppBar(
            title = {
              Column {
                Row(
                    modifier = Modifier.fillMaxSize().padding(0.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically) {
                      Image(
                          painter = painterResource(id = R.drawable.home_screen_headline),
                          contentDescription = "home_title",
                          modifier = Modifier.size(200.dp))
                    }
              }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background),
            navigationIcon = {},
            actions = {
              IconButton(
                  onClick = { navigationActions.navigateTo(UserScreen.MESSAGES) },
                  Modifier.testTag("MessagesButton")) {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Messages",
                        tint = colorScheme.background)
                  }
            })
      },
      content = { padding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(padding)
                    .padding(vertical = 8.dp)
                    .testTag("homeContent"),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {
              // Keep the Row unchanged
              Row(
                  modifier = Modifier.fillMaxWidth(),
              ) {
                Spacer(modifier = Modifier.width(10.dp))
                Log.d("QuickFixTextFieldCustomHomeScreen", "DISPLAYED")
                QuickFixTextFieldCustom(
                    modifier = Modifier.semantics { testTag = "searchBar" },
                    showLeadingIcon = { true },
                    showTrailingIcon = { true },
                    leadingIcon = Icons.Outlined.Search,
                    trailingIcon = { Icons.Default.Clear },
                    descriptionLeadIcon = "Search",
                    descriptionTrailIcon = "Clear",
                    placeHolderText = "Find your perfect fix with QuickFix",
                    shape = CircleShape,
                    textStyle = poppinsTypography.bodyMedium,
                    textColor = MaterialTheme.colorScheme.onBackground,
                    placeHolderColor = MaterialTheme.colorScheme.onBackground,
                    leadIconColor = MaterialTheme.colorScheme.onBackground,
                    trailIconColor = MaterialTheme.colorScheme.onBackground,
                    widthField = 330.dp, // unchanged width
                    heightField = 40.dp, // unchanged height
                    onValueChange = {},
                    value = "",
                    debug = "homescreen")

                Spacer(modifier = Modifier.width(20.dp))

                IconButton(
                    onClick = { navigationActions.navigateTo(UserScreen.MESSAGES) },
                    modifier =
                        Modifier.size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp)
                            .testTag(C.Tag.notification)) {
                      Icon(
                          painter = painterResource(id = R.drawable.bell),
                          contentDescription = "notifications",
                          tint = MaterialTheme.colorScheme.primary)
                    }
              }

              // Popular Services Row
              Text(
                  text = "Popular services",
                  style = MaterialTheme.typography.titleMedium,
                  modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp))

              // Adjust the PopularServicesRow to take flexible height and width

              PopularServicesRow(
                  services = services,
                  modifier = Modifier.testTag("PopularServicesRow"),
                  onServiceClick = { /* Handle Service Click */})

              // Spacer with flexible height using weight
              Spacer(modifier = Modifier.weight(0.09f))

              // Upcoming QuickFixes with flexible height
              Box(
                  modifier =
                      Modifier.fillMaxWidth()
                          .weight(1.5f) // Flexible space, larger than the PopularServicesRow
                  ) {
                    QuickFixesWidget(
                        status = "Upcoming",
                        quickFixList = quickFixes,
                        onShowAllClick = { /* Handle Show All Click */},
                        onItemClick = { /* Handle QuickFix Item Click */},
                        modifier = Modifier.testTag("UpcomingQuickFixes"))
                  }
            }
      })
}

@Composable
@Preview
fun PreviewHomeScreen() {
  QuickFixTheme {
    val navController = rememberNavController()
    val navigationActions = remember { NavigationActions(navController) }
    Surface(modifier = Modifier.fillMaxSize(), color = colorScheme.background) {
      HomeScreen(navigationActions)
    }
  }
}

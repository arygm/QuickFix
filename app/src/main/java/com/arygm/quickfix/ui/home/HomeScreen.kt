package com.arygm.quickfix.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.R
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.BottomNavigationMenu
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Route
import com.arygm.quickfix.ui.navigation.Screen
import com.arygm.quickfix.ui.theme.poppinsTypography

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navigationActions: NavigationActions, isUser: Boolean = true) {
  val focusRequesterSearchBar = remember { FocusRequester() }
  val focusManager = LocalFocusManager.current
  // Use Scaffold for the layout structure
  Scaffold(
      modifier =
          Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = { focusManager.clearFocus() }) // Clear focus when tapping outside
              }
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
                  onClick = { navigationActions.navigateTo(Screen.MESSAGES) },
                  Modifier.testTag("MessagesButton")) {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = "Messages",
                        tint = colorScheme.background)
                  }
            })
      },
      bottomBar = {
        // Boolean isUser = true for this HomeScreen
        BottomNavigationMenu(
            selectedItem = Route.HOME, // Start with the "Home" route
            onTabSelect = { selectedDestination ->
              // Use this block to navigate based on the selected tab
              navigationActions.navigateTo(selectedDestination)
            },
            isUser = isUser, // Assuming the user is of type User
        )
      },
      content = { padding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(padding)
                    .padding(vertical = 8.dp)
                    .testTag(
                        "homeContent"), // This should match the exact value you're asserting in the
            // test,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {
              Row(
                  modifier = Modifier.fillMaxWidth(),
              ) {
                Spacer(modifier = Modifier.width(10.dp))
                QuickFixTextFieldCustom(
                    showLeadingIcon = { true },
                    showTrailingIcon = { true },
                    leadingIcon = Icons.Outlined.Search,
                    trailingIcon = Icons.Default.Clear,
                    descriptionLeadIcon = "Search",
                    descriptionTrailIcon = "Clear",
                    placeHolderText = "Find your perfect fix with QuickFix",
                    shape = CircleShape,
                    textStyle = poppinsTypography.labelSmall,
                    textColor = colorScheme.onBackground,
                    placeHolderColor = colorScheme.onBackground,
                    leadIconColor = colorScheme.onBackground,
                    trailIconColor = colorScheme.onBackground,
                    widthField = 330.dp,
                    heightField = 40.dp,
                    moveContentHorizontal = 5.dp,
                    moveContentBottom = 0.dp,
                    moveContentTop = 0.dp,
                    sizeIconGroup = 30.dp,
                    spaceBetweenLeadIconText = 0.dp,
                    onTextFieldClick = {},
                    focusRequester = focusRequesterSearchBar)

                Spacer(modifier = Modifier.width(20.dp))
                IconButton(
                    onClick = {},
                    modifier =
                        Modifier.size(40.dp) // Define the size of the button
                            .clip(CircleShape)
                            .background(color = colorScheme.surface) // Add a border
                            .padding(8.dp)
                            .testTag(C.Tag.notification)
                    // Make it circular
                    ) {
                      Icon(
                          painter = painterResource(id = R.drawable.bell),
                          contentDescription = "notifications",
                          tint = colorScheme.primary, // Set the icon color
                          modifier = Modifier.size(20.dp))
                    }
              }
            }
      })
}

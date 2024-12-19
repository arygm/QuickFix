package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModelUserProfile
import com.arygm.quickfix.model.switchModes.AppMode
import com.arygm.quickfix.model.switchModes.ModeViewModel
import com.arygm.quickfix.ui.elements.QuickFixProfileScreenElement
import com.arygm.quickfix.ui.elements.SettingItemData
import com.arygm.quickfix.ui.elements.SettingsSection
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen

@Composable
fun UserProfileScreen(
    userNavigationActions: NavigationActions,
    navigationActions: NavigationActions,
    rootMainNavigationActions: NavigationActions,
    preferencesViewModel: PreferencesViewModel,
    userPreferencesViewModel: PreferencesViewModelUserProfile,
    appContentNavigationActions: NavigationActions,
    modeViewModel: ModeViewModel
) {
  val isWorker by preferencesViewModel.isWorkerFlow.collectAsState(initial = false)

  // Define Personal Settings
  val personalSettings =
      listOf(
          SettingItemData(
              icon = Icons.Outlined.Person,
              label = "My Account",
              testTag = "AccountConfigurationOption",
              action = { navigationActions.navigateTo(UserScreen.ACCOUNT_CONFIGURATION) }),
          SettingItemData(
              icon = Icons.Outlined.Settings,
              label = "Preferences",
              testTag = "Preferences",
              action = { /* Action */}),
          SettingItemData(
              icon = Icons.Outlined.FavoriteBorder,
              label = "Saved Lists",
              testTag = "SavedLists",
              action = {}))

  // Define Resources Section
  val resources =
      listOf(
          SettingItemData(
              icon = Icons.AutoMirrored.Outlined.HelpOutline,
              label = "Support",
              testTag = "Support",
              action = { /* Action */}),
          SettingItemData(
              icon = Icons.Outlined.Info,
              label = "Legal",
              testTag = "Legal",
              action = { /* Action */}))

  // Conditional "Become a Worker" Section
  val conditionalWorkerSection =
      SettingItemData(
          icon = Icons.Outlined.WorkOutline,
          label = "Become a Worker",
          testTag = "SetupYourBusinessAccountOption",
          action = { navigationActions.navigateTo(UserScreen.TO_WORKER) })

  // Pass sections as lambdas to `QuickFixProfileScreenElement`
  QuickFixProfileScreenElement(
      modeNavigationActions = userNavigationActions,
      navigationActions = navigationActions,
      rootMainNavigationActions = rootMainNavigationActions,
      preferencesViewModel = preferencesViewModel,
      userPreferencesViewModel = userPreferencesViewModel,
      appContentNavigationActions = appContentNavigationActions,
      modeViewModel = modeViewModel,
      initialState = false,
      switchMode = AppMode.WORKER,
      sections =
          listOf(
              { modifier ->
                SettingsSection(
                    title = "Personal Settings",
                    items = personalSettings,
                    screenWidth = 360.dp,
                    cardCornerRadius = 16.dp)
              },
              { modifier ->
                SettingsSection(
                    title = "Resources",
                    items = resources,
                    screenWidth = 360.dp,
                    cardCornerRadius = 16.dp,
                    showConditionalItem = !isWorker,
                    conditionalItem = conditionalWorkerSection)
              }))
}

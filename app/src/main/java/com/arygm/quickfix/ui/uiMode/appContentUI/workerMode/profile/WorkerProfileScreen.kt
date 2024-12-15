package com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
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
import com.arygm.quickfix.ui.uiMode.workerMode.navigation.WorkerScreen

@Composable
fun WorkerProfileScreen(
    workerNavigationActions: NavigationActions,
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
              action = { navigationActions.navigateTo(WorkerScreen.ACCOUNT_CONFIGURATION) }),
          SettingItemData(
              icon = Icons.Outlined.Settings,
              label = "Preferences",
              testTag = "Preferences",
              action = { /* Action */}),
          SettingItemData(
              icon = Icons.Outlined.WorkOutline,
              label = "My Profile",
              testTag = "WorkerProfileConfiguration",
              action = { /* Action */}))

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

  // Pass sections as lambdas to `QuickFixProfileScreenElement`
  BoxWithConstraints {
    val screenWidth = maxWidth
    val screenHeight = maxHeight
    QuickFixProfileScreenElement(
        modeNavigationActions = workerNavigationActions,
        navigationActions = navigationActions,
        rootMainNavigationActions = rootMainNavigationActions,
        preferencesViewModel = preferencesViewModel,
        userPreferencesViewModel = userPreferencesViewModel,
        appContentNavigationActions = appContentNavigationActions,
        modeViewModel = modeViewModel,
        true,
        AppMode.USER,
        sections =
            listOf(
                { modifier ->
                  SettingsSection(
                      title = "Personal Settings",
                      items = personalSettings,
                      screenWidth = screenWidth,
                      cardCornerRadius = 16.dp)
                },
                { modifier ->
                  SettingsSection(
                      title = "Resources",
                      items = resources,
                      screenWidth = screenWidth,
                      cardCornerRadius = 16.dp,
                  )
                }))
  }
}

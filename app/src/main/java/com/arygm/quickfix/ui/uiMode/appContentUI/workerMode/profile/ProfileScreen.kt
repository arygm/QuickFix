package com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.model.switchModes.AppMode
import com.arygm.quickfix.model.switchModes.ModeViewModel
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.appContentUI.navigation.AppContentRoute
import com.arygm.quickfix.utils.setAppMode

@Composable
fun ProfileScreen(
    preferencesViewModel: PreferencesViewModel,
    modeViewModel: ModeViewModel,
    appContentNavigationActions: NavigationActions
) {
  var isChecked by remember { mutableStateOf(false) } // State to track the switch state

  Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Profile Screen", style = poppinsTypography.headlineLarge)
        Switch(
            checked = isChecked,
            modifier = Modifier.testTag(C.Tag.buttonSwitch),
            onCheckedChange = {
              isChecked = it
              setAppMode(preferencesViewModel, AppMode.USER.name)
              modeViewModel.switchMode(AppMode.USER)
              appContentNavigationActions.navigateTo(AppContentRoute.USER_MODE)
            },
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = colorScheme.onPrimary,
                    uncheckedThumbColor = colorScheme.onPrimary,
                    checkedTrackColor = colorScheme.primary,
                    uncheckedTrackColor = colorScheme.tertiaryContainer,
                    uncheckedBorderColor = colorScheme.tertiaryContainer,
                    checkedBorderColor = colorScheme.primary))
      }
}

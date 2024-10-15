package com.arygm.quickfix.ui.profile

import QuickFixTextField
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.model.profile.Profile
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.elements.QuickFixBackButton
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessScreen(
    navigationActions: NavigationActions,
    profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
) {
    val context = LocalContext.current
    val loggedInProfile by profileViewModel.loggedInProfile.collectAsState()

    // Local state variables to hold business details
    var occupation by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var hourlyRate by remember { mutableStateOf("0") }
    var location by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("<Last name and first name>", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navigationActions.goBack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }, containerColor = Color(0xFFFFFFFF),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Occupation Input
                Text(
                    text = "Occupation",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
                QuickFixTextField(
                    value = occupation,
                    onValueChange = { occupation = it },
                    label = "Occupation",
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("occupationField")
                        .shadow(4.dp, RoundedCornerShape(16.dp)), // Apply rounded shadow
                    singleLine = true,
                    color = colorScheme.background
                )

                // Description Input
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
                QuickFixTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .testTag("descriptionField")
                        .shadow(4.dp, RoundedCornerShape(16.dp)), // Apply rounded shadow
                    singleLine = false,
                    color = Color.Transparent
                )

                // Hourly Rate Input with "0 Chf/h"
                Text(
                    text = "Hourly rate",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
                QuickFixTextField(
                    value = "$hourlyRate Chf/h",
                    onValueChange = { newValue ->
                        hourlyRate = newValue.filter { it.isDigit() }
                    },
                    label = "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hourlyRateField")
                        .shadow(4.dp, RoundedCornerShape(16.dp)), // Apply rounded shadow
                    singleLine = true,
                    color = Color.Transparent
                )

                // Location Input
                Text(
                    text = "Location",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
                QuickFixTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = "Location",
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("locationField")
                        .shadow(4.dp, RoundedCornerShape(16.dp)), // Apply rounded shadow
                    singleLine = true,
                    color = Color.Transparent
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Validate Business Account Button
                QuickFixButton(
                    buttonText = "Validate my business account",
                    onClickAction = {
                        if (occupation.isNotBlank() && hourlyRate.isNotBlank() && location.isNotBlank()) {
                            loggedInProfile?.let { profile ->
                                val updatedProfile = profile.copy(
                                    occupation = occupation,
                                    description = description,
                                    hourlyRate = hourlyRate,
                                    location = location
                                )

                                profileViewModel.updateProfile(updatedProfile)
                            } ?: Toast.makeText(context, "Profile not found!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    buttonColor = Color(0xFF66001A),
                    textColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}


// Preview function to visualize the UI
@Preview(showBackground = true)
@Composable
fun PreviewBusinessScreen() {
    // Mocked version for preview
    val navController = rememberNavController()
    val navigationActions = NavigationActions(navController)

    // Using default profileViewModel with no actual data backend for preview
    val sampleProfileViewModel = ProfileViewModel(object : ProfileRepository {
        override fun getNewUid(): String = "12345"
        override fun init(onSuccess: () -> Unit) = onSuccess()
        override fun getProfiles(
            onSuccess: (List<Profile>) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
        }

        override fun addProfile(
            profile: Profile,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
        }

        override fun updateProfile(
            profile: Profile,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) = onSuccess()

        override fun deleteProfileById(
            id: String,
            onSuccess: () -> Unit,
            onFailure: (Exception) -> Unit
        ) {
        }

        override fun profileExists(
            email: String,
            onSuccess: (Pair<Boolean, Profile?>) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
        }

        override fun getProfileById(
            uid: String,
            onSuccess: (Profile?) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
        }
    })

    BusinessScreen(
        navigationActions = navigationActions,
        profileViewModel = sampleProfileViewModel
    )
}
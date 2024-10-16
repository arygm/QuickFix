package com.arygm.quickfix.ui.profile

import QuickFixTextField
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.arygm.quickfix.model.profile.Profile
import com.arygm.quickfix.model.profile.ProfileRepository
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.QuickFixTheme

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
    var typedOccupation by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var hourlyRate by remember { mutableStateOf("0") }
    var location by remember { mutableStateOf("") }

    // Dropdown menu related state for occupation
    var showOccupationDropdown by remember { mutableStateOf(false) }
    val occupations = listOf("Carpenter", "Painter", "Plumber", "Electrician", "Mechanic")
    val filteredOccupations = occupations.filter { it.startsWith(typedOccupation, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "<Last name and first name>",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigationActions.goBack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF2F2F2),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
                    .padding(paddingValues)
            ) {
                @Composable
                fun ShadowBox(content: @Composable () -> Unit) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                4.dp,
                                RoundedCornerShape(16.dp),
                                spotColor = Color.Black,
                                ambientColor = Color(0x40000000)
                            )
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        content()
                    }
                }

                // Occupation Input with Dropdown Menu
                Text(
                    text = "Occupation",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 11.dp)
                )
                Box {
                    ShadowBox {
                        QuickFixTextField(
                            value = occupation,
                            onValueChange = { input ->
                                typedOccupation = input // Update typed text to filter dropdown
                                occupation = input // Continue showing typed text
                                showOccupationDropdown = input.isNotEmpty() // Show dropdown on typing
                            },
                            label = "Select occupation",
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            color = Color.Gray
                        )
                    }
                    DropdownMenu(
                        expanded = showOccupationDropdown && filteredOccupations.isNotEmpty(),
                        onDismissRequest = { showOccupationDropdown = false },
                        properties = PopupProperties(focusable = false), // Allow typing while dropdown is open
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        filteredOccupations.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    occupation = item // Set the selected item in the text field
                                    typedOccupation = "" // Reset typed occupation
                                    showOccupationDropdown = false // Close dropdown
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Description Input
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 11.dp)
                )
                ShadowBox {
                    QuickFixTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Description",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        singleLine = false,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Hourly Rate Input
                Text(
                    text = "Hourly rate",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 11.dp)
                )
                ShadowBox {
                    QuickFixTextField(
                        value = "$hourlyRate Chf/h",
                        onValueChange = { newValue ->
                            hourlyRate = newValue.filter { it.isDigit() }
                        },
                        label = "",
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Location Input
                Text(
                    text = "Location",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 11.dp)
                )
                ShadowBox {
                    QuickFixTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = "Location",
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(70.dp))

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
                            } ?: Toast.makeText(context, "Profile not found!", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    buttonColor = MaterialTheme.colorScheme.primary,
                    textColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewBusinessScreen() {
    val navController = rememberNavController()
    val navigationActions = NavigationActions(navController)

    val sampleProfileViewModel = ProfileViewModel(object : ProfileRepository {
        override fun getNewUid(): String = "12345"
        override fun init(onSuccess: () -> Unit) = onSuccess()
        override fun getProfiles(onSuccess: (List<Profile>) -> Unit, onFailure: (Exception) -> Unit) {}
        override fun addProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {}
        override fun updateProfile(profile: Profile, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) = onSuccess()
        override fun deleteProfileById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {}
        override fun profileExists(email: String, onSuccess: (Pair<Boolean, Profile?>) -> Unit, onFailure: (Exception) -> Unit) {}
        override fun getProfileById(uid: String, onSuccess: (Profile?) -> Unit, onFailure: (Exception) -> Unit) {}
    })

    QuickFixTheme {
        BusinessScreen(
            navigationActions = navigationActions,
            profileViewModel = sampleProfileViewModel
        )
    }
}

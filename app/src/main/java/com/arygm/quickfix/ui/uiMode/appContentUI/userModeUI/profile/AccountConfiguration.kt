package com.arygm.quickfix.ui.profile

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.offline.small.PreferencesViewModel
import com.arygm.quickfix.ui.camera.QuickFixUploadImageSheet
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.utils.isValidDate
import com.arygm.quickfix.utils.isValidEmail
import com.arygm.quickfix.utils.loadBirthDate
import com.arygm.quickfix.utils.loadEmail
import com.arygm.quickfix.utils.loadFirstName
import com.arygm.quickfix.utils.loadLastName
import com.arygm.quickfix.utils.loadProfilePicture
import com.arygm.quickfix.utils.loadUserId
import com.arygm.quickfix.utils.setAccountPreferences
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.Timestamp
import java.util.GregorianCalendar

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AccountConfigurationScreen(
    navigationActions: NavigationActions,
    accountViewModel: AccountViewModel,
    preferencesViewModel: PreferencesViewModel
) {
  var uid by remember { mutableStateOf("Loading...") }

  // State to store saved data
  var savedFirstName by remember { mutableStateOf("Loading...") }
  var savedLastName by remember { mutableStateOf("Loading...") }
  var savedEmail by remember { mutableStateOf("Loading...") }
  var savedBirthDate by remember { mutableStateOf("Loading...") }
  var savedProfilePicture by remember {
    mutableStateOf("https://example.com/default-profile-pic.jpg")
  }
  var imageChanged by remember { mutableStateOf(false) }

  // State for input fields
  var inputFirstName by remember { mutableStateOf("Loading...") }
  var inputLastName by remember { mutableStateOf("Loading...") }
  var inputEmail by remember { mutableStateOf("Loading...") }
  var inputBirthDate by remember { mutableStateOf("Loading...") }
  var inputProfilePicture by remember { mutableStateOf(savedProfilePicture) }

  // State for selected profile image
  var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var showBottomSheetPP by remember { mutableStateOf(false) }
  val sheetState = rememberModalBottomSheetState()

  var isUploading by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    // Load saved data
    uid = loadUserId(preferencesViewModel)
    savedFirstName = loadFirstName(preferencesViewModel)
    savedLastName = loadLastName(preferencesViewModel)
    savedEmail = loadEmail(preferencesViewModel)
    savedBirthDate = loadBirthDate(preferencesViewModel)
    savedProfilePicture = loadProfilePicture(preferencesViewModel)

    // Initialize input fields with saved data
    inputFirstName = savedFirstName
    inputLastName = savedLastName
    inputEmail = savedEmail
    inputBirthDate = savedBirthDate
    inputProfilePicture = savedProfilePicture
  }

  var emailError by remember { mutableStateOf(false) }
  var birthDateError by remember { mutableStateOf(false) }

  val context = LocalContext.current

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val screenWidth = maxWidth
    val screenHeight = maxHeight

    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(horizontal = screenWidth * 0.05f)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top) {
          // Top bar with back button and profile information
          Box(modifier = Modifier.fillMaxWidth().padding(vertical = screenHeight * 0.02f)) {
            // Back button at the top left
            IconButton(
                onClick = { navigationActions.goBack() },
                modifier = Modifier.align(Alignment.TopStart).testTag("goBackButton")) {
                  Icon(
                      imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                      contentDescription = "Back",
                      tint = MaterialTheme.colorScheme.onBackground)
                }

            // Centered profile column with image, name, and email
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.TopCenter).testTag("CenteredProfileColumn")) {
                  // Profile image
                  Box(
                      modifier =
                          Modifier.size(screenWidth * 0.28f)
                              .clip(CircleShape)
                              .background(Color.Gray)
                              .testTag("ProfileImage")
                              .clickable { showBottomSheetPP = true },
                      contentAlignment = Alignment.Center) {
                        profileBitmap?.let {
                          Image(
                              bitmap = it.asImageBitmap(),
                              contentDescription = "Profile Image",
                              modifier = Modifier.fillMaxSize().clip(CircleShape),
                              contentScale = ContentScale.Crop // Fit image inside the circle
                              )
                        }
                            ?: SubcomposeAsyncImage(
                                model = savedProfilePicture,
                                contentDescription = "Profile Image",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop, // Fit image inside the circle
                                error = {
                                  Icon(
                                      Icons.Outlined.CameraAlt,
                                      contentDescription = "Placeholder",
                                      tint = Color.Gray)
                                },
                            )
                      }

                  Spacer(modifier = Modifier.height(screenHeight * 0.01f))

                  // Full name
                  Text(
                      text = capitalizeName(savedFirstName, savedLastName),
                      style =
                          poppinsTypography.headlineSmall.copy(
                              fontWeight = FontWeight.Bold, fontSize = 15.sp),
                      color = MaterialTheme.colorScheme.onBackground,
                      modifier = Modifier.testTag("ProfileDisplayName"),
                      textAlign = TextAlign.Center)

                  Spacer(modifier = Modifier.height(screenHeight * 0.005f))

                  // Email
                  Text(
                      text = savedEmail,
                      style =
                          poppinsTypography.bodySmall.copy(
                              fontSize = 14.sp, fontWeight = FontWeight.Medium),
                      color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                      modifier = Modifier.testTag("ProfileEmail"))
                }
          }

          Spacer(modifier = Modifier.height(screenHeight * 0.03f))

          // Input fields for First Name and Last Name
          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(screenWidth * 0.02f),
              verticalAlignment = Alignment.CenterVertically) {
                QuickFixTextFieldCustom(
                    value = inputFirstName,
                    onValueChange = { inputFirstName = it },
                    placeHolderText = "First Name",
                    placeHolderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    showLabel = true,
                    label = { Text("First Name") },
                    shape = RoundedCornerShape(screenWidth * 0.02f),
                    hasShadow = false,
                    borderColor = MaterialTheme.colorScheme.tertiaryContainer,
                    widthField = screenWidth * 0.44f,
                    heightField = screenHeight * 0.035f,
                    modifier = Modifier.testTag("firstNameInput"))

                QuickFixTextFieldCustom(
                    value = inputLastName,
                    onValueChange = { inputLastName = it },
                    placeHolderText = "Last Name",
                    placeHolderColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    showLabel = true,
                    label = { Text("Last Name") },
                    shape = RoundedCornerShape(screenWidth * 0.02f),
                    hasShadow = false,
                    borderColor = MaterialTheme.colorScheme.tertiaryContainer,
                    widthField = screenWidth * 0.44f,
                    heightField = screenHeight * 0.035f,
                    modifier = Modifier.testTag("lastNameInput"))
              }

          Spacer(modifier = Modifier.height(screenHeight * 0.02f))

          // Email Input
          QuickFixTextFieldCustom(
              value = inputEmail,
              onValueChange = {
                inputEmail = it
                emailError = !isValidEmail(it)
              },
              placeHolderText = "E-mail address",
              placeHolderColor = MaterialTheme.colorScheme.onSecondaryContainer,
              isError = emailError,
              errorText = "Invalid Email",
              showLabel = true,
              label = { Text("E-mail address") },
              shape = RoundedCornerShape(screenWidth * 0.02f),
              hasShadow = false,
              borderColor = MaterialTheme.colorScheme.tertiaryContainer,
              widthField = screenWidth * 0.9f,
              heightField = screenHeight * 0.035f,
              modifier = Modifier.testTag("emailInput"))

          Spacer(modifier = Modifier.height(screenHeight * 0.02f))

          // Birthdate Input
          QuickFixTextFieldCustom(
              value = inputBirthDate,
              onValueChange = {
                inputBirthDate = it
                birthDateError = !isValidDate(it)
              },
              placeHolderText = "Enter your birthdate (DD/MM/YYYY)",
              placeHolderColor = MaterialTheme.colorScheme.onSecondaryContainer,
              isError = birthDateError,
              errorText = "Invalid Date",
              showLabel = true,
              label = { Text("Birthdate") },
              shape = RoundedCornerShape(screenWidth * 0.02f),
              hasShadow = false,
              borderColor = MaterialTheme.colorScheme.tertiaryContainer,
              widthField = screenWidth * 0.9f,
              heightField = screenHeight * 0.035f,
              modifier = Modifier.testTag("birthDateInput"))

          Spacer(modifier = Modifier.height(screenHeight * 0.25f))

          // Save Button
          val isModified =
              inputFirstName != savedFirstName ||
                  inputLastName != savedLastName ||
                  inputEmail != savedEmail ||
                  inputBirthDate != savedBirthDate ||
                  imageChanged

          Button(
              onClick = {
                if (profileBitmap != null) {
                  // Handle image upload logic
                  isUploading = true
                  accountViewModel.uploadAccountImages(
                      accountId = uid,
                      images = listOf(profileBitmap!!),
                      onSuccess = { imageUrls ->
                        val newProfilePicture = imageUrls.first()
                        val updatedAccount =
                            Account(
                                uid = uid,
                                firstName = inputFirstName,
                                lastName = inputLastName,
                                email = inputEmail,
                                birthDate =
                                    Timestamp(
                                        GregorianCalendar(
                                                inputBirthDate.split("/")[2].toInt(),
                                                inputBirthDate.split("/")[1].toInt() - 1,
                                                inputBirthDate.split("/")[0].toInt())
                                            .time),
                                profilePicture = newProfilePicture)
                        accountViewModel.updateAccount(
                            updatedAccount,
                            onSuccess = {
                              setAccountPreferences(preferencesViewModel, updatedAccount)
                              isUploading = false
                              Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                              savedFirstName = inputFirstName
                              savedLastName = inputLastName
                              savedEmail = inputEmail
                              savedBirthDate = inputBirthDate
                              savedProfilePicture = newProfilePicture
                              imageChanged = false
                            },
                            onFailure = {
                              isUploading = false
                              Toast.makeText(context, "Update failed!", Toast.LENGTH_SHORT).show()
                            })
                      },
                      onFailure = {
                        isUploading = false
                        Toast.makeText(context, "Image upload failed!", Toast.LENGTH_SHORT).show()
                      })
                } else {
                  // Update without changing the image
                  val updatedAccount =
                      Account(
                          uid = uid,
                          firstName = inputFirstName,
                          lastName = inputLastName,
                          email = inputEmail,
                          birthDate =
                              Timestamp(
                                  GregorianCalendar(
                                          inputBirthDate.split("/")[2].toInt(),
                                          inputBirthDate.split("/")[1].toInt() - 1,
                                          inputBirthDate.split("/")[0].toInt())
                                      .time),
                          profilePicture = savedProfilePicture)
                  accountViewModel.updateAccount(
                      updatedAccount,
                      onSuccess = {
                        setAccountPreferences(preferencesViewModel, updatedAccount)
                        Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                        savedFirstName = inputFirstName
                        savedLastName = inputLastName
                        savedEmail = inputEmail
                        savedBirthDate = inputBirthDate
                        imageChanged = false
                      },
                      onFailure = {
                        Toast.makeText(context, "Update failed!", Toast.LENGTH_SHORT).show()
                      })
                }
              },
              enabled = isModified && !emailError && !birthDateError,
              colors =
                  ButtonDefaults.buttonColors(
                      containerColor =
                          if (isModified) MaterialTheme.colorScheme.primary
                          else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
              shape = RoundedCornerShape(50),
              modifier =
                  Modifier.width(screenWidth * 0.85f)
                      .height(screenHeight * 0.05f)
                      .testTag("SaveButton")) {
                Text("Save")
              }
        }
  }

  // Image selection interface
  QuickFixUploadImageSheet(
      sheetState = sheetState,
      showModalBottomSheet = showBottomSheetPP,
      onDismissRequest = { showBottomSheetPP = false },
      onShowBottomSheetChange = { showBottomSheetPP = it },
      onActionRequest = { bitmap ->
        inputProfilePicture = "example.com"
        imageChanged = true
        profileBitmap = bitmap
        showBottomSheetPP = false
      })
}

// Helper function to capitalize names
private fun capitalizeName(firstName: String?, lastName: String?): String {
  val capitalizedFirstName = firstName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
  val capitalizedLastName = lastName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
  return "$capitalizedFirstName $capitalizedLastName".trim()
}

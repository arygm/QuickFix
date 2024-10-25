package com.arygm.quickfix.ui.profile

import QuickFixTextField
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag // Import pour les testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.arygm.quickfix.R
import com.arygm.quickfix.model.Location.Location
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.profile.WorkerCategory
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.google.firebase.firestore.GeoPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessScreen(
    navigationActions: NavigationActions,
    accountViewModel: AccountViewModel,
    workerViewModel: ProfileViewModel,
    loggedInAccountViewModel: LoggedInAccountViewModel
) {
  val context = LocalContext.current
  val loggedInAccount by loggedInAccountViewModel.loggedInAccount.collectAsState()
  var errorMessage by remember { mutableStateOf("") }

  // Variables d'état locales pour les détails de l'entreprise
  var occupation by remember { mutableStateOf("") }
  var typedOccupation by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var hourlyRate by remember { mutableStateOf("") }
  var location by remember { mutableStateOf("") }

  // État du menu déroulant pour l'occupation
  var expanded by remember { mutableStateOf(false) }
  val occupations = listOf("Carpenter", "Painter", "Plumber", "Electrician", "Mechanic")
  val filteredOccupations = occupations.filter { it.startsWith(typedOccupation, ignoreCase = true) }

  var textFieldSize by remember { mutableStateOf(Size.Zero) }
  val icon = Icons.Filled.ArrowDropDown

  Scaffold(
      topBar = {
        TopAppBar(
            title = {
              Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Business Account",
                    modifier = Modifier.testTag("BusinessAccountTitle").padding(end = 29.dp),
                    style = poppinsTypography.headlineMedium,
                    color = colorScheme.primary)
              }
            },
            navigationIcon = {
              IconButton(
                  onClick = { navigationActions.goBack() },
                  modifier = Modifier.testTag("goBackButton")) {
                    Icon(
                        Icons.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = colorScheme.primary)
                  }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.background))
      },
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              // Profile Image Placeholder
              Icon(
                  painter = painterResource(R.drawable.profilevector),
                  contentDescription = "Account Circle Icon",
                  tint = colorScheme.surface,
                  modifier =
                      Modifier.size(100.dp)
                          .clip(CircleShape)
                          .border(2.dp, colorScheme.background, CircleShape))

              Spacer(modifier = Modifier.height(16.dp))

              // Profile Card
              Card(
                  modifier =
                      Modifier.fillMaxWidth(0.85f)
                          .align(Alignment.CenterHorizontally)
                          .testTag("ProfileCard"),
                  shape = RoundedCornerShape(16.dp),
                  colors =
                      CardDefaults.cardColors(
                          containerColor = colorScheme.surface,
                          contentColor = colorScheme.onSurface),
                  elevation = CardDefaults.cardElevation(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(7.dp)) {
                          Icon(
                              imageVector = Icons.Default.AccountCircle,
                              contentDescription = "Profile Icon",
                              tint = colorScheme.primary,
                              modifier = Modifier.size(24.dp))
                          Spacer(modifier = Modifier.width(65.dp))

                          val displayName =
                              capitalizeName(loggedInAccount?.firstName, loggedInAccount?.lastName)

                          Text(
                              text = displayName,
                              style = MaterialTheme.typography.bodyLarge,
                              color = colorScheme.onBackground,
                              modifier = Modifier.testTag("ProfileName"))
                        }
                  }
              Spacer(modifier = Modifier.height(18.dp))
              // Champ de saisie pour l'occupation avec menu déroulant
              Column(modifier = Modifier.fillMaxSize().padding(15.dp)) {
                QuickDescription(description = "Occupation")

                Box {

                  // Champ de texte pour l'occupation
                  OutlinedTextField(
                      value = occupation,
                      onValueChange = { input ->
                        typedOccupation = input
                        occupation = input
                        expanded = input.isNotEmpty()
                      },
                      placeholder = { Text("Select occupation") },
                      trailingIcon = {
                        IconButton(
                            onClick = { expanded = !expanded },
                            modifier =
                                Modifier.testTag("occupationDropdownIcon") // Ajout du testTag
                            ) {
                              Icon(imageVector = icon, contentDescription = "Dropdown Icon")
                            }
                      },
                      modifier =
                          Modifier.fillMaxWidth()
                              .testTag("occupationInput") // Ajout du testTag
                              .zIndex(0f)
                              .onGloballyPositioned { coordinates ->
                                textFieldSize = coordinates.size.toSize()
                              }
                              .background(
                                  color = MaterialTheme.colorScheme.surface,
                                  shape =
                                      RoundedCornerShape(
                                          topStart = 12.dp,
                                          topEnd = 12.dp,
                                          bottomStart = if (expanded) 0.dp else 12.dp,
                                          bottomEnd = if (expanded) 0.dp else 12.dp)),
                      singleLine = true,
                      colors =
                          OutlinedTextFieldDefaults.colors(
                              focusedTextColor = MaterialTheme.colorScheme.onBackground,
                              unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                              unfocusedContainerColor = Color.Transparent,
                              focusedContainerColor = Color.Transparent,
                              errorContainerColor = MaterialTheme.colorScheme.errorContainer,
                              unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface,
                              focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface,
                              errorTextColor = MaterialTheme.colorScheme.error,
                              unfocusedBorderColor = Color.Transparent,
                              focusedBorderColor = Color.Transparent,
                              errorBorderColor = MaterialTheme.colorScheme.error),
                      shape =
                          RoundedCornerShape(
                              topStart = 12.dp,
                              topEnd = 12.dp,
                              bottomStart = if (expanded) 0.dp else 12.dp,
                              bottomEnd = if (expanded) 0.dp else 12.dp))

                  if (expanded && filteredOccupations.isNotEmpty()) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        properties = PopupProperties(focusable = false),
                        shadowElevation = 0.dp,
                        shape =
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 0.dp,
                                bottomStart = 12.dp,
                                bottomEnd = 12.dp),
                        modifier =
                            Modifier.width(
                                    with(LocalDensity.current) { textFieldSize.width.toDp() })
                                .offset(y = (-1).dp)
                                .zIndex(1f)
                                .background(MaterialTheme.colorScheme.surface)) {
                          // Contenu du DropdownMenu
                          DropdownMenuContent(
                              items = filteredOccupations,
                              selectedItem = occupation, // Passe l'élément sélectionné actuel
                              onItemClick = { item ->
                                occupation = item
                                typedOccupation = ""
                                expanded = false
                              })
                        }
                  }
                }

                Spacer(modifier = Modifier.height(18.dp))
                QuickDescription(description = "Description")

                QuickFixTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description",
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(150.dp)
                            .testTag("descriptionInput"), // Ajout du testTag
                    singleLine = false,
                    color = MaterialTheme.colorScheme.onBackground)

                Spacer(modifier = Modifier.height(18.dp))

                // Champ de saisie pour le taux horaire

                QuickDescription(description = "Hourly rate")

                QuickFixTextField(
                    value = hourlyRate,
                    onValueChange = { newValue ->
                      hourlyRate = newValue.filter { it.isDigit() || it == '.' || it == ',' }
                    },
                    label = "Hourly Rate",
                    modifier = Modifier.fillMaxWidth().testTag("hourlyRateInput"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    color = MaterialTheme.colorScheme.onBackground,
                    trailingIcon = {
                      Text("CHF/h", color = MaterialTheme.colorScheme.onBackground)
                    })

                Spacer(modifier = Modifier.height(18.dp))

                // Champ de saisie pour la localisation

                QuickDescription(description = "Location")

                QuickFixTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = "Location",
                    modifier = Modifier.fillMaxWidth().testTag("locationInput"), // Ajout du testTag
                    singleLine = true,
                    color = MaterialTheme.colorScheme.onBackground)

                Spacer(modifier = Modifier.height(70.dp))

                // Validation Boutton
                QuickFixButton(
                    buttonText = "Validate my business account",
                    onClickAction = {
                      val hourlyRateValue = hourlyRate.replace(",", ".").toDoubleOrNull() ?: 0.0
                      if (occupation.isNotBlank() &&
                          hourlyRateValue != 0.0 &&
                          location.isNotBlank()) {

                        loggedInAccount?.let { account ->
                          accountViewModel.updateAccount(
                              account =
                                  Account(
                                      uid = account.uid,
                                      firstName = account.firstName,
                                      lastName = account.lastName,
                                      birthDate = account.birthDate,
                                      email = account.email,
                                      isWorker = true),
                              onSuccess = {
                                accountViewModel.fetchUserAccount(account.uid) { account ->
                                  loggedInAccountViewModel.setLoggedInAccount(account!!)
                                }
                              },
                              onFailure = {
                                Toast.makeText(
                                        context, "Failed to update profile", Toast.LENGTH_SHORT)
                                    .show()
                              })
                          workerViewModel.addProfile(
                              profile =
                                  WorkerProfile(
                                      uid = account.uid,
                                      location = Location(0.0,0.0,"default"),
                                      description = description,
                                      fieldOfWork = WorkerCategory.HomeImprovementAndRepair.Handyman,// we will have to rework the UI to let the user choose the category
                                      hourlyRate = hourlyRateValue),
                              onSuccess = {
                                Toast.makeText(
                                        context, "Business account validated!", Toast.LENGTH_SHORT)
                                    .show()
                                navigationActions.goBack()
                              },
                              onFailure = {
                                Toast.makeText(
                                        context,
                                        "Business account creation failed",
                                        Toast.LENGTH_SHORT)
                                    .show()
                              })
                        }
                            ?: Toast.makeText(context, "Profile not found!", Toast.LENGTH_SHORT)
                                .show()
                        errorMessage = ""
                      } else {
                        errorMessage = "Please fill all fields"
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                      }
                    },
                    buttonColor = MaterialTheme.colorScheme.primary,
                    textColor = Color.White,
                    modifier = Modifier.fillMaxWidth().testTag("validateButton"))
                if (errorMessage.isNotEmpty()) {
                  Text(
                      text = errorMessage,
                      color = MaterialTheme.colorScheme.error,
                      style = MaterialTheme.typography.bodyMedium,
                      modifier = Modifier.padding(top = 8.dp).testTag("errorMessage"))
                }
              }
            }
      })
}

@Composable
fun DropdownMenuContent(
    items: List<String>,
    selectedItem: String?, // Paramètre pour l'élément sélectionné
    onItemClick: (String) -> Unit
) {
  items.forEachIndexed { index, item ->
    DropdownMenuItem(
        text = {
          Text(
              item,
              color = MaterialTheme.colorScheme.onSurface,
              style = MaterialTheme.typography.headlineMedium)
        },
        onClick = { onItemClick(item) },
        modifier =
            Modifier.fillMaxWidth()
                .background(
                    if (item == selectedItem) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.surface)
                .testTag(item) // Ajout du testTag pour chaque option
        )

    // Ajouter un séparateur entre les éléments, sauf après le dernier
    if (index < items.size - 1) {
      Divider(
          color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 8.dp))
    }
  }
}

@Composable
fun QuickDescription(description: String) {
  Text(
      text = description,
      style = MaterialTheme.typography.headlineSmall,
      color = MaterialTheme.colorScheme.onBackground,
      modifier = Modifier.padding(start = 11.dp))
}

private fun capitalizeName(firstName: String?, lastName: String?): String {
  val capitalizedFirstName = firstName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
  val capitalizedLastName = lastName?.lowercase()?.replaceFirstChar { it.uppercase() } ?: ""
  return "$capitalizedFirstName $capitalizedLastName".trim()
}

package com.arygm.quickfix.ui.search

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.arygm.quickfix.R
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.account.LoggedInAccountViewModel
import com.arygm.quickfix.model.profile.ProfileViewModel
import com.arygm.quickfix.model.profile.UserProfile
import com.arygm.quickfix.model.search.Announcement
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.camera.QuickFixUploadImageSheet
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementScreen(
    announcementViewModel: AnnouncementViewModel =
        viewModel(factory = AnnouncementViewModel.Factory),
    loggedInAccountViewModel: LoggedInAccountViewModel =
        viewModel(factory = LoggedInAccountViewModel.Factory),
    profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.UserFactory),
    accountViewModel: AccountViewModel = viewModel(factory = AccountViewModel.Factory),
    navigationActions: NavigationActions,
    isUser: Boolean = true
) {

  val loggedInAccount by loggedInAccountViewModel.loggedInAccount.collectAsState()
  val userId =
      loggedInAccount?.uid
          ?: "Should not happen" // If no user is logged, no announcement can be made

  var title by rememberSaveable { mutableStateOf("") }
  var category by rememberSaveable { mutableStateOf("") }
  var location by remember { mutableStateOf("") }
  var description by rememberSaveable { mutableStateOf("") }

  var titleIsEmpty by rememberSaveable { mutableStateOf(true) }
  var categoryIsSelected by rememberSaveable {
    mutableStateOf(true)
  } // TODO: add the different categories
  var locationIsSelected by rememberSaveable {
    mutableStateOf(false)
  } // TODO: add the implemented location
  var descriptionIsEmpty by rememberSaveable { mutableStateOf(true) }
  val uploadedImages by announcementViewModel.uploadedImages.collectAsState()

  // State to control the visibility of the image upload sheet
  var showUploadImageSheet by remember { mutableStateOf(false) }
  val sheetState = rememberModalBottomSheetState()

  BoxWithConstraints {
    val widthRatio = maxWidth / 411
    val heightRatio = maxHeight / 860
    val sizeRatio = minOf(widthRatio, heightRatio)

    // Use Scaffold for the layout structure
    Scaffold(
        containerColor = colorScheme.background,
        topBar = {},
        modifier = Modifier.testTag("AnnouncementContent"),
        content = { padding ->
          Column(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(padding)
                      .padding(top = 30.dp)
                      .align(Alignment.Center)
                      .padding(16.dp)
                      .zIndex(100f)
                      .verticalScroll(rememberScrollState()),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center,
          ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
            ) {
              QuickFixTextFieldCustom(
                  value = title,
                  onValueChange = {
                    title = it
                    titleIsEmpty = title.isEmpty()
                  },
                  placeHolderText = "Enter the title of your quickFix",
                  placeHolderColor = colorScheme.onSecondaryContainer,
                  shape = RoundedCornerShape(12.dp),
                  moveContentHorizontal = 10.dp,
                  heightField = 42.dp,
                  widthField = 360.dp,
                  modifier = Modifier.testTag("titleInput"),
                  showLabel = true,
                  label = {
                    Text(
                        "Title *",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(start = 3.dp).testTag("titleText"))
                  })
            }

            Spacer(modifier = Modifier.padding(6.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
            ) {
              QuickFixTextFieldCustom(
                  value = category,
                  onValueChange = { category = it },
                  placeHolderText = "Enter the category",
                  placeHolderColor = colorScheme.onSecondaryContainer,
                  shape = RoundedCornerShape(12.dp),
                  moveContentHorizontal = 10.dp,
                  heightField = 42.dp,
                  widthField = 360.dp,
                  modifier = Modifier.testTag("categoryInput"),
                  showLabel = true,
                  label = {
                    Text(
                        "Category *",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(start = 3.dp).testTag("categoryText"))
                  })
            }

            Spacer(modifier = Modifier.padding(6.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
            ) {
              QuickFixTextFieldCustom(
                  value = description,
                  onValueChange = {
                    description = it
                    descriptionIsEmpty = description.isEmpty()
                  },
                  placeHolderText = "Describe the quickFix",
                  placeHolderColor = colorScheme.onSecondaryContainer,
                  shape = RoundedCornerShape(12.dp),
                  moveContentHorizontal = 10.dp,
                  heightField = 95.dp,
                  widthField = 360.dp,
                  modifier = Modifier.testTag("descriptionInput"),
                  showLabel = true,
                  label = {
                    Text(
                        "Description *",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(start = 3.dp).testTag("descriptionText"))
                  },
                  singleLine = false)
            }

            Spacer(modifier = Modifier.padding(6.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
            ) {
              QuickFixTextFieldCustom(
                  value = location,
                  onValueChange = { location = it },
                  placeHolderText = "Enter the location",
                  placeHolderColor = colorScheme.onSecondaryContainer,
                  shape = RoundedCornerShape(12.dp),
                  moveContentHorizontal = 10.dp,
                  heightField = 42.dp,
                  widthField = 360.dp,
                  modifier = Modifier.testTag("locationInput"),
                  showLabel = true,
                  label = {
                    Text(
                        "Location *",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(start = 3.dp).testTag("locationText"))
                  })
            }

            Spacer(modifier = Modifier.padding(10.dp))

            QuickFixButtonWithIcon(
                buttonText = "Availability",
                onClickAction = {
                  // TODO: Apply the backend of the availability
                },
                buttonColor = colorScheme.surface,
                textColor = colorScheme.onBackground,
                textStyle = MaterialTheme.typography.titleMedium,
                modifier =
                    Modifier.width(360.dp)
                        .height(42.dp)
                        .testTag("availabilityButton")
                        .graphicsLayer(alpha = 1f),
                iconId = R.drawable.calendar,
                iconContentDescription = "availability",
                iconColor = colorScheme.onBackground)

            Spacer(modifier = Modifier.padding(10.dp))

            if (uploadedImages.isEmpty()) {
              QuickFixButtonWithIcon(
                  buttonText = "Upload pictures",
                  onClickAction = { showUploadImageSheet = true },
                  buttonColor = colorScheme.surface,
                  textColor = colorScheme.onBackground,
                  textStyle = MaterialTheme.typography.titleMedium,
                  modifier =
                      Modifier.width(360.dp)
                          .height(90.dp)
                          .testTag("picturesButton")
                          .graphicsLayer(alpha = 1f),
                  iconId = R.drawable.upload_image,
                  iconContentDescription = "upload_image",
                  iconColor = colorScheme.onBackground)
            } else {
              Box(
                  modifier =
                      Modifier.width(360.dp)
                          .fillMaxWidth(0.8f)
                          .shadow(
                              elevation = 2.dp,
                              shape = RoundedCornerShape(10.dp),
                              clip = false // Ensure the shadow is not clipped
                              )
                          .clip(RoundedCornerShape(10.dp))
                          .height(90.dp) // Same height as the button when no images exist
                          .clip(RoundedCornerShape(8.dp))
                          .background(colorScheme.surface)
                          .clickable { showUploadImageSheet = true }) {
                    Row(
                        modifier =
                            Modifier.fillMaxSize()
                                .padding(start = 15.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start) {
                          // Leading Icon
                          Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                          Icon(
                              painter = painterResource(id = R.drawable.upload_image),
                              contentDescription = "Upload image",
                              modifier = Modifier.size(20.dp),
                              tint = colorScheme.onBackground)

                          Spacer(modifier = Modifier.width(15.dp))

                          // LazyRow for displaying images
                          LazyRow(
                              modifier = Modifier.fillMaxSize(),
                              contentPadding = PaddingValues(horizontal = 8.dp)) {
                                val maxVisibleImages = 3
                                val visibleImages = uploadedImages.take(maxVisibleImages)
                                val remainingImageCount = uploadedImages.size - maxVisibleImages

                                // Display the visible images
                                items(visibleImages.size) { index ->
                                  Card(
                                      modifier =
                                          Modifier.size(90.dp)
                                              .padding(
                                                  end =
                                                      if (index != visibleImages.lastIndex) 8.dp
                                                      else 0.dp),
                                      shape = RoundedCornerShape(8.dp)) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                          // Display the image
                                          Image(
                                              painter =
                                                  rememberAsyncImagePainter(visibleImages[index]),
                                              contentDescription = "Image $index",
                                              modifier = Modifier.fillMaxSize(),
                                              contentScale = ContentScale.Crop)

                                          // Overlay logic for the third image
                                          if (index == 2 && remainingImageCount > 0) {
                                            Box(
                                                modifier =
                                                    Modifier.fillMaxSize()
                                                        .background(Color.Black.copy(alpha = 0.6f))
                                                        .clickable {
                                                          navigationActions.navigateTo(
                                                              Screen.DISPLAY_UPLOADED_IMAGES)
                                                        }, // Set the boolean to true on click
                                                contentAlignment = Alignment.Center) {
                                                  Text(
                                                      text = "+$remainingImageCount",
                                                      color = Color.White,
                                                      style = MaterialTheme.typography.bodyLarge)
                                                }
                                          }

                                          // Delete button
                                          IconButton(
                                              onClick = {
                                                announcementViewModel.deleteUploadedImages(
                                                    listOf(visibleImages[index]))
                                              }, // Provide the delete action
                                              modifier =
                                                  Modifier.align(
                                                          Alignment
                                                              .TopEnd) // Align at the top-right
                                                                       // corner
                                                      .padding(4.dp) // Add some padding for spacing
                                                      .size(24.dp) // Set size for the button
                                              ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Close,
                                                    contentDescription = "Remove Image",
                                                    tint = Color.White,
                                                    modifier =
                                                        Modifier.background(
                                                            color = Color.Black.copy(alpha = 0.6f),
                                                            shape = CircleShape))
                                              }
                                        }
                                      }
                                }
                              }
                        }
                  }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).padding(start = 8.dp),
            ) {
              Text(
                  text = "* Mandatory fields",
                  color =
                      if (titleIsEmpty ||
                          !categoryIsSelected ||
                          !locationIsSelected ||
                          descriptionIsEmpty)
                          colorScheme.error
                      else colorScheme.onSecondaryContainer,
                  style = MaterialTheme.typography.bodySmall,
                  modifier = Modifier.padding(start = 9.dp).testTag("mandatoryText"))
            }

            QuickFixButton(
                buttonText = "Post your announcement",
                onClickAction = {

                  // Make the announcement
                  val announcement =
                      Announcement(
                          announcementId = announcementViewModel.getNewUid(),
                          userId = userId,
                          title = title,
                          category = category, // replace by the category type
                          description = description,
                          location = null,
                          availability = emptyList(),
                          quickFixImages = emptyList())
                  announcementViewModel.announce(announcement)

                  // Update the user profile with the new announcement

                  profileViewModel.fetchUserProfile(userId) { profile ->
                    if (profile is UserProfile) {
                      val announcementList = profile.announcements + announcement.announcementId

                      profileViewModel.updateProfile(
                          UserProfile(profile.locations, announcementList, profile.uid),
                          {
                            accountViewModel.fetchUserAccount(profile.uid) { account ->
                              loggedInAccountViewModel.setLoggedInAccount(account!!)
                            }
                          },
                          {})
                    } else {
                      Log.e("Wrong profile", "Should be a user profile")
                    }
                  }

                  // Reset all parameters after making an announcement
                  title = ""
                  category = ""
                  description = ""
                  location = ""
                  titleIsEmpty = true
                  // categoryIsSelected = false
                  // locationIsSelected = false
                  descriptionIsEmpty = true
                },
                buttonColor = colorScheme.primary,
                textColor = colorScheme.onPrimary,
                textStyle = MaterialTheme.typography.titleMedium,
                modifier =
                    Modifier.width(360.dp)
                        .height(55.dp)
                        .testTag("announcementButton")
                        .graphicsLayer(alpha = 1f),
                enabled =
                    !titleIsEmpty &&
                        categoryIsSelected &&
                        locationIsSelected &&
                        !descriptionIsEmpty)
          }
        })
    // Upload Image Sheet
    QuickFixUploadImageSheet(
        sheetState = sheetState,
        showModalBottomSheet = showUploadImageSheet,
        onDismissRequest = { showUploadImageSheet = false },
        onShowBottomSheetChange = { showUploadImageSheet = it },
        onActionRequest = { value -> announcementViewModel.addUploadedImage(value) })
  }
}

@Composable
fun QuickFixButtonWithIcon(
    buttonText: String,
    onClickAction: () -> Unit,
    buttonColor: Color,
    buttonOpacity: Float = 1f,
    textColor: Color,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconId: Int,
    iconContentDescription: String,
    iconColor: Color
) {
  Button(
      onClick = onClickAction,
      colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
      modifier =
          modifier
              .fillMaxWidth(0.8f)
              .height(50.dp)
              .graphicsLayer(alpha = buttonOpacity)
              .shadow(
                  elevation = 2.dp,
                  shape = RoundedCornerShape(10.dp), // Match the shape with the text field
                  clip = false // Ensure the shadow is not clipped
                  )
              .clip(RoundedCornerShape(10.dp)), // Apply clipping to match the text field
      shape = RoundedCornerShape(12.dp),
      contentPadding = contentPadding,
      enabled = enabled) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start) {
              Icon(
                  painter = painterResource(id = iconId),
                  contentDescription = iconContentDescription,
                  modifier = Modifier.size(20.dp),
                  tint = iconColor)
              Spacer(modifier = Modifier.width(80.dp)) // Consistent spacing with the text field
              Text(text = buttonText, style = textStyle, color = textColor)
            }
      }
}

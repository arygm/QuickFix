package com.arygm.quickfix.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.elements.QuickFixUploadImageSheet
import com.arygm.quickfix.ui.navigation.NavigationActions

@Composable
fun AnnouncementScreen(navigationActions: NavigationActions, isUser: Boolean = true) {

  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("") }
  var location by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }

  var titleIsEmpty by remember { mutableStateOf(true) }
  var categoryIsSelected by remember { mutableStateOf(true) } // TODO: add the different categories
  var locationIsSelected by remember { mutableStateOf(true) } // TODO: add the implemented location
  var descriptionIsEmpty by remember { mutableStateOf(true) }

  // State to control the visibility of the image upload sheet
  var showUploadImageSheet by remember { mutableStateOf(false) }

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

            /*QuickFixButtonWithIcon(
                            buttonText = "Availability",
                            onClickAction = {
                              // TODO: Apply the backend of the pictures
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
            */
            Spacer(modifier = Modifier.padding(10.dp))
            /*
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

            */

            QuickFixButton(
                buttonText = "Post your announcement",
                onClickAction = {
                  // TODO: Apply the backend of the announcement creation
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
        showModalBottomSheet = showUploadImageSheet,
        onDismissRequest = { showUploadImageSheet = false },
        onTakePhotoClick = {
          // TODO: Handle take photo action
          showUploadImageSheet = false
        },
        onChooseFromLibraryClick = {
          // TODO: choose from library action
          showUploadImageSheet = false
        })
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

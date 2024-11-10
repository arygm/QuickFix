package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickFixUploadImageSheet(
    showModalBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    onTakePhotoClick: () -> Unit,
    onChooseFromLibraryClick: () -> Unit
) {
    if (showModalBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            modifier = Modifier.testTag("uploadImageSheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Pictures",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colorScheme.onBackground,
                    modifier = Modifier.testTag("picturesText")
                )
                Divider(
                    color = colorScheme.onSecondaryContainer,
                    thickness = 1.dp,
                    modifier = Modifier.testTag("divider")
                )

                // Option to take a photo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onTakePhotoClick()
                            onDismissRequest()
                        }
                        .padding(vertical = 8.dp)
                        .testTag("cameraRow"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Take a photo",
                        tint = colorScheme.onBackground,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Text(
                        "Take a photo",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colorScheme.onBackground,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .testTag("cameraText")
                    )
                }

                Divider(color = colorScheme.onSecondaryContainer, thickness = 1.dp)

                // Option to choose from library
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onChooseFromLibraryClick()
                            onDismissRequest()
                        }
                        .padding(vertical = 8.dp)
                        .testTag("libraryRow"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.upload_image),
                        contentDescription = "Choose from library",
                        tint = colorScheme.onBackground,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Spacer(modifier = Modifier.width(108.dp))
                    Text(
                        "Choose from library",
                        style = MaterialTheme.typography.headlineSmall,
                        color = colorScheme.onBackground,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .testTag("libraryText")
                    )
                }

                Divider(color = colorScheme.onSecondaryContainer, thickness = 1.dp)
            }
        }
    }
}

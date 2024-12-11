package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.R
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.ui.theme.poppinsTypography

@Composable
fun QuickFixWorkerOverview(
    workerProfile: WorkerProfile,
    modifier: Modifier,
    sizePP: Dp = 32.dp,
    distance: Int? = null
) {
  Box(
      modifier =
          modifier
              .clip(RoundedCornerShape(10.dp))
              .border(
                  width = 1.5.dp,
                  color = colorScheme.tertiaryContainer,
                  shape = RoundedCornerShape(10.dp))
              .testTag("QuickFixWorkerOverview_Box")) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("QuickFixWorkerOverview_Row"),
            verticalAlignment = Alignment.Top,
        ) {
          Column(
              verticalArrangement = Arrangement.Top,
              modifier = Modifier.weight(0.1f).testTag("WorkerProfilePicture_Column")) {
                Image(
                    painter =
                        painterResource(
                            R.drawable
                                .placeholder_worker), // to replace with workerProfile.profileImage
                    contentDescription = null,
                    modifier =
                        Modifier.size(sizePP)
                            .clip(CircleShape)
                            .testTag("WorkerProfilePicture_Image"),
                    contentScale = ContentScale.Crop)
              }

          Column(
              modifier =
                  Modifier.weight(0.4f).padding(start = 8.dp).testTag("WorkerDetails_Column"),
              horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Name and rating",
                    color = colorScheme.onSecondaryContainer,
                    style =
                        poppinsTypography.bodyMedium.copy(
                            fontSize = 9.sp, fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.testTag("WorkerNameRating_Placeholder"))

                Text(
                    text = workerProfile.displayName,
                    color = colorScheme.onBackground,
                    style =
                        poppinsTypography.bodyMedium.copy(
                            fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.testTag("WorkerDisplayName_Text"))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("WorkerRatingReviews_Row")) {
                      Text(
                          text =
                              if (!workerProfile.rating.isNaN()) "${workerProfile.rating} ★"
                              else "No rating",
                          color = colorScheme.onBackground,
                          style =
                              poppinsTypography.bodyMedium.copy(
                                  fontSize = 13.sp, fontWeight = FontWeight.Medium),
                          modifier = Modifier.testTag("WorkerRating_Text"))
                      Text(
                          text = "(${workerProfile.reviews.size}+)",
                          style =
                              poppinsTypography.bodyMedium.copy(
                                  fontSize = 13.sp, fontWeight = FontWeight.Medium),
                          color = colorScheme.onSurface,
                          modifier =
                              Modifier.padding(start = 4.dp).testTag("WorkerReviewsCount_Text"))
                    }
              }

          Column(
              verticalArrangement = Arrangement.spacedBy(0.dp),
              modifier = Modifier.weight(0.4f).testTag("WorkerLocationFieldWork_Column"),
              horizontalAlignment = Alignment.End) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("WorkerLocation_Row")) {
                      Icon(
                          imageVector = Icons.Default.LocationOn,
                          contentDescription = "Location",
                          tint = colorScheme.onSurface,
                          modifier = Modifier.size(10.dp).testTag("WorkerLocation_Icon"))

                      Text(
                          text = workerProfile.location?.name ?: "Unknown Location",
                          color = colorScheme.onSurface,
                          style =
                              poppinsTypography.bodyMedium.copy(
                                  fontSize = 9.sp, fontWeight = FontWeight.Medium),
                          textAlign = TextAlign.End,
                          modifier = Modifier.testTag("WorkerLocation_Text"))
                    }

                Text(
                    text = workerProfile.fieldOfWork,
                    color = colorScheme.onBackground,
                    style =
                        poppinsTypography.bodyMedium.copy(
                            fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                    textAlign = TextAlign.End,
                    modifier = Modifier.testTag("WorkerFieldOfWork_Text"))
              }
        }
      }
}

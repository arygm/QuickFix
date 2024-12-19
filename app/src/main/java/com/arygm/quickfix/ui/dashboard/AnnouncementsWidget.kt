package com.arygm.quickfix.ui.dashboard

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.category.getCategoryIcon
import com.arygm.quickfix.model.search.Announcement
import com.arygm.quickfix.model.search.AnnouncementViewModel
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.navigation.UserScreen

@Composable
fun AnnouncementsWidget(
    announcementViewModel: AnnouncementViewModel,
    categoryViewModel: CategoryViewModel =
        viewModel(factory = CategoryViewModel.Factory(LocalContext.current)),
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier,
    itemsToShowDefault: Int = 2
) {
  var showAll by remember { mutableStateOf(false) }
  val announcements by announcementViewModel.announcementsForUser.collectAsState()
  val imagesForAnnouncements by announcementViewModel.announcementImagesMap.collectAsState()

  BoxWithConstraints {
    val cardWidth = maxWidth * 0.4f // 40% of the available width for each card
    val horizontalSpacing = maxWidth * 0.025f // 2.5% of the available width for spacing

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontalSpacing)
                .shadow(5.dp, RoundedCornerShape(20.dp))
                .background(colorScheme.surface, RoundedCornerShape(12.dp))
                .testTag("AnnouncementsWidget")) {
          // Header with Show All button
          Row(
              modifier = Modifier.fillMaxWidth().padding(8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Announcements",
                    color = colorScheme.onBackground,
                    style = poppinsTypography.headlineMedium,
                    fontSize = 19.sp,
                    modifier = Modifier.testTag("AnnouncementsTitle"))
                TextButton(
                    onClick = { showAll = !showAll },
                    modifier = Modifier.testTag("ShowAllButton")) {
                      Text(
                          text = if (showAll) "Show Less" else "Show All",
                          color = colorScheme.onSurface,
                          style = poppinsTypography.bodyMedium,
                          fontWeight = FontWeight.SemiBold)
                    }
              }

          Divider(
              color = colorScheme.onSurface.copy(alpha = 0.2f),
              thickness = 1.dp,
              modifier = Modifier.testTag("AnnouncementsDivider"))

          val itemsToShow = if (showAll) announcements else announcements.take(itemsToShowDefault)
          itemsToShow.forEachIndexed { index, announcement ->
            val pairs = imagesForAnnouncements[announcement.announcementId] ?: emptyList()
            val bitmapToDisplay = pairs.firstOrNull()?.second
            var category by remember { mutableStateOf(Category()) }
            LaunchedEffect(Unit) {
              categoryViewModel.getCategoryBySubcategoryId(
                  announcement.category,
                  onSuccess = {
                    if (it != null) {
                      category = it
                    }
                  })
            }

            AnnouncementItem(
                announcement = announcement,
                announcementImage = bitmapToDisplay,
                categoryIcon = getCategoryIcon(category),
                onClick = {
                  announcementViewModel.selectAnnouncement(announcement)
                  navigationActions.navigateTo(UserScreen.ANNOUNCEMENT_DETAIL)
                })

            if (index < itemsToShow.size - 1) {
              Divider(
                  color = colorScheme.onSurface.copy(alpha = 0.2f),
                  thickness = 1.dp,
                  modifier = Modifier.testTag("AnnouncementDivider_$index"))
            }
          }
        }
  }
}

@Composable
fun AnnouncementItem(
    announcement: Announcement,
    announcementImage: Bitmap?,
    categoryIcon: ImageVector,
    onClick: () -> Unit
) {

  Row(
      modifier =
          Modifier.fillMaxWidth()
              .padding(8.dp)
              .clickable { onClick() }
              .testTag("AnnouncementItem_${announcement.announcementId}"),
      verticalAlignment = Alignment.Top) {
        // Image
        if (announcementImage != null) {
          Image(
              bitmap = announcementImage.asImageBitmap(),
              contentDescription = "Announcement Image",
              contentScale = ContentScale.Crop,
              modifier =
                  Modifier.size(40.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(colorScheme.onSurface.copy(alpha = 0.1f))
                      .testTag("AnnouncementImage_${announcement.announcementId}"))
        } else {
          Box(
              modifier =
                  Modifier.size(40.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(colorScheme.onSurface.copy(alpha = 0.1f)),
              contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = colorScheme.primary, modifier = Modifier.testTag("Loader"))
              }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Text content
        Column(modifier = Modifier.weight(1f)) {
          // Title and category icon row
          Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier =
                  Modifier.fillMaxWidth()
                      .testTag("AnnouncementRow_${announcement.announcementId}")) {
                Text(
                    text = announcement.title,
                    style = poppinsTypography.bodyMedium,
                    color = colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("AnnouncementTitle_${announcement.announcementId}"))

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = categoryIcon,
                    contentDescription = "Category Icon",
                    tint = colorScheme.primary,
                    modifier = Modifier.testTag("CategoryIcon_${announcement.announcementId}"))

                Spacer(modifier = Modifier.weight(1f))

                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("LocationRow_${announcement.announcementId}")) {
                      Icon(
                          imageVector = Icons.Default.LocationOn,
                          contentDescription = "Location",
                          tint = colorScheme.onSurface,
                          modifier =
                              Modifier.size(16.dp)
                                  .testTag("LocationIcon_${announcement.announcementId}"))
                      Text(
                          text = announcement.location?.name ?: "Unknown",
                          fontSize = 9.sp,
                          color = colorScheme.onSurface,
                          modifier =
                              Modifier.padding(start = 2.dp)
                                  .testTag("LocationText_${announcement.announcementId}"),
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis)
                    }
              }

          Spacer(modifier = Modifier.height(4.dp))

          // Description row
          Text(
              text = announcement.description,
              style = poppinsTypography.bodyMedium.copy(fontSize = 12.sp),
              fontWeight = FontWeight.Normal,
              color = colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.testTag("AnnouncementDescription_${announcement.announcementId}"))
        }
      }
}

package com.arygm.quickfix.ui.search

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.theme.poppinsFontFamily
import com.arygm.quickfix.ui.theme.poppinsTypography

@SuppressLint("DefaultLocale")
@Composable
fun SearchWorkerProfileResult(
    modifier: Modifier = Modifier,
    profileImage: Int,
    name: String,
    category: String,
    rating: Double,
    reviewCount: Int,
    location: String,
    price: String,
    onBookClick: () -> Unit,
    distance: Int? = null
) {
  val displayLocation =
      if (location.length <= 10) {
        location
      } else {
        location.take(10) + "..."
      }
  Card(
      shape = RoundedCornerShape(8.dp),
      modifier = modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 10.dp),
      elevation = CardDefaults.cardElevation(10.dp),
      colors = CardDefaults.cardColors().copy(containerColor = colorScheme.surface)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
              Image(
                  painter = painterResource(id = profileImage),
                  contentDescription = "Profile image of $name, $category",
                  modifier = Modifier.clip(RoundedCornerShape(8.dp)).size(100.dp).aspectRatio(1f),
                  contentScale = ContentScale.FillBounds)

              Spacer(modifier = Modifier.width(8.dp))

              Column(
                  modifier = Modifier.weight(0.6f).height(100.dp),
                  verticalArrangement = Arrangement.SpaceAround,
              ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    val roundedRating = String.format("%.2f", rating).toDouble()
                    Text(
                        text = "$roundedRating ★",
                        fontFamily = poppinsFontFamily,
                        color = colorScheme.onBackground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium)
                    Text(
                        text = "($reviewCount+)",
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = colorScheme.onSurface,
                        modifier = Modifier.padding(start = 4.dp))
                  }

                  Text(
                      text = category,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.SemiBold,
                      fontFamily = poppinsFontFamily,
                      color = colorScheme.onBackground)

                  Text(
                      text = name,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      fontFamily = poppinsFontFamily,
                      color = colorScheme.onSurface)
                }
                Row(verticalAlignment = Alignment.Bottom) {
                  Text(
                      text = "CHF",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.SemiBold,
                      lineHeight = 20.sp,
                      color = colorScheme.onBackground)
                  Text(
                      text = price,
                      fontSize = 19.sp,
                      fontWeight = FontWeight.Bold,
                      lineHeight = 20.sp,
                      color = colorScheme.onBackground,
                      modifier = Modifier.testTag("price"))
                  Text(
                      text = "/Hour",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.SemiBold,
                      lineHeight = 20.sp,
                      color = colorScheme.onSurface)
                }
              }

              Column(
                  horizontalAlignment = Alignment.End,
                  verticalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier.weight(0.4f).height(100.dp).padding(end = 8.dp)) {
                    Column {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = colorScheme.onSurface,
                            modifier = Modifier.size(16.dp))
                        Text(
                            text = displayLocation,
                            fontSize = 9.sp,
                            fontFamily = poppinsFontFamily,
                            color = colorScheme.onSurface)
                      }
                      distance?.let {
                        Text(
                            text = "$distance km away",
                            fontSize = 9.sp,
                            fontFamily = poppinsFontFamily,
                            color = colorScheme.onSurface)
                      }
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                      QuickFixButton(
                          onClickAction = onBookClick,
                          buttonText = "Book",
                          textStyle =
                              poppinsTypography.bodyMedium.copy(
                                  fontWeight = FontWeight.SemiBold,
                                  fontSize = 16.sp,
                                  lineHeight = 20.sp),
                          buttonColor = colorScheme.primary,
                          textColor = colorScheme.onPrimary,
                          contentPadding = PaddingValues(0.dp),
                          height = 30.dp,
                          modifier = Modifier.testTag("book_button"))
                    }
                  }
            }
      }
}

package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.R
import com.arygm.quickfix.ui.theme.QuickFixTheme

// Data class to represent a service
data class Service(val name: String, val imageResId: Int)

// ServiceCard composable with onClick handler and modifier for width control
@Composable
fun ServiceCard(service: Service, modifier: Modifier = Modifier, onClick: () -> Unit) {
  Card(
      modifier =
          modifier
              .aspectRatio(0.9f) // Maintain a consistent aspect ratio for height
              // .fillMaxHeight()
              .shadow(elevation = 5.dp, shape = RoundedCornerShape(8.dp))
              .testTag("ServiceCard_${service.name}"),
      shape = RoundedCornerShape(8.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      onClick = onClick) {
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
              Image(
                  painter = painterResource(id = service.imageResId),
                  contentDescription = service.name,
                  modifier =
                      Modifier.fillMaxWidth()
                          .weight(0.7f) // Allocate 70% of card height to the image
                          .testTag("ServiceImage_${service.name}"),
                  contentScale = androidx.compose.ui.layout.ContentScale.Crop)
              Text(
                  text = service.name,
                  color = MaterialTheme.colorScheme.onBackground,
                  textAlign = TextAlign.Center,
                  style = MaterialTheme.typography.headlineMedium,
                  maxLines = 1,
                  modifier =
                      Modifier.fillMaxWidth()
                          .weight(0.3f) // Allocate 30% of card height to the text
                          .wrapContentHeight(Alignment.CenterVertically)
                          .testTag("ServiceName_${service.name}"))
            }
      }
}

// PopularServicesRow composable with percentage-based card width and consistent padding
@Composable
fun PopularServicesRow(
    services: List<Service>,
    onServiceClick: (Service) -> Unit,
    modifier: Modifier = Modifier
) {
  BoxWithConstraints {
    val cardWidth = maxWidth * 0.4f // 40% of the available width for each card
    val horizontalSpacing = maxWidth * 0.025f // 2.5% of the available width for spacing

    LazyRow(
        modifier = modifier.fillMaxWidth().testTag("PopularServicesRow"),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        contentPadding = PaddingValues(start = horizontalSpacing, end = horizontalSpacing)) {
          items(services.size) { index ->
            ServiceCard(service = services[index], modifier = Modifier.width(cardWidth)) {
              onServiceClick(services[index])
            }
          }
        }
  }
}

// MainContent composable (the screen that contains all sections)
@Composable
fun MainContent() {
  val backgroundColor = MaterialTheme.colorScheme.background

  val services =
      listOf(
          Service("Mechanic", R.drawable.mechanic),
          Service("Gardener", R.drawable.gardener),
          Service("Electrician", R.drawable.electrician),
          Service("Painter", R.drawable.painter),
          Service("Plumber", R.drawable.plumber),
      )

  Column(
      modifier =
          Modifier.fillMaxSize()
              .background(backgroundColor) // Consistent background color
              .testTag("MainContent")) {
        Text(
            text = "Popular services",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium,
            modifier =
                Modifier.padding(start = 15.dp).testTag("PopularServicesTitle").fillMaxWidth())
        PopularServicesRow(
            services = services,
            onServiceClick = { service ->
              // Action when a service is clicked
            },
            modifier = Modifier)
      }
}

// Preview for MainContent to ensure consistent styling
@Preview(showBackground = true)
@Composable
fun PreviewMainContent() {
  QuickFixTheme {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // Consistent background color
        ) {
          MainContent()
        }
  }
}

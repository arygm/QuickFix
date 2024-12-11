package com.arygm.quickfix.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Carpenter
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.ImagesearchRoller
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.NaturePeople
import androidx.compose.material.icons.outlined.Plumbing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsFontFamily
import com.arygm.quickfix.ui.userModeUI.navigation.UserScreen

@Composable
fun ExpandableCategoryItem(
    item: Category,
    isExpanded: Boolean,
    backgroundColor: Color = colorScheme.surface,
    onExpandedChange: (Boolean) -> Unit,
    searchViewModel: SearchViewModel,
    navigationActions: NavigationActions
) {
  val subCategories = remember { item.subcategories }
  val interactionSource = remember { MutableInteractionSource() }
  val rotationAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "")

  Column(
      modifier =
          Modifier.fillMaxWidth()
              .shadow(5.dp, shape = RoundedCornerShape(8.dp), clip = false)
              .background(color = colorScheme.surface, shape = RoundedCornerShape(12.dp))
              .clickable(interactionSource = interactionSource, indication = null) {
                onExpandedChange(!isExpanded)
              }
              .semantics { testTag = C.Tag.expandableCategoryItem }) {
        Row(
            modifier =
                Modifier.padding(start = 12.dp, top = 8.dp, bottom = 8.dp)
                    .background(backgroundColor),
            verticalAlignment = Alignment.CenterVertically) {
              // Icon
              nameToIcon(item.name)?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.testTag("categoryIcon"))
              }

              Spacer(modifier = Modifier.width(10.dp))

              // Text Column
              Column(modifier = Modifier.weight(7f)) {
                Text(
                    text = item.name,
                    color = colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = poppinsFontFamily,
                    fontSize = 16.sp)
                Text(
                    text = item.description,
                    color = colorScheme.onSecondary,
                    fontWeight = FontWeight.Medium,
                    fontFamily = poppinsFontFamily,
                    fontSize = 11.sp,
                    lineHeight = 16.sp)
              }
              Icon(
                  imageVector = Icons.Filled.KeyboardArrowDown,
                  contentDescription = if (isExpanded) "Collapse" else "Expand",
                  modifier = Modifier.graphicsLayer(rotationZ = rotationAngle).weight(1f))
            }
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()) {
              Column(
                  modifier =
                      Modifier.fillMaxWidth().padding(top = 3.dp).semantics {
                        testTag = C.Tag.subCategories
                      }) {
                    subCategories.forEach {
                      Row(
                          modifier = Modifier.padding(horizontal = 10.dp, vertical = 0.dp),
                          verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                modifier =
                                    Modifier.weight(10f)
                                        .semantics {
                                          testTag = "${C.Tag.subCategoryName}_${it.name}"
                                        }
                                        .clickable {
                                          searchViewModel.updateSearchQuery(it.name)
                                          searchViewModel.setSearchSubcategory(it)
                                          searchViewModel.filterWorkersBySubcategory(it.name)
                                          navigationActions.navigateTo(
                                              UserScreen.SEARCH_WORKER_RESULT)
                                        },
                                text = it.name,
                                color = colorScheme.onSecondary,
                                fontWeight = FontWeight.Medium,
                                fontFamily = poppinsFontFamily,
                                fontSize = 11.sp,
                                lineHeight = 16.sp)
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowRight,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                modifier =
                                    Modifier.weight(1f)
                                        .clickable {}
                                        .semantics {
                                          testTag = "${C.Tag.enterSubCateIcon}_${it.name}"
                                        })
                            Spacer(modifier = Modifier.height(10.dp))
                          }
                    }
                  }
            }
      }
}

private fun nameToIcon(displayName: String?): ImageVector? {
  return when (displayName) {
    "Painting" -> Icons.Outlined.ImagesearchRoller
    "Plumbing" -> Icons.Outlined.Plumbing
    "Gardening" -> Icons.Outlined.NaturePeople
    "Electrical Work" -> Icons.Outlined.ElectricalServices
    "Handyman Services" -> Icons.Outlined.Handyman
    "Cleaning Services" -> Icons.Outlined.CleaningServices
    "Carpentry" -> Icons.Outlined.Carpenter
    "Moving Services" -> Icons.Outlined.LocalShipping
    else -> null
  }
}

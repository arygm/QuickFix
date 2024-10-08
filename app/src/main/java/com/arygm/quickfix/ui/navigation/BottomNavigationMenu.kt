package com.arygm.quickfix.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationMenu(
    onTabSelect: (TopLevelDestination) -> Unit,
    isUser: Boolean,
    selectedItem: String,
    LoD: Boolean
) {
  val color1 = if (LoD) Color(0xFFF16138) else Color(0xFF633040)
  val selectedItemColor = if (LoD) Color(0xFF731734) else Color(0xFFB78080)
  val backgroundColor = if (LoD) Color.White else Color(0xFF282828)
  val tabList: List<TopLevelDestination> =
      if (isUser) USER_TOP_LEVEL_DESTINATIONS else WORKER_TOP_LEVEL_DESTINATIONS

  NavigationBar(
      modifier = Modifier.fillMaxWidth().height(73.dp),
      containerColor = color1,
      contentColor = backgroundColor) {
        tabList.forEach { item ->
          val isSelected = selectedItem == item.route
          NavigationBarItem(
              selected = selectedItem == item.route,
              onClick = { onTabSelect(item) },
              icon = {
                if (isSelected) {
                  Box(
                      modifier =
                          Modifier.size(width = 70.dp, height = 100.dp)
                              .offset(y = (-20).dp)
                              .clip(RoundedCornerShape(24.dp))
                              .background(backgroundColor),
                      contentAlignment = Alignment.Center) {
                        // This box ensures the red circle is centered horizontally and offset
                        // vertically
                        Box(
                            modifier =
                                Modifier.size(37.dp)
                                    .offset(
                                        y =
                                            (16)
                                                .dp) // Moves the red circle outside the white
                                                     // ellipse
                                    .clip(CircleShape)
                                    .background(selectedItemColor),
                            contentAlignment = Alignment.Center) {
                              Icon(
                                  imageVector = item.icon,
                                  contentDescription = item.textId,
                                  tint = backgroundColor)
                            }
                      }
                } else {
                  Icon(
                      imageVector = item.icon,
                      contentDescription = item.textId,
                      tint = backgroundColor // Icon color when unselected
                      )
                }
              },
              label = {
                if (selectedItem == item.route) {
                  Text(text = item.textId, modifier = Modifier.offset(y = (-15).dp))
                }
              },
              colors =
                  NavigationBarItemDefaults.colors(
                      selectedIconColor = backgroundColor,
                      unselectedIconColor = backgroundColor,
                      selectedTextColor = backgroundColor,
                      unselectedTextColor = backgroundColor,
                      indicatorColor = color1))
        }
      }
}

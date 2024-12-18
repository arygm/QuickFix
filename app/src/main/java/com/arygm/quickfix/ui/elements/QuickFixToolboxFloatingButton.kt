import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun QuickFixToolboxFloatingButton(
    mainIcon: ImageVector = Icons.Default.Work,
    iconList: List<ImageVector>,
    onIconClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
  var expanded by remember { mutableStateOf(false) }

  BoxWithConstraints {
    val screenHeight = maxHeight.value

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
      AnimatedVisibility(
          visible = expanded,
          enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
          exit = fadeOut() + slideOutVertically(targetOffsetY = { it })) {
            Column(
                verticalArrangement = Arrangement.spacedBy((screenHeight * 0.01).dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                  iconList.forEachIndexed { index, icon ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        modifier =
                            Modifier.size((screenHeight * 0.06).dp)
                                .clickable { onIconClick(index) }
                                .testTag("subIcon$index"),
                        color = colorScheme.surface,
                        shadowElevation = 5.dp) {
                          Icon(
                              imageVector = icon,
                              contentDescription = "Sub Icon $index",
                              modifier = Modifier.padding((screenHeight * 0.012).dp),
                              tint = colorScheme.primary)
                        }
                  }
                }
          }

      Spacer(modifier = Modifier.height((screenHeight * 0.01).dp))

      Surface(
          shape = RoundedCornerShape(20.dp),
          modifier =
              Modifier.size((screenHeight * 0.07).dp)
                  .clickable { expanded = !expanded }
                  .testTag("mainIcon"),
          color = if (!expanded) colorScheme.surface else colorScheme.primary,
          shadowElevation = 5.dp) {
            Icon(
                imageVector = mainIcon,
                contentDescription = "Main Icon",
                modifier = Modifier.padding((screenHeight * 0.01).dp),
                tint = if (!expanded) colorScheme.primary else colorScheme.surface,
            )
          }
    }
  }
}

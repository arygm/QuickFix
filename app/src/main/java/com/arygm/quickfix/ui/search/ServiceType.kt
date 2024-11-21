import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ServiceTypeBottomBar(
    serviceTypes: List<String>,
    selectedService: String?,
    onServiceSelected: (String) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit
) {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .wrapContentHeight()
              .background(Color.White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
              .padding(8.dp)
              .testTag("serviceTypeBottomBar") // Test tag for the entire component
      ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
              // Title
              Text(
                  text = "Service type",
                  fontWeight = FontWeight.Bold,
                  fontSize = 32.sp,
                  color = Color.Black,
                  modifier =
                      Modifier.padding(bottom = 8.dp)
                          .testTag("serviceTypeTitle") // Test tag for title
                  )

              // Divider
              HorizontalDivider(
                  modifier =
                      Modifier.fillMaxWidth().testTag("serviceTypeDivider"), // Test tag for divider
                  thickness = 1.dp,
                  color = Color(0xFFE0E0E0))

              Spacer(modifier = Modifier.height(20.dp))

              // Service Type Options (Wrapped in Grid)
              LazyVerticalGrid(
                  columns = GridCells.Adaptive(100.dp), // Adaptive cells based on available space
                  modifier =
                      Modifier.fillMaxWidth()
                          .wrapContentHeight()
                          .testTag("serviceTypeGrid"), // Test tag for grid
                  horizontalArrangement = Arrangement.spacedBy(8.dp),
                  verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(serviceTypes.size) { index ->
                      val service = serviceTypes[index]
                      Box(
                          modifier =
                              Modifier.background(
                                      if (service == selectedService) Color(0xFF800000)
                                      else Color(0xFFE0E0E0),
                                      shape = RoundedCornerShape(100.dp))
                                  .clickable { onServiceSelected(service) }
                                  .padding(horizontal = 12.dp, vertical = 3.dp)
                                  .testTag(
                                      "serviceTypeItem_$index") // Unique test tag for each item
                          ) {
                            Text(
                                text = service,
                                color =
                                    if (service == selectedService) Color.White else Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1)
                          }
                    }
                  }

              Spacer(modifier = Modifier.height(20.dp))

              Column(
                  verticalArrangement = Arrangement.spacedBy(8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier =
                      Modifier.fillMaxWidth()
                          .testTag("serviceTypeButtonsColumn") // Test tag for the buttons column
                  ) {
                    // Apply Button
                    Button(
                        onClick = onApply,
                        modifier =
                            Modifier.fillMaxWidth()
                                .height(36.dp)
                                .testTag("applyButton"), // Test tag for apply button
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF800000))) {
                          Text(text = "Apply", fontSize = 15.sp, color = Color.White)
                        }

                    // Reset Button
                    Button(
                        onClick = onReset,
                        modifier =
                            Modifier.fillMaxWidth()
                                .height(36.dp)
                                .testTag("resetButton"), // Test tag for reset button
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent, contentColor = Color.Gray),
                        elevation = ButtonDefaults.buttonElevation(0.dp)) {
                          Text(text = "Reset", fontSize = 15.sp, color = Color.Gray)
                        }
                  }
            }
      }
}

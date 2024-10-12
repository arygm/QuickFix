import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuickFixTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    color1: Color = MaterialTheme.colorScheme.primary,
    color2: Color = MaterialTheme.colorScheme.secondary,
    singleLine: Boolean = true,
    errorText: String = "",
    showError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
  OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      label = {
        Text(
            text = label,
            color = color2.copy(alpha = 0.6f),
            style = MaterialTheme.typography.labelLarge)
      },
      isError = isError,
      modifier = modifier,
      textStyle =
          androidx.compose.ui.text.TextStyle(
              color = color2.copy(alpha = 1f), // Full opacity
              fontWeight = FontWeight.ExtraBold,
              fontSize = 20.sp,
              fontStyle = FontStyle.Italic),
      singleLine = singleLine,
      shape = RoundedCornerShape(10.dp),
      colors =
          OutlinedTextFieldDefaults.colors(
              focusedBorderColor = color1, unfocusedBorderColor = color1, cursorColor = color1),
      visualTransformation = visualTransformation,
      keyboardOptions = keyboardOptions)

  if (showError && isError) {
    Text(
        errorText,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(top = 4.dp, start = 3.dp).testTag("errorText"))
  }
}

import android.graphics.drawable.Icon
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuickFixTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    isError: Boolean = false,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    color: Color = MaterialTheme.colorScheme.onSurface,
    singleLine: Boolean = true,
    errorText: String = "",
    showError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    leadingIcon: @Composable (() -> Unit)? = null,
    cornerRadius: Dp = 10.dp
) {
  OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      placeholder = {
          Text(
              text = label,
              color = color.copy(alpha = 0.6f),
              style = MaterialTheme.typography.labelSmall)
        },
      leadingIcon = leadingIcon,
      isError = isError,
      modifier = modifier.testTag("textField"),
      textStyle =
          androidx.compose.ui.text.TextStyle(
              color = color.copy(alpha = 1f), // Full opacity
              fontWeight = FontWeight.Normal,
              fontSize = 16.sp,
              fontStyle = FontStyle.Normal),
      singleLine = singleLine,
      shape = RoundedCornerShape(cornerRadius),
      colors =
          OutlinedTextFieldDefaults.colors(
              unfocusedContainerColor = backgroundColor, focusedContainerColor = backgroundColor, errorContainerColor = MaterialTheme.colorScheme.errorContainer,
              unfocusedPlaceholderColor = backgroundColor, focusedPlaceholderColor = backgroundColor, errorTextColor = MaterialTheme.colorScheme.error,
              unfocusedBorderColor = Color.Transparent, focusedBorderColor = Color.Transparent, errorBorderColor = MaterialTheme.colorScheme.error),
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

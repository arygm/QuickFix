package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.theme.poppinsTypography
import org.w3c.dom.Text

@Composable
fun QuickFixTextFieldCustom(
    modifier: Modifier = Modifier,
    value: String,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit,
    showLeadingIcon: () -> Boolean = { true }, // Boolean flag to show or hide the leading icon
    showTrailingIcon: () -> Boolean = { true }, // Boolean flag to show or hide the trailing icon
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable() (() -> Unit)? = null,
    descriptionLeadIcon: String = "",
    descriptionTrailIcon: String = "",
    placeHolderText: String = "",
    shape: Shape = CircleShape, // Define the shape of the textField
    textStyle: TextStyle = poppinsTypography.labelSmall,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    placeHolderColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    leadIconColor: Color = MaterialTheme.colorScheme.onBackground,
    trailIconColor: Color = MaterialTheme.colorScheme.onBackground,
    widthField: Dp = 330.dp, // Set the width of the container (the rectangle)
    heightField: Dp = 40.dp, // Set the height of the container
    moveContentHorizontal: Dp = 5.dp, // Move the icon and text group horizontally
    moveContentBottom: Dp = 0.dp, // Move the icon and text group down as you increase
    moveContentTop: Dp = 0.dp, // Move the icon and text group to the top as you increase
    sizeIconGroup: Dp =
        30.dp, // Size of the icon (if you want to change the size of the text you have to
    // increase the font)
    spaceBetweenLeadIconText: Dp = 0.dp, // Space between leading icon and text
    onTextFieldClick: () -> Unit = {},
    focusRequester: FocusRequester = FocusRequester(),
    isError: Boolean = false,
    errorText: String = "",
    errorColor: Color = MaterialTheme.colorScheme.error,
    showError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    showLabel: Boolean = false,
    label: @Composable (() -> Unit)? = {},
    onClick: Boolean = false
) {
  var textState by remember { mutableStateOf(TextFieldValue(value)) }
  val scrollState = rememberScrollState() // Scroll state for horizontal scrolling
  // Launch a coroutine to scroll to the end of the text when typing
  LaunchedEffect(textState) { scrollState.animateScrollTo(scrollState.maxValue) }

  if (showLabel) {
    if (label != null) {
      label()
      Spacer(modifier = Modifier.padding(1.5.dp))
    }
  }
  Box(
      modifier =
          Modifier.clip(shape)
              .background(MaterialTheme.colorScheme.surface)
              .let {
                if (isError)
                    it.border(1.dp, errorColor, shape).background(errorColor.copy(alpha = 0.2f))
                else it
              }
              .size(width = widthField, height = heightField) // Set the width and height
              .fillMaxWidth() // Fill the width of the container
              .padding(start = moveContentHorizontal, top = moveContentTop, bottom = moveContentTop)
              .clickable { onTextFieldClick() }
              .testTag(C.Tag.main_container_text_field_custom), // Apply padding
      contentAlignment = Alignment.Center) {
        Row(
            horizontalArrangement = Arrangement.Center, // Aligning icon and text horizontally
            verticalAlignment = Alignment.CenterVertically, // Aligning icon and text vertically
            modifier = modifier.fillMaxWidth()) {
              if (showLeadingIcon()) { // Conditionally show the leading icon
                if (leadingIcon != null) {
                  Icon(
                      imageVector = leadingIcon,
                      contentDescription = descriptionLeadIcon,
                      tint = leadIconColor, // Icon color
                      modifier =
                          Modifier.size(sizeIconGroup) // Set icon size
                              .padding(end = 8.dp)
                              .testTag(C.Tag.icon_custom_text_field) // Space between icon and text
                      )
                }
              }
              Spacer(
                  modifier.padding(
                      horizontal = spaceBetweenLeadIconText)) // Space between icon and text
              Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = textState,
                    onValueChange = { newText ->
                      textState = newText
                      onValueChange(newText.text)
                    },
                    modifier =
                        modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState) // Enable horizontal scrolling
                            .focusable(true)
                            .focusRequester(focusRequester)
                            .testTag(
                                C.Tag.text_field_custom) // Makes the text field take up remaining
                    // space
                    ,
                    textStyle =
                        textStyle.copy(
                            color =
                                if (isError) errorColor else textColor), // Text style with color
                    singleLine = singleLine, // Keep the text on a single line
                    keyboardOptions = keyboardOptions,
                    visualTransformation = visualTransformation,
                )
                if (textState.text.isEmpty()) {
                  Text(
                      modifier = Modifier.testTag(C.Tag.place_holder_text_field_custom),
                      text = placeHolderText,
                      style =
                          textStyle.copy(
                              color =
                                  if (isError) errorColor
                                  else placeHolderColor) // Placeholder text style
                      )
                }
              }
              if (showTrailingIcon() && textState.text.isNotEmpty()) {
                IconButton(
                    onClick = { if (onClick) textState = TextFieldValue("") },
                    modifier =
                        Modifier.testTag(C.Tag.clear_button_text_field_custom)
                            .size(sizeIconGroup)
                            .padding(end = 9.dp)
                            .align(Alignment.CenterVertically)) {
                      trailingIcon?.invoke()
                    }
              }
            }
      }
  if (showError && isError) {
    Text(
        errorText,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(top = 4.dp, start = 3.dp).testTag("errorText"))
  }
}

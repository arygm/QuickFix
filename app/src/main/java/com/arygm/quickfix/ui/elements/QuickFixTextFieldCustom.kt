package com.arygm.quickfix.ui.elements

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.theme.poppinsTypography
import net.bytebuddy.asm.Advice.AssignReturned.ExceptionHandler.Factory.Enabled

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
    onClick: Boolean = false,
    borderThickness: Dp = 1.dp,
    hasShadow: Boolean = true,
    borderColor: Color = Color.Transparent,
    showCharCounter: Boolean = false,
    maxChar: Int = Int.MAX_VALUE,
    charCounterTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
    charCounterColor: Color = MaterialTheme.colorScheme.onBackground,
    charCounterErrorColor: Color = errorColor,
    moveCounter: Dp = 0.dp,
    debug: String = "",
    isTextField: Boolean = true,
    columnModifier: Modifier = Modifier,
    alwaysShowTrailingIcon : Boolean = false,
    moveTrailingIconLeft: Dp = 9.dp,
    enabled: Boolean = true,
    minHeight: Dp = 40.dp,
    maxHeight: Dp = Dp.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    heightInEnabled : Boolean = false
) {
  val scrollState = rememberScrollState() // Scroll state for horizontal scrolling
  // Launch a coroutine to scroll to the end of the text when typing
  LaunchedEffect(TextFieldValue(value)) { scrollState.animateScrollTo(scrollState.maxValue) }

  val charCount = value.length

  // **Enforce maximum character limit**
  val updatedValue = if (charCount <= maxChar) value else value.substring(0, maxChar)

  // **Update the onValueChange to enforce character limit**
  val onValueChangeWithLimit: (String) -> Unit = {
    if (it.length <= maxChar) {
      onValueChange(it)
    }
  }
  Column (
        modifier = columnModifier
  ){
    if (showLabel || showCharCounter) {
      Row(modifier = Modifier.padding(end = moveCounter)) {
        if (showLabel) {
          if (label != null) {
            label()
          }
        }
        if (showCharCounter) {
          val counterColor = if (charCount > maxChar) charCounterErrorColor else charCounterColor
          Text(
              text = "$charCount / $maxChar",
              color = counterColor,
              style = charCounterTextStyle,
              modifier = Modifier.fillMaxWidth().padding(end = 3.dp).testTag("charCounterText"),
              textAlign = TextAlign.End)
        }
      }
      Spacer(modifier = Modifier.padding(1.5.dp))
    }
    Box(
        modifier =
            Modifier.let {
                  if (isError)
                      it.clip(shape)
                          .background(MaterialTheme.colorScheme.surface)
                          .border(borderThickness, errorColor, shape)
                          .background(errorColor.copy(alpha = 0.2f))
                  else {
                    if (hasShadow) {
                      Log.d("QuickFixTextFieldCustom", "debug: $debug")
                      it.shadow(elevation = 2.dp, shape = shape, clip = false)
                          .clip(shape)
                          .background(MaterialTheme.colorScheme.surface)
                          .border(borderThickness, borderColor, shape)
                    } else {
                      it.clip(shape)
                          .background(MaterialTheme.colorScheme.surface)
                          .border(borderThickness, borderColor, shape)
                    }
                  }
                }
                .width(widthField).then(
                    if (heightInEnabled) {
                        Modifier
                            .defaultMinSize(minHeight = minHeight)
                            .heightIn(max = maxHeight)
                    } else {
                        Modifier.height(heightField)
                    }
                )
                .fillMaxWidth() // Fill the width of the container
                .padding(
                    start = moveContentHorizontal, top = moveContentBottom, bottom = moveContentTop)
                .clickable(enabled = !isTextField && enabled) { onTextFieldClick() }
                .testTag(C.Tag.main_container_text_field_custom), // Apply padding
        contentAlignment = Alignment.Center) {
          Row(
              horizontalArrangement = Arrangement.Center, // Aligning icon and text horizontally
              verticalAlignment = Alignment.CenterVertically, // Aligning icon and text vertically
              modifier = Modifier.fillMaxWidth()) {
                if (showLeadingIcon()) { // Conditionally show the leading icon
                  if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = descriptionLeadIcon,
                        tint = leadIconColor, // Icon color
                        modifier =
                            Modifier.size(sizeIconGroup) // Set icon size
                                .padding(end = 8.dp)
                                .testTag(
                                    C.Tag.icon_custom_text_field) // Space between icon and text
                        )
                  }
                }
                Spacer(
                    modifier.padding(
                        horizontal = spaceBetweenLeadIconText)) // Space between icon and text
                Box(modifier = Modifier.weight(1f)) {
                  BasicTextField(
                      value = updatedValue,
                      onValueChange = onValueChangeWithLimit,
                      modifier =
                          modifier
                              .fillMaxWidth()
                              .focusRequester(focusRequester)
                              .testTag(C.Tag.text_field_custom)
                              .focusable(isTextField && enabled)
                              .let {
                                if (singleLine) {
                                  it.horizontalScroll(scrollState)
                                } else it // Enable horizontal scrolling
                              },
                      textStyle =
                          textStyle.copy(
                              color =
                                  if (isError) errorColor else textColor), // Text style with color
                      singleLine = singleLine, // Keep the text on a single line
                      keyboardOptions = keyboardOptions,
                      visualTransformation = visualTransformation,
                      enabled = isTextField && enabled,
                      maxLines = maxLines
                  )
                  if (value.isEmpty()) {
                    Log.d("QuickFixTextFieldCustom", "placeHolderText: $placeHolderText")
                    Text(
                        modifier = Modifier.testTag(C.Tag.place_holder_text_field_custom),
                        text = placeHolderText,
                        style =
                            textStyle.copy(
                                color =
                                    if (isError) errorColor
                                    else placeHolderColor), // Placeholder text style
                        textAlign = TextAlign.Start // Align the placeholder text to the start
                        )
                  }
                }
                if ((showTrailingIcon() && value.isNotEmpty()) || alwaysShowTrailingIcon) {
                  IconButton(
                      onClick = { if (onClick) onValueChange("") },
                      modifier =
                          Modifier.testTag(C.Tag.clear_button_text_field_custom)
                              .size(sizeIconGroup)
                              .padding(end = moveTrailingIconLeft)
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
}

package com.arygm.quickfix.ui.elements

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.arygm.quickfix.ui.theme.poppinsTypography

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

@Preview
@Composable
fun QuickFixSearchBar(
    showLeadingIcon: () -> Boolean = { true }, // Boolean flag to show or hide the leading icon
    showTrailingIcon: () -> Boolean = { true }, // Boolean flag to show or hide the trailing icon
    leadingIcon: ImageVector = Icons.Default.Search,
    trailingIcon: ImageVector = Icons.Default.Clear,
    descriptionLeadIcon: String = "Search",
    descriptionTrailIcon: String = "Clear",
    placeHolderText: String = "Find your perfect fix with QuickFix",
    shape: Shape = CircleShape, // Define the shape of the textField
    textStyle: TextStyle = poppinsTypography.labelSmall,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    placeHolderColor: Color = MaterialTheme.colorScheme.onBackground,
    leadIconColor: Color = MaterialTheme.colorScheme.onBackground,
    trailIconColor: Color = MaterialTheme.colorScheme.onBackground,
    widthField: Dp = 330.dp, // Set the width of the container (the rectangle)
    heightField: Dp = 40.dp, // Set the height of the container
    moveContentHorizontal: Dp = 5.dp, // Move the icon and text group horizontally
    moveContentBottom: Dp = 0.dp, // Move the icon and text group down as you increase
    moveContentTop: Dp = 0.dp, // Move the icon and text group to the top as you increase
    sizeIconGroup: Dp = 30.dp, // Size of the icon (if you want to change the size of the text you have to increase the font)
    spaceBetweenLeadIconText: Dp = 0.dp, // Space between leading icon and text
    onTextFieldClick: () -> Unit = { },
    focusRequester: FocusRequester = FocusRequester()
) {
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    val scrollState = rememberScrollState() // Scroll state for horizontal scrolling

    // Launch a coroutine to scroll to the end of the text when typing
    LaunchedEffect(textState) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Box(
        modifier = Modifier
            .size(width = widthField, height = heightField) // Set the width and height
            .fillMaxWidth() // Fill the width of the container
            .clip(shape) // Clip with shape
            .background(MaterialTheme.colorScheme.surface) // Apply background
            .padding(start = moveContentHorizontal, top = moveContentTop, bottom = moveContentTop)
            .clickable { onTextFieldClick() } ,// Apply padding
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center, // Aligning icon and text horizontally
            verticalAlignment = Alignment.CenterVertically, // Aligning icon and text vertically
            modifier = Modifier.fillMaxWidth()
        ) {
            if (showLeadingIcon()) { // Conditionally show the leading icon
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = descriptionLeadIcon,
                    tint = leadIconColor, // Icon color
                    modifier = Modifier
                        .size(sizeIconGroup) // Set icon size
                        .padding(end = 8.dp) // Space between icon and text
                )
            }

            Spacer(Modifier.padding(horizontal = spaceBetweenLeadIconText)) // Space between icon and text

            Box(
                modifier = Modifier.weight(1f)

            ) {
                BasicTextField(
                    value = textState,
                    onValueChange = { newText ->
                        textState = newText
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                        .focusable(true)
                        .focusRequester(focusRequester)// Makes the text field take up remaining space

                        , // Enable horizontal scrolling
                    textStyle = textStyle.copy(color = textColor), // Text style with color
                    singleLine = true // Keep the text on a single line
                )

                if (textState.text.isEmpty()) {
                    Text(
                        text = placeHolderText,
                        style = textStyle.copy(color = placeHolderColor) // Placeholder text style
                    )
                }
            }
            if (showTrailingIcon() && textState.text.isNotEmpty() ) { // Conditionally show the trailing icon
                IconButton(
                    onClick = { textState = TextFieldValue("") }, // Clear the text when clicked
                    modifier = Modifier
                        .size(sizeIconGroup) // Set icon size
                        .padding(end = 9.dp) // Space between icon and text
                        .align(Alignment.CenterVertically) // Align the icon vertically
                ) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = descriptionTrailIcon,
                        tint = trailIconColor, // Icon color
                    )
                }
            }
        }
    }
}
package com.arygm.quickfix.ui.profile.becomeWorker.views.professional

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.theme.poppinsTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalInfoScreen(
    pagerState: PagerState,
    price: MutableState<String>,
    fieldOfWork: MutableState<String>,
    includedServices: MutableState<List<IncludedService>>,
    addOnServices: MutableState<List<AddOnService>>,
    tags: MutableState<List<String>>
) {
    val categoryViewModel: CategoryViewModel = viewModel(factory = CategoryViewModel.Factory)
    val categories = categoryViewModel.categories.collectAsState().value
    val subcategories = categoryViewModel.subcategories.collectAsState().value
    Log.d("ProfessionalInfoScreen", "Categories: $categories")
    Log.d("ProfessionalInfoScreen", "Subcategories: $subcategories")
    var selectedCategory by remember { mutableStateOf("") }
    var selectedSubcategory by remember { mutableStateOf("") }
    var expandedDropDownCategory by remember { mutableStateOf(false) }
    var expandedDropDownSubcategory by remember { mutableStateOf(false) }
    BoxWithConstraints {
        val widthRatio = maxWidth / 411
        val heightRatio = maxHeight / 860
        val sizeRatio = minOf(widthRatio, heightRatio)
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            item {
                Text(
                    "Professional Info",
                    style =
                    poppinsTypography.headlineMedium.copy(
                        fontSize = 24.sp, fontWeight = FontWeight.SemiBold
                    ),
                    color = colorScheme.onBackground,
                    modifier = Modifier.semantics {
                        testTag = C.Tag.personalInfoScreenSectionTitle
                    })
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "This is your time to shine. Let potential buyers know what you do best and how you gained your skills, certifications and experience.",
                        style =
                        poppinsTypography.headlineMedium.copy(
                            fontSize = 9.sp, fontWeight = FontWeight.Medium
                        ),
                        color = colorScheme.onSurface,
                        modifier =
                        Modifier.weight(0.8f).semantics {
                            testTag = C.Tag.personalInfoScreenSectionDescription
                        })
                    Spacer(modifier = Modifier.weight(0.2f))
                }
            }

            item {
                Row(

                ) {
                    val categoryTextStyle = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 10.sp,
                        color = colorScheme.onBackground,
                        fontWeight = FontWeight.Medium
                    )

                    // Calculate maximum text width for categories
                    val maxCategoryTextWidth = calculateMaxTextWidth(
                        texts = categories.map { it.name },
                        textStyle = categoryTextStyle
                    )

                    // Add padding or extra space if needed
                    val dropdownMenuWidth = maxCategoryTextWidth + 40.dp
                    Box(
                        modifier = Modifier.weight(0.465f).align(Alignment.Bottom),
                    ) {
                        QuickFixTextFieldCustom(
                            modifier = Modifier.semantics {
                                testTag = C.Tag.personalInfoScreendisplayNameField
                            },
                            widthField = 380.dp * widthRatio.value,
                            value = selectedCategory,
                            onValueChange = {
                            },
                            shape = RoundedCornerShape(8.dp),
                            showLabel = true,
                            label =
                            @Composable {
                                Text(
                                    text =
                                    buildAnnotatedString {
                                        append("Your Occupation")
                                        withStyle(
                                            style =
                                            SpanStyle(
                                                color = colorScheme.primary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        ) {
                                            append("*")
                                        }
                                    },
                                    style =
                                    poppinsTypography.headlineMedium.copy(
                                        fontSize = 12.sp, fontWeight = FontWeight.Medium
                                    ),
                                    color = colorScheme.onBackground
                                )
                            },
                            hasShadow = false,
                            borderColor = colorScheme.tertiaryContainer,
                            placeHolderText = "Select Occupation",
                            isTextField = false,
                            alwaysShowTrailingIcon = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "dropdown",
                                    tint = colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(30.dp)
                                )
                            },
                            moveTrailingIconLeft = 2.dp,
                            onTextFieldClick = {
                                expandedDropDownCategory = !expandedDropDownCategory
                            },
                            singleLine = false,
                            heightInEnabled = true,
                            minHeight = 27.dp * heightRatio.value, // Set default height
                            maxHeight = 54.dp * heightRatio.value, // Allow expansion up to double the default height
                            maxLines = 2,
                        )
                        DropdownMenu(
                            expanded = expandedDropDownCategory,
                            onDismissRequest = { expandedDropDownCategory = false },
                            modifier = Modifier.width(dropdownMenuWidth * widthRatio.value),
                            containerColor = colorScheme.surface
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = category.name,
                                            style = categoryTextStyle
                                        )
                                    },
                                    onClick = {
                                        categoryViewModel.getSubcategories(category.id)
                                        selectedCategory = category.name
                                        selectedSubcategory = ""
                                        expandedDropDownCategory = false
                                    }, modifier = Modifier.height(30.dp * heightRatio.value)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(0.07f))
                    val maxSubcategoryTextWidth = calculateMaxTextWidth(
                        texts = subcategories.map { it.name },
                        textStyle = categoryTextStyle
                    )

                    val subDropdownMenuWidth = maxSubcategoryTextWidth + 40.dp
                    val subTextFieldWidth = subDropdownMenuWidth * widthRatio.value
                    Column(
                        modifier = Modifier.weight(0.465f),
                    ) {
                        Spacer(modifier = Modifier.height(40.dp * heightRatio.value))
                        QuickFixTextFieldCustom(
                            modifier = Modifier.semantics {
                                testTag = C.Tag.personalInfoScreendisplayNameField
                            },
                            heightField = 27.dp,
                            widthField = 380.dp * widthRatio.value,
                            value = selectedSubcategory,
                            onValueChange = {},
                            shape = RoundedCornerShape(8.dp),
                            hasShadow = false,
                            borderColor = colorScheme.tertiaryContainer,
                            placeHolderText = "Select Occupation",
                            isTextField = false,
                            alwaysShowTrailingIcon = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "dropdown",
                                    tint = colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(30.dp)
                                )
                            },
                            moveTrailingIconLeft = 2.dp,
                            onTextFieldClick = {
                                expandedDropDownSubcategory = !expandedDropDownSubcategory
                            },
                            enabled = selectedCategory.isNotEmpty(), singleLine = false,
                            heightInEnabled = true,
                            minHeight = 27.dp * heightRatio.value, // Set default height
                            maxHeight = 54.dp * heightRatio.value, // Allow expansion up to double the default height
                            maxLines = 2)
                        DropdownMenu(
                            expanded = expandedDropDownSubcategory,
                            onDismissRequest = { expandedDropDownSubcategory = false },
                            modifier = Modifier.width(subDropdownMenuWidth * widthRatio.value),
                            containerColor = colorScheme.surface
                        ) {
                            subcategories.forEach { subcategory ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = subcategory.name,
                                            style = categoryTextStyle
                                        )
                                    },
                                    onClick = {
                                        selectedSubcategory = subcategory.name
                                        expandedDropDownSubcategory = false
                                    }, modifier = Modifier.height(30.dp * heightRatio.value)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row {
                    Column(
                        modifier = Modifier.weight(1f),
                        content = {

                        }
                    )
                }
            }
        }
    }
}


@Composable
fun calculateMaxTextWidth(
    texts: List<String>,
    textStyle: TextStyle
): Dp {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    var maxWidthPx = 0f

    texts.forEach { text ->
        val textLayoutResult = textMeasurer.measure(
            text = text,
            style = textStyle,
            maxLines = 1
        )
        val width = textLayoutResult.size.width.toFloat()
        if (width > maxWidthPx) {
            maxWidthPx = width
        }
    }

    // Convert pixels to Dp
    return with(density) { maxWidthPx.toDp() }
}
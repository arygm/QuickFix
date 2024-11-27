package com.arygm.quickfix.ui.profile.becomeWorker.views.personal

import android.util.Log
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.material.icons.outlined.CameraAlt
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.arygm.quickfix.model.profile.WorkerProfile
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixTextFieldCustom
import com.arygm.quickfix.ui.theme.poppinsTypography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(pagerState: PagerState,
                       displayName: MutableState<String>,
                       description: MutableState<String>,
                       imagePathPP: MutableState<String>,
                       imagePathBP: MutableState<String>,
                       displayNameError: Boolean,
                       onDisplayNameErrorChange: (Boolean) -> Unit,
                       descriptionError: Boolean,
                       onDescriptionErrorChange: (Boolean) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheetPP by remember { mutableStateOf(false) }
    var showBottomSheetBP by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    BoxWithConstraints {
        val widthRatio = maxWidth / 411
        val heightRatio = maxHeight / 860
        val sizeRatio = minOf(widthRatio, heightRatio)

        Column(
            modifier = Modifier.fillMaxSize().padding(start = 14.dp)
        ) {
            Column(
                modifier = Modifier.weight(0.92f)
            ) {
                Text(
                    "Personal info",
                    style = poppinsTypography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = colorScheme.onBackground
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Tell us a bit about yourself. This information will appear on your public profile, so that potential " +
                                "buyers can get to know you better.",
                        style = poppinsTypography.headlineMedium.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = colorScheme.onSurface, modifier = Modifier.weight(0.8f)
                    )
                    Spacer(modifier = Modifier.weight(0.2f))
                }
                Spacer(modifier = Modifier.height(15.dp))

                QuickFixTextFieldCustom(
                    heightField = 27.dp,
                    widthField = 380.dp * widthRatio.value,
                    value = displayName.value,
                    onValueChange = {
                        displayName.value = it
                        onDisplayNameErrorChange(displayName.value.length < 3)
                                    },
                    shape = RoundedCornerShape(8.dp),
                    showLabel = true,
                    label = @Composable {
                        Text(
                            text = buildAnnotatedString {
                                append("Display name")
                                withStyle(
                                    style = SpanStyle(
                                        color = colorScheme.primary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                ) {
                                    append("*")
                                }
                            },
                            style = poppinsTypography.headlineMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ), color = colorScheme.onBackground
                        )
                    },
                    hasShadow = false,
                    borderColor = colorScheme.tertiaryContainer,
                    placeHolderText = "Ex. Moha A.",
                    isError = displayNameError,
                    errorText = "That’s too short. Your display name must be at least 3 characters.",
                    showError = displayNameError,
                )
                Spacer(modifier = Modifier.height(17.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(0.4f),
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append("Profile Picture")
                                withStyle(
                                    style = SpanStyle(
                                        color = colorScheme.primary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                ) {
                                    append("*")
                                }
                            },
                            style = poppinsTypography.headlineMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ), color = colorScheme.onBackground,
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.padding(1.5.dp))
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .clickable {
                                    showBottomSheetPP = true
                                }
                                .background(colorScheme.background)
                        ) {
                            SubcomposeAsyncImage(
                                model = imagePathPP.value.takeIf { it.isNotEmpty() },
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center,
                                loading = {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.CameraAlt,
                                            contentDescription = "Placeholder",
                                            tint = colorScheme.onBackground,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                },
                                error = {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.CameraAlt,
                                            contentDescription = "Error",
                                            tint = colorScheme.onBackground,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                },
                                success = {
                                    SubcomposeAsyncImageContent(
                                        modifier = Modifier.clip(CircleShape)
                                    )
                                }
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(0.6f).padding(end = 17.dp * widthRatio.value),
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                append("Banner Picture")
                                withStyle(
                                    style = SpanStyle(
                                        color = colorScheme.onSecondaryContainer,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                ) {
                                    append(" (optional)")
                                }
                            },
                            style = poppinsTypography.headlineMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ), color = colorScheme.onBackground,
                            textAlign = TextAlign.Start
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    showBottomSheetBP = true
                                }
                                .background(colorScheme.background)
                        ) {
                            SubcomposeAsyncImage(
                                model = imagePathBP.value.takeIf { it.isNotEmpty() },
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center,
                                loading = {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.CameraAlt,
                                            contentDescription = "Placeholder",
                                            tint = colorScheme.onBackground,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                },
                                error = {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.CameraAlt,
                                            contentDescription = "Error",
                                            tint = colorScheme.onBackground,
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                },
                                success = {
                                    SubcomposeAsyncImageContent(
                                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                                    )
                                }
                            )
                        }
                    }
                }
                if (showBottomSheetPP || showBottomSheetBP) {
                    CameraBottomSheet(
                        sheetState = sheetState,
                        onDismissRequest = {
                            if (showBottomSheetPP) showBottomSheetPP = false
                            else showBottomSheetBP = false
                        },
                        onShowBottomSheetChange = {
                            if (showBottomSheetPP) showBottomSheetPP = it
                            else showBottomSheetBP = it
                        },
                        onActionRequest = { value ->
                            if (showBottomSheetPP) {
                                imagePathPP.value = value
                                Log.d("imagePathPP", imagePathPP.value)
                            } else {
                                imagePathBP.value = value
                                Log.d("imagePathBP", imagePathBP.value)
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(17.dp))
                QuickFixTextFieldCustom(
                    heightField = 150.dp,
                    widthField = 380.dp * widthRatio.value,
                    value = description.value,
                    onValueChange = {
                        description.value = it
                        onDescriptionErrorChange(description.value.length < 150)
                                    },
                    shape = RoundedCornerShape(8.dp),
                    showLabel = true,
                    label = @Composable {
                        Text(
                            text = buildAnnotatedString {
                                append("Description")
                                withStyle(
                                    style = SpanStyle(
                                        color = colorScheme.primary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                ) {
                                    append("*")
                                }
                            },
                            style = poppinsTypography.headlineMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ), color = colorScheme.onBackground
                        )
                    },
                    hasShadow = false,
                    borderColor = colorScheme.tertiaryContainer,
                    placeHolderText = "Type a description...",
                    maxChar = 1500,
                    showCharCounter = true,
                    moveCounter = 17.dp,
                    charCounterTextStyle = poppinsTypography.headlineMedium.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    charCounterColor = colorScheme.onSecondaryContainer,
                    moveContentTop = 125.dp,
                    isError = descriptionError,
                    errorText = "Please enter at least 150 characters",
                    showError = descriptionError
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 5.dp, end = 5.dp, bottom = 8.dp).weight(0.08f),
            ) {
                QuickFixButton(
                    buttonText = "Cancel",
                    onClickAction = {

                    },
                    buttonColor = colorScheme.surface,
                    textColor = colorScheme.error,
                    modifier = Modifier.weight(0.5f),
                    textStyle = poppinsTypography.headlineMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                QuickFixButton(
                    buttonText = "Continue",
                    onClickAction = {
                        coroutineScope.launch { pagerState.scrollToPage(pagerState.currentPage + 1) }
                    },
                    buttonColor = colorScheme.primary,
                    enabled = displayName.value.isNotEmpty() && description.value.isNotEmpty() && imagePathPP.value.isNotEmpty(),
                    textColor = colorScheme.onPrimary,
                    textStyle = poppinsTypography.headlineMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.weight(0.5f)
                )
            }
        }
    }

}



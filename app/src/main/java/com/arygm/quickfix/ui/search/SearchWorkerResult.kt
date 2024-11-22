package com.arygm.quickfix.ui.search

import android.location.Location
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.MainActivity
import com.arygm.quickfix.R
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.search.SearchViewModel
import com.arygm.quickfix.ui.elements.QuickFixButton
import com.arygm.quickfix.ui.elements.QuickFixSlidingWindow
import com.arygm.quickfix.ui.navigation.NavigationActions
import com.arygm.quickfix.ui.theme.poppinsTypography
import com.arygm.quickfix.utils.LocationHelper

data class SearchFilterButtons(
    val onClick: () -> Unit,
    val text: String,
    val leadingIcon: ImageVector? = null,
    val trailingIcon: ImageVector? = null,
)

val listOfButtons =
    listOf(
        SearchFilterButtons(
            onClick = { /* Handle click */ },
            text = "Location",
        ),
        SearchFilterButtons(
            onClick = { /* Handle click */ },
            text = "Service Type",
            trailingIcon = Icons.Default.KeyboardArrowDown,
        ),
        SearchFilterButtons(
            onClick = { /* Handle click */ },
            text = "Availability",
            leadingIcon = Icons.Default.CalendarMonth,
            trailingIcon = Icons.Default.KeyboardArrowDown,
        ),
        SearchFilterButtons(
            onClick = { /* Handle click */ },
            text = "Highest Rating",
            leadingIcon = Icons.Default.WorkspacePremium,
        ),
        SearchFilterButtons(
            onClick = { /* Handle click */ },
            text = "Price Range",
        ),
    )

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchWorkerResult(
    navigationActions: NavigationActions,
    searchViewModel: SearchViewModel,
    accountViewModel: AccountViewModel
) {

    //==========================================================================//
    //============ TODO: REMOVE NO-DATA WHEN BACKEND IS IMPLEMENTED ============//
    //==========================================================================//

    val bannerImage = R.drawable.moroccan_flag
    val profilePicture = R.drawable.placeholder_worker
    val workerName = "Moha Abbes"
    val workerCategory = "Exterior Painter"
    val description =
        "According to all known laws of aviation, there is no way a bee should be able " +
                "to fly. Its wings are too small to get its fat little body off the ground. The bee, of " +
                "course, flies anyway because bees don't care what humans think is impossible. Yellow, " +
                "black. Yellow, black. Yellow, black. Yellow, black. Ooh, black and yellow! Let's shake " +
                "it up a little. Barry! Breakfast is ready! Coming! Hang on a second. Hello? - Barry? - Adam? " +
                "- Can you believe this is happening? - I can't. I'll pick you up. Looking sharp. Use the stairs. " +
                "Your father paid good money for those. Sorry. I'm excited. Here's the graduate. We're " +
                "very proud of you, son. A perfect report card, all B's. Very proud. Ma! I got a thing " +
                "going here. - You got lint on your fuzz. - Ow! That's me! - Wave to us! We'll be in row " +
                "118,000. - Bye! Barry, I told you, stop flying in the house! - Hey, Adam. - Hey, Barry. " +
                "- Is that fuzz gel? - A little. Special day, graduation. Never thought I'd make it. " +
                "Three days grade school, three days high school. Those were awkward. Three days college. " +
                "I'm glad I took a day and hitchhiked around the hive. You did come back different. " +
                "- Hi, Barry. - Artie, growing a mustache? Looks good. - Hear about Frankie? - Yeah. " +
                "- You going to the funeral? - No, I'm not going. Everybody knows, sting someone, you die. " +
                "Don't waste it on a squirrel. Such a hothead. I guess he could have just gotten out of the way. " +
                "I love this incorporating an amusement park into our day. That's why we don't need vacations. " +
                "Boy, quite a bit of pomp... under the circumstances."
    val workerAddress = "Ecublens, VD"
    val workerRating = 3.8
    val includedServices = listOf(
        "Initial Consultation",
        "Basic Surface Preparation",
        "Priming of Surfaces",
        "High-Quality Paint Application",
        "Two Coats of Paint",
        "Professional Cleanup"
    )
    val addonServices =
        listOf(
            "Detailed Color Consultation",
            "Premium paint Upgrade",
            "Extensive Surface Preparation",
            "Extra Coats for added Durability",
            "Power Washing and Deep Cleaning"
        )
    val rate = 40
    val tags =
        listOf(
            "Exterior Painting",
            "Interior Painting",
            "Cabinet Painting",
            "Licensed & Insured",
            "Local Worker"
        )
    val reviews = listOf(
        "Overall, the work was shit; it’s fair to say that since he just painted the whole mural with " +
                "his shit. I don’t know what to do; I’ll probably sue him.",
        "Moha was very punctual and did a great job painting our living room. Highly recommended!",
        "Moha was very professional and did a fantastic job painting our house",
        "I wanna marry that man,"
    )

    //==========================================================================//
    //==========================================================================//
    //==========================================================================//

    var isWindowVisible by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }
    val searchQuery by searchViewModel.searchQuery.collectAsState()
    val workerProfiles by searchViewModel.workerProfiles.collectAsState()
    var currentLocation by remember { mutableStateOf<Location?>(null) }

    val locationHelper: LocationHelper = LocationHelper(LocalContext.current, MainActivity())

    // Wrap everything in a Box to allow overlay
    Box(modifier = Modifier.fillMaxSize()) {
        // Scaffold containing the main UI elements
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Search Results",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigationActions.goBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Handle search */ }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = colorScheme.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = colorScheme.background
                    ),
                )
            }
        ) { paddingValues ->
            // Main content inside the Scaffold
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = searchQuery,
                        style = poppinsTypography.labelMedium,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "This is a sample description for the $searchQuery result",
                        style = poppinsTypography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 10.dp)
                        .padding(horizontal = 10.dp)
                        .wrapContentHeight()
                        .testTag("filter_buttons_row"),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    items(listOfButtons.size) { index ->
                        QuickFixButton(
                            buttonText = listOfButtons[index].text,
                            onClickAction = listOfButtons[index].onClick,
                            buttonColor = colorScheme.surface,
                            textColor = colorScheme.onBackground,
                            textStyle = poppinsTypography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            height = 40.dp,
                            leadingIcon = listOfButtons[index].leadingIcon,
                            trailingIcon = listOfButtons[index].trailingIcon,
                            contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                            modifier = Modifier.testTag("filter_button_${listOfButtons[index].text}")
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }

                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .testTag("worker_profiles_list")) {
                    items(workerProfiles.size) { index ->
                        val profile = workerProfiles[index]
                        var account by remember { mutableStateOf<Account?>(null) }
                        var distance by remember { mutableStateOf<Int?>(null) }

                        locationHelper.getCurrentLocation { location ->
                            currentLocation = location
                            location?.let {
                                distance = profile.location
                                    ?.let { workerLocation ->
                                        searchViewModel.calculateDistance(
                                            workerLocation.latitude,
                                            workerLocation.longitude,
                                            it.latitude,
                                            it.longitude
                                        )
                                    }
                                    ?.toInt()
                            }
                        }

                        LaunchedEffect(profile.uid) {
                            accountViewModel.fetchUserAccount(profile.uid) { fetchedAccount: Account? ->
                                account = fetchedAccount
                            }
                        }

                        account?.let { acc ->
                            val locationName = if (profile.location?.name.isNullOrEmpty()) "Unknown"
                            else profile.location?.name

                            locationName?.let {
                                SearchWorkerProfileResult(
                                    modifier = Modifier.testTag("worker_profile_result$index"),
                                    profileImage = R.drawable.placeholder_worker,
                                    name = "${acc.firstName} ${acc.lastName}",
                                    category = profile.fieldOfWork,
                                    rating = profile.rating,
                                    reviewCount = profile.reviews.size,
                                    location = it,
                                    price = profile.hourlyRate?.toString() ?: "N/A",
                                    onBookClick = { isWindowVisible = true },
                                    distance = distance,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                    }
                }
            }
        }

        if (isWindowVisible) {
            QuickFixSlidingWindow(
                isVisible = isWindowVisible,
                onDismiss = { isWindowVisible = false }
            ) {
                // Content of the sliding window
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                        .fillMaxWidth()
                        .background(colorScheme.background)

                ) {
                    // Top Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp) // Adjusted height to accommodate profile picture overlap
                    ) {
                        // Banner Image
                        Image(
                            painter = painterResource(id = bannerImage),
                            contentDescription = "Banner",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentScale = ContentScale.Crop
                        )

                        QuickFixButton(
                            buttonText = if (saved) "saved" else "save",
                            onClickAction = { saved = !saved },
                            buttonColor = colorScheme.surface,
                            textColor = colorScheme.onBackground,
                            textStyle = MaterialTheme.typography.labelMedium,
                            contentPadding = PaddingValues(horizontal = 4.dp),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .width(100.dp)
                                .offset(x = (-16).dp), // Negative offset to position correctly,
                            leadingIcon = if (saved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
                        )


                        // Profile picture overlapping the banner image
                        Image(
                            painter = painterResource(id = profilePicture),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.BottomStart)
                                .offset(x = 16.dp)
                                .clip(CircleShape),
                            // Negative offset to position correctly
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Worker Field and Address under the profile picture
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = workerCategory,
                            style = MaterialTheme.typography.headlineLarge,
                            color = colorScheme.onBackground
                        )
                        Text(
                            text = workerAddress,
                            style = MaterialTheme.typography.headlineSmall,
                            color = colorScheme.onBackground
                        )
                    }

                    // Main content should be scrollable
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .background(colorScheme.surface)

                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Description with "Show more" functionality
                        var showFullDescription by remember { mutableStateOf(false) }
                        val descriptionText =
                            if (showFullDescription || description.length <= 100) {
                                description
                            } else {
                                description.take(100) + "..."
                            }

                        Text(
                            text = descriptionText,
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        if (description.length > 100) {
                            Text(
                                text = if (showFullDescription) "Show less" else "Show more",
                                style = MaterialTheme.typography.bodySmall.copy(color = colorScheme.primary),
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .clickable { showFullDescription = !showFullDescription }
                            )
                        }

                        // Delimiter between description and services
                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 1.dp,
                            color = colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Services Section
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            // Included Services
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Included Services",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                includedServices.forEach { service ->
                                    Text(
                                        text = "• $service",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Add-On Services
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Add-On Services",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                addonServices.forEach { service ->
                                    Text(
                                        text = "• $service",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = colorScheme.primary,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Continue Button with Rate/HR
                        QuickFixButton(
                            buttonText = "Continue (CHF$rate/Hour)",
                            onClickAction = { /* Handle continue */ },
                            buttonColor = colorScheme.primary,
                            textColor = colorScheme.onPrimary,
                            textStyle = MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 1.dp,
                            color = colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Tags Section
                        Text(
                            text = "Tags",
                            style = MaterialTheme.typography.headlineMedium,
                            color = colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Display tags using FlowRow for wrapping
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                        ) {
                            tags.forEach { tag ->
                                Text(
                                    text = tag,
                                    color = colorScheme.primary,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .border(
                                            width = 1.dp,
                                            color = colorScheme.primary,
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 1.dp,
                            color = colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Reviews",
                            style = MaterialTheme.typography.headlineMedium,
                            color = colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Star Rating Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            val filledStars = workerRating.toInt()
                            val unfilledStars = 5 - filledStars
                            repeat(filledStars) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = colorScheme.onBackground
                                )
                            }
                            repeat(unfilledStars) {
                                Icon(
                                    imageVector = Icons.Outlined.StarOutline,
                                    contentDescription = null,
                                    tint = colorScheme.onBackground,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            itemsIndexed(reviews) { index, review ->
                                var isExpanded by remember { mutableStateOf(false) }
                                val displayText = if (isExpanded || review.length <= 100) {
                                    review
                                } else {
                                    review.take(100) + "..."
                                }

                                Box(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .width(250.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(colorScheme.background)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = displayText,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = colorScheme.onSurface
                                        )
                                        if (review.length > 100) {
                                            Text(
                                                text = if (isExpanded) "See less" else "See more",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = colorScheme.primary
                                                ),
                                                modifier = Modifier
                                                    .clickable { isExpanded = !isExpanded }
                                                    .padding(top = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}


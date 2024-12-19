package com.arygm.quickfix.ui.search

import android.graphics.Bitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.account.Account
import com.arygm.quickfix.model.account.AccountViewModel
import com.arygm.quickfix.model.category.Category
import com.arygm.quickfix.model.category.CategoryViewModel
import com.arygm.quickfix.model.locations.Location
import com.arygm.quickfix.model.search.Announcement
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.arygm.quickfix.ui.uiMode.appContentUI.workerMode.announcements.AnnouncementCard
import com.google.firebase.Timestamp
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnnouncementCardTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var categoryViewModel: CategoryViewModel
  private lateinit var accountViewModel: AccountViewModel

  private val sampleAnnouncement =
      Announcement(
          announcementId = "testAnn",
          userId = "user123",
          title = "Test Announcement",
          category = "testSubCat",
          description = "A test announcement description",
          location = Location(10.0, 10.0, "Test Location"),
          availability = emptyList(),
          quickFixImages = emptyList())
  private val timestamp = Timestamp.now()

  @Before
  fun setup() {
    categoryViewModel = mockk(relaxed = true)
    accountViewModel = mockk(relaxed = true)
  }

  @Test
  fun announcementCard_withImage_andCategory_andAccount() {
    // Mock category found
    every { categoryViewModel.getCategoryBySubcategoryId(eq("testSubCat"), any()) } answers
        {
          val onSuccess = arg<(Category?) -> Unit>(1)
          onSuccess(
              Category(
                  id = "catId",
                  name = "Mock Category",
                  description = "A mock category",
                  subcategories = emptyList()))
        }

    // Mock account found
    every { accountViewModel.fetchUserAccount(eq("user123"), any()) } answers
        {
          val onResult = arg<(Account?) -> Unit>(1)
          onResult(
              Account(
                  uid = "user123",
                  firstName = "John",
                  lastName = "Doe",
                  email = "john@doe.com",
                  birthDate = timestamp))
        }

    val dummyBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)

    var onClickCalled = false

    composeTestRule.setContent {
      QuickFixTheme {
        AnnouncementCard(
            announcement = sampleAnnouncement,
            announcementImage = dummyBitmap,
            accountViewModel = accountViewModel,
            categoryViewModel = categoryViewModel) {
              onClickCalled = true
            }
      }
    }

    composeTestRule.waitForIdle()

    // Verify image displayed
    composeTestRule
        .onNodeWithTag("AnnouncementImage_testAnn", useUnmergedTree = true)
        .assertExists()

    // Verify title
    composeTestRule
        .onNodeWithTag("AnnouncementTitle_testAnn", useUnmergedTree = true)
        .assertTextEquals("Test Announcement")

    // Verify description
    composeTestRule
        .onNodeWithTag("AnnouncementDescription_testAnn", useUnmergedTree = true)
        .assertTextEquals("A test announcement description")

    // Verify location
    composeTestRule
        .onNodeWithTag("AnnouncementLocation_testAnn", useUnmergedTree = true)
        .assertTextEquals("Test Location")

    // Verify user name
    composeTestRule
        .onNodeWithTag("AnnouncementUserName_testAnn", useUnmergedTree = true)
        .assertTextContains("By John D.")

    // Click card
    composeTestRule.onNodeWithTag("AnnouncementCard_testAnn", useUnmergedTree = true).performClick()
    assertTrue(onClickCalled)
  }

  @Test
  fun announcementCard_noImage_noCategory_noAccount_noLocation() {
    // Mock category returns null
    every { categoryViewModel.getCategoryBySubcategoryId(eq("testSubCat"), any()) } answers
        {
          val onSuccess = arg<(Category?) -> Unit>(1)
          onSuccess(null)
        }

    // Mock account returns null
    every { accountViewModel.fetchUserAccount(eq("user123"), any()) } answers
        {
          val onResult = arg<(Account?) -> Unit>(1)
          onResult(null)
        }

    val noLocationAnnouncement = sampleAnnouncement.copy(location = null)

    composeTestRule.setContent {
      QuickFixTheme {
        AnnouncementCard(
            announcement = noLocationAnnouncement,
            announcementImage = null, // no image
            accountViewModel = accountViewModel,
            categoryViewModel = categoryViewModel) {}
      }
    }

    composeTestRule.waitForIdle()

    // Verify loader displayed instead of image
    composeTestRule
        .onNodeWithTag("AnnouncementImagePlaceholder_testAnn", useUnmergedTree = true)
        .assertExists()
    composeTestRule.onNodeWithTag("Loader_testAnn", useUnmergedTree = true).assertExists()

    // Verify title
    composeTestRule
        .onNodeWithTag("AnnouncementTitle_testAnn", useUnmergedTree = true)
        .assertTextEquals("Test Announcement")

    // Description should still be correct
    composeTestRule
        .onNodeWithTag("AnnouncementDescription_testAnn", useUnmergedTree = true)
        .assertTextEquals("A test announcement description")

    // Location now unknown
    composeTestRule
        .onNodeWithTag("AnnouncementLocation_testAnn", useUnmergedTree = true)
        .assertTextEquals("Unknown")

    // By Unknown user
    composeTestRule
        .onNodeWithTag("AnnouncementUserName_testAnn", useUnmergedTree = true)
        .assertTextEquals("By Unknown")

    // Category icon will still appear even with null category, default icon scenario
    composeTestRule
        .onNodeWithTag("AnnouncementCategoryIcon_testAnn", useUnmergedTree = true)
        .assertExists()
  }

  @Test
  fun announcementCard_noAccountInitially_showsUnknown() {
    // Initially return null account
    every { accountViewModel.fetchUserAccount(eq("user123"), any()) } answers
        {
          val onResult = arg<(Account?) -> Unit>(1)
          onResult(null)
        }

    // category found scenario
    every { categoryViewModel.getCategoryBySubcategoryId(eq("testSubCat"), any()) } answers
        {
          val onSuccess = arg<(Category?) -> Unit>(1)
          onSuccess(
              Category(
                  id = "catId",
                  name = "Mock Category",
                  description = "A mock category",
                  subcategories = emptyList()))
        }

    composeTestRule.setContent {
      QuickFixTheme {
        AnnouncementCard(
            announcement = sampleAnnouncement,
            announcementImage = null,
            accountViewModel = accountViewModel,
            categoryViewModel = categoryViewModel) {}
      }
    }

    composeTestRule.waitForIdle()

    // Verify initially "By Unknown"
    composeTestRule
        .onNodeWithTag("AnnouncementUserName_testAnn", useUnmergedTree = true)
        .assertTextEquals("By Unknown")
  }

  @Test
  fun announcementCard_accountFetchedInitially_showsUserName() {
    // Now return an account right away
    val laterAccount =
        Account(
            uid = "user123",
            firstName = "Alice",
            lastName = "Smith",
            email = "a@b.com",
            birthDate = timestamp)

    every { accountViewModel.fetchUserAccount(eq("user123"), any()) } answers
        {
          val onResult = arg<(Account?) -> Unit>(1)
          onResult(laterAccount)
        }

    every { categoryViewModel.getCategoryBySubcategoryId(eq("testSubCat"), any()) } answers
        {
          val onSuccess = arg<(Category?) -> Unit>(1)
          onSuccess(
              Category(
                  id = "catId",
                  name = "Mock Category",
                  description = "A mock category",
                  subcategories = emptyList()))
        }

    composeTestRule.setContent {
      QuickFixTheme {
        AnnouncementCard(
            announcement = sampleAnnouncement,
            announcementImage = null,
            accountViewModel = accountViewModel,
            categoryViewModel = categoryViewModel) {}
      }
    }

    composeTestRule.waitForIdle()

    // Check that user name is shown right away
    composeTestRule
        .onNodeWithTag("AnnouncementUserName_testAnn", useUnmergedTree = true)
        .assertTextContains("By Alice S.")
  }

  @Test
  fun announcementCard_noCategoryInitially_showsDefaultIcon() {
    // Initially return null category
    every { categoryViewModel.getCategoryBySubcategoryId(eq("testSubCat"), any()) } answers
        {
          val onSuccess = arg<(Category?) -> Unit>(1)
          onSuccess(null)
        }

    every { accountViewModel.fetchUserAccount(eq("user123"), any()) } answers
        {
          val onResult = arg<(Account?) -> Unit>(1)
          onResult(null)
        }

    composeTestRule.setContent {
      QuickFixTheme {
        AnnouncementCard(
            announcement = sampleAnnouncement,
            announcementImage = null,
            accountViewModel = accountViewModel,
            categoryViewModel = categoryViewModel) {}
      }
    }

    composeTestRule.waitForIdle()

    // Category is null => default category icon
    composeTestRule
        .onNodeWithTag("AnnouncementCategoryIcon_testAnn", useUnmergedTree = true)
        .assertExists()
  }

  @Test
  fun announcementCard_categoryFetchedInitially_showsCategoryIcon() {
    every { categoryViewModel.getCategoryBySubcategoryId(eq("testSubCat"), any()) } answers
        {
          val onSuccess = arg<(Category?) -> Unit>(1)
          onSuccess(
              Category(
                  id = "catId",
                  name = "Later Category",
                  description = "",
                  subcategories = emptyList()))
        }

    every { accountViewModel.fetchUserAccount(eq("user123"), any()) } answers
        {
          val onResult = arg<(Account?) -> Unit>(1)
          onResult(null)
        }

    composeTestRule.setContent {
      QuickFixTheme {
        AnnouncementCard(
            announcement = sampleAnnouncement,
            announcementImage = null,
            accountViewModel = accountViewModel,
            categoryViewModel = categoryViewModel) {}
      }
    }

    composeTestRule.waitForIdle()

    // Check icon exists after category found initially
    composeTestRule
        .onNodeWithTag("AnnouncementCategoryIcon_testAnn", useUnmergedTree = true)
        .assertExists()
  }
}

package com.arygm.quickfix.model.category

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.verify

class CategoryViewModelTest {

  private lateinit var categoryRepository: CategoryRepositoryFirestore
  private lateinit var categoryViewModel: CategoryViewModel
  private val subcategory =
      Subcategory(
          id = "residential_painting",
          name = "Residential Painting",
          tags = listOf("Interior Painting", "Exterior Painting", "Cabinet Painting"),
          scale =
              Scale(
                  longScale = "Prices are displayed relative to the cost of painting a 20 m² room.",
                  shortScale = "20 m² room equivalent"),
          setServices =
              listOf(
                  "Surface Preparation",
                  "Interior Painting",
                  "Exterior Painting",
                  "Cabinet Painting",
                  "Trim and Baseboard Painting",
                  "Wallpaper Removal",
                  "Deck and Fence Painting",
                  "Popcorn Ceiling Removal",
                  "Pressure Washing",
                  "Garage Floor Painting",
                  "Sealing and Caulking",
                  "Color Consultation",
                  "Minor Repairs",
                  "Clean-Up"))
  private val category =
      Category(
          id = "painting",
          name = "Painting",
          description = "Find skilled painters for residential or commercial projects.",
          subcategories =
              listOf(
                  Subcategory(
                      id = "residential_painting",
                      name = "Residential Painting",
                      tags = listOf("Interior Painting", "Exterior Painting", "Cabinet Painting"),
                      scale =
                          Scale(
                              longScale =
                                  "Prices are displayed relative to the cost of painting a 20 m² room.",
                              shortScale = "20 m² room equivalent"),
                      setServices =
                          listOf(
                              "Surface Preparation",
                              "Interior Painting",
                              "Exterior Painting",
                              "Cabinet Painting",
                              "Trim and Baseboard Painting",
                              "Wallpaper Removal",
                              "Deck and Fence Painting",
                              "Popcorn Ceiling Removal",
                              "Pressure Washing",
                              "Garage Floor Painting",
                              "Sealing and Caulking",
                              "Color Consultation",
                              "Minor Repairs",
                              "Clean-Up")),
                  Subcategory(
                      id = "commercial_painting",
                      name = "Commercial Painting",
                      tags = listOf("Office Buildings", "Retail Spaces"),
                      scale =
                          Scale(
                              longScale =
                                  "Prices are displayed relative to the cost of painting a 100 m² commercial space.",
                              shortScale = "100 m² commercial space equivalent"),
                      setServices =
                          listOf(
                              "Surface Preparation",
                              "Interior Commercial Painting",
                              "Exterior Commercial Painting",
                              "Specialty Coatings",
                              "Epoxy Floor Coatings",
                              "Line Striping and Markings",
                              "Power Washing",
                              "Graffiti Removal",
                              "Metal Structure Painting",
                              "Parking Lot Painting",
                              "Safety Painting",
                              "Color Branding",
                              "Clean-Up")),
                  Subcategory(
                      id = "decorative_painting",
                      name = "Decorative Painting",
                      tags = listOf("Faux Finishes", "Murals"),
                      scale =
                          Scale(
                              longScale =
                                  "Prices are displayed relative to the cost of painting a 20 m² room.",
                              shortScale = "20 m² room equivalent"),
                      setServices =
                          listOf(
                              "Decorative Painting",
                              "Faux Finishes",
                              "Murals",
                              "Accent Walls",
                              "Textured Painting",
                              "Stenciling",
                              "Color Washing",
                              "Rag Rolling",
                              "Sponging",
                              "Venetian Plaster",
                              "Glazing",
                              "Metallic Finishes",
                              "Surface Preparation",
                              "Color Consultation",
                              "Clean-Up"))))

  @Before
  fun setUp() {
    runBlocking {
      categoryRepository = mock(CategoryRepositoryFirestore::class.java)
      categoryViewModel = CategoryViewModel(categoryRepository)

      val initCaptor = argumentCaptor<() -> Unit>()
      verify(categoryRepository).init(initCaptor.capture())

      // Mock getProfiles

      // Simulate repository calling the init callback
      initCaptor.firstValue.invoke()
    }
  }

  @Test
  fun init_callsRepositoryInit() {
    verify(categoryRepository).init(any())
  }

  @Test
  fun init_invokesFetchCategoriesWhenRepositoryInitCallsCallback() {
    runBlocking { verify(categoryRepository).fetchCategories(any(), any()) }
  }

  @Test
  fun fetchCategories_whenSuccess_updatesProfilesStateFlow() = runTest {
    val categoriesList = listOf(category)
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Category?>) -> Unit>(0)
          onSuccess(categoriesList)
          null
        }
        .`when`(categoryRepository)
        .fetchCategories(any(), any())

    categoryViewModel.getCategories()

    val result = categoryViewModel.categories.first()
    assertThat(result, `is`(categoriesList))
  }

  @Test
  fun fetchCategories_whenFailure_logsError() {
    runBlocking {
      val exception = Exception("Test exception")
      doAnswer { invocation ->
            val onFailure = invocation.getArgument<(Exception) -> Unit>(1)
            onFailure(exception)
            null
          }
          .`when`(categoryRepository)
          .fetchCategories(any(), any())

      categoryViewModel.getCategories()

      // We can check that profiles remains empty
      val result = categoryViewModel.categories.value
      assertThat(result, `is`(emptyList()))
    }
  }

  @Test
  fun fetchSubCategories_whenSuccess_updatesProfilesStateFlow() = runTest {
    val subcategoriesList = listOf(subcategory)
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Subcategory?>) -> Unit>(1)
          onSuccess(subcategoriesList)
          null
        }
        .`when`(categoryRepository)
        .fetchSubcategories(any(), any(), any())

    categoryViewModel.getSubcategories("painting")

    val result = categoryViewModel.subcategories.first()
    assertThat(result, `is`(subcategoriesList))
  }

  @Test
  fun fetchSubCategories_whenFailure_logsError() {
    runBlocking {
      val exception = Exception("Test exception")
      doAnswer { invocation ->
            val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
            onFailure(exception)
            null
          }
          .`when`(categoryRepository)
          .fetchSubcategories(any(), any(), any())

      categoryViewModel.getSubcategories("painting")

      // We can check that profiles remains empty
      val result = categoryViewModel.subcategories.value
      assertThat(result, `is`(emptyList()))
    }
  }
}

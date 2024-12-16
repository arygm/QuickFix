package com.arygm.quickfix.ui.profile.becomeWorker.views.professional

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.theme.QuickFixTheme
import com.arygm.quickfix.ui.uiMode.appContentUI.userModeUI.profile.becomeWorker.views.professional.QuickFixCheckedList
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuickFixCheckedListElementTest {

  @get:Rule val composeTestRule = createComposeRule()

  private val listOfServicesOdd = listOf("Service 1", "Service 2", "Service 3")
  private val listOfServicesEven = listOf("Service 1", "Service 2", "Service 3", "Service 4")

  private val checkedStatesServiceOdd =
      mutableStateListOf<Boolean>().apply { repeat(listOfServicesOdd.size) { add(false) } }
  private val checkedStatesServiceEven =
      mutableStateListOf<Boolean>().apply { repeat(listOfServicesEven.size) { add(false) } }

  @Before
  fun setup() {
    checkedStatesServiceOdd.map { false }
    checkedStatesServiceEven.map { false }
  }

  private fun setUpProfessionalInfoScreen(
      listServices: List<String>,
      includedServices: Boolean,
      checkedStatesServices: SnapshotStateList<Boolean>,
      minToSelect: Int = 2,
      maxToSelect: Int = listServices.size,
      isTextFieldList: Boolean = false
  ) {
    composeTestRule.setContent {
      val indices = (0 until listServices.size step 2)
      val formValidated = remember { mutableStateOf(false) }
      val textFieldList = remember { mutableStateListOf<MutableState<String>>() }
      val canAddTextField = remember { mutableStateOf(true) }

      QuickFixTheme {
        if (includedServices) {
          val includedServicesList = remember { mutableStateOf(listOf<IncludedService>()) }
          QuickFixCheckedList(
              listServices = listServices,
              checkedStatesServices = checkedStatesServices,
              heightRatio = 1.dp,
              indices = indices,
              minToSelect = minToSelect,
              maxToSelect = maxToSelect,
              onClickActionOk = {
                includedServicesList.value =
                    listServices
                        .filterIndexed { index, _ -> checkedStatesServices[index] }
                        .map { IncludedService(it) }
                formValidated.value = true
              },
              formValidated = formValidated,
              boldText = " 5 to 10 Included services",
              label = "Choose",
              secondPartLabel = " in your painting job from this set",
              widthRatio = 1.dp,
              modifier =
                  Modifier.semantics { testTag = C.Tag.professionalInfoScreenIncludedServicesList },
              isTextFieldList = isTextFieldList,
              textFieldList = textFieldList,
              canAddTextField = canAddTextField)
        } else {
          val addOnServices = remember { mutableStateOf(listOf<AddOnService>()) }
          QuickFixCheckedList(
              listServices = listServices,
              checkedStatesServices = checkedStatesServices,
              heightRatio = 1.dp,
              indices = indices,
              minToSelect = minToSelect,
              maxToSelect = maxToSelect,
              onClickActionOk = {
                val filteredAddOnServices =
                    listServices
                        .filterIndexed { index, _ -> checkedStatesServices[index] }
                        .map { AddOnService(it) }

                // Map textFieldList to AddOnService
                val textFieldAddOnServices = textFieldList.map { AddOnService(it.value) }

                // Combine both lists and assign to addOnServices.value
                addOnServices.value = filteredAddOnServices + textFieldAddOnServices
                formValidated.value = true
                canAddTextField.value = false
              },
              formValidated = formValidated,
              boldText = " 5 to 10 Included services",
              label = "Choose",
              secondPartLabel = " in your painting job from this set",
              widthRatio = 1.dp,
              modifier =
                  Modifier.semantics { testTag = C.Tag.professionalInfoScreenIncludedServicesList },
              isTextFieldList = isTextFieldList,
              textFieldList = textFieldList,
              canAddTextField = canAddTextField)
        }
      }
    }
  }

  @Test
  fun testCheckedListElementOddWithoutTextField() {
    val listServices = listOfServicesOdd
    val checkedStatesServices = checkedStatesServiceOdd
    val includedServices = true
    val indices = (0 until listServices.size step 2)
    setUpProfessionalInfoScreen(listServices, includedServices, checkedStatesServices)
    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListOk).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListReset).assertIsDisplayed()
    indices.forEach {
      composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListElementLeft + it).assertIsDisplayed()
      if (it + 1 < listServices.size)
          composeTestRule
              .onNodeWithTag(C.Tag.quickFixCheckedListElementRight + it)
              .assertIsDisplayed()
      else
          composeTestRule
              .onNodeWithTag(C.Tag.quickFixCheckedListElementRight + it)
              .assertDoesNotExist()
    }
  }

  @Test
  fun testCheckedListElementEvenWithoutTextField() {
    val listServices = listOfServicesEven
    val checkedStatesServices = checkedStatesServiceEven
    val includedServices = true
    val indices = (0 until listServices.size step 2)
    setUpProfessionalInfoScreen(listServices, includedServices, checkedStatesServices)
    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListOk).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListReset).assertIsDisplayed()
    indices.forEach {
      composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListElementLeft + it).assertIsDisplayed()
      if (it + 1 < listServices.size)
          composeTestRule
              .onNodeWithTag(C.Tag.quickFixCheckedListElementRight + it)
              .assertIsDisplayed()
      else
          composeTestRule
              .onNodeWithTag(C.Tag.quickFixCheckedListElementRight + it)
              .assertDoesNotExist()
    }
  }

  @Test
  fun testCheckedListElementOddWithoutTextFieldOkEnabledToEdit() {
    val listServices = listOfServicesOdd
    val checkedStatesServices = checkedStatesServiceOdd
    val includedServices = true
    val indices = (0 until listServices.size step 2)
    setUpProfessionalInfoScreen(listServices, includedServices, checkedStatesServices)
    indices.forEach {
      composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListElementLeft + it).performClick()
      if (it + 1 < listServices.size)
          composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListElementRight + it).performClick()
    }

    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListOk).assertIsEnabled()
    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListOk).performClick()
    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListEdit).assertIsDisplayed()

    indices.forEach {
      composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListElementLeft + it).assertIsNotEnabled()
      if (it + 1 < listServices.size)
          composeTestRule
              .onNodeWithTag(C.Tag.quickFixCheckedListElementRight + it)
              .assertIsNotEnabled()
    }

    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListEdit).performClick()
    indices.forEach {
      composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListElementLeft + it).assertIsEnabled()
      if (it + 1 < listServices.size)
          composeTestRule
              .onNodeWithTag(C.Tag.quickFixCheckedListElementRight + it)
              .assertIsEnabled()
    }
  }

  @Test
  fun testCheckedListElementEvenWithoutTextFieldOkEnabledToEdit() {
    val listServices = listOfServicesEven
    val checkedStatesServices = checkedStatesServiceEven
    val includedServices = true
    val indices = (0 until listServices.size step 2)
    setUpProfessionalInfoScreen(listServices, includedServices, checkedStatesServices)
    indices.forEach {
      composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListElementLeft + it).performClick()
      if (it + 1 < listServices.size)
          composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListElementRight + it).performClick()
    }

    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListOk).assertIsEnabled()
    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListOk).performClick()
    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListEdit).assertIsDisplayed()

    indices.forEach {
      composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListElementLeft + it).assertIsNotEnabled()
      if (it + 1 < listServices.size)
          composeTestRule
              .onNodeWithTag(C.Tag.quickFixCheckedListElementRight + it)
              .assertIsNotEnabled()
    }

    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListEdit).performClick()
    indices.forEach {
      composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListElementLeft + it).assertIsEnabled()
      if (it + 1 < listServices.size)
          composeTestRule
              .onNodeWithTag(C.Tag.quickFixCheckedListElementRight + it)
              .assertIsEnabled()
    }
  }

  @Test
  fun testCheckedListElementOddWithTextFieldOkEnabledToEdit() {
    val listServices = listOfServicesOdd
    val checkedStatesServices = checkedStatesServiceOdd
    val includedServices = false
    setUpProfessionalInfoScreen(
        listServices = listServices,
        includedServices = includedServices,
        checkedStatesServices = checkedStatesServices,
        isTextFieldList = true)

    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListElementLeft + 0).performClick()

    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListTextFieldElementAdd).assertIsDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListTextFieldElementAdd).performClick()

    composeTestRule
        .onNodeWithTag(C.Tag.quickFixCheckedListTextFieldElement + 0)
        .performTextInput("Service 1")
    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListOk).assertIsEnabled()
    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListOk).performClick()
    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListEdit).assertIsDisplayed()

    composeTestRule
        .onNodeWithTag(C.Tag.quickFixCheckedListTextFieldElementDelete + 0)
        .assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListEdit).performClick()

    composeTestRule
        .onNodeWithTag(C.Tag.quickFixCheckedListTextFieldElementDelete + 0)
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(C.Tag.quickFixCheckedListTextFieldElementDelete + 0)
        .performClick()
    composeTestRule
        .onNodeWithTag(C.Tag.quickFixCheckedListTextFieldElement + 1)
        .assertDoesNotExist()
    composeTestRule.onNodeWithTag(C.Tag.quickFixCheckedListOk).assertIsNotEnabled()
  }
}

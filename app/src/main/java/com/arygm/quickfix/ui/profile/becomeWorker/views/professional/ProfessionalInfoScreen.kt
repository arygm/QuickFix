package com.arygm.quickfix.ui.profile.becomeWorker.views.professional

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.arygm.quickfix.model.profile.dataFields.AddOnService
import com.arygm.quickfix.model.profile.dataFields.IncludedService
import com.arygm.quickfix.ressources.C
import com.arygm.quickfix.ui.theme.poppinsTypography

@Composable
fun ProfessionalInfoScreen(
    pagerState: PagerState,
    price: MutableState<String>,
    fieldOfWork: MutableState<String>,
    includedServices: MutableState<List<IncludedService>>,
    addOnServices: MutableState<List<AddOnService>>,
    tags: MutableState<List<String>>,
) {
    LazyColumn {
        item {
            Text(
                "Personal info",
                style =
                poppinsTypography.headlineMedium.copy(
                    fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
                color = colorScheme.onBackground,
                modifier = Modifier.semantics { testTag = C.Tag.personalInfoScreenSectionTitle })
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Tell us a bit about yourself. This information will appear on your public profile, so that potential " +
                            "buyers can get to know you better.",
                    style =
                    poppinsTypography.headlineMedium.copy(
                        fontSize = 9.sp, fontWeight = FontWeight.Medium),
                    color = colorScheme.onSurface,
                    modifier =
                    Modifier.weight(0.8f).semantics {
                        testTag = C.Tag.personalInfoScreenSectionDescription
                    })
                Spacer(modifier = Modifier.weight(0.2f))
            }
        }

        item {

        }
    }
}
package pt.pc.gymlog.ui.screens.me

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.pc.gymlog.viewmodel.FocusAreaUi
import pt.pc.gymlog.viewmodel.WorkoutViewModel
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FocusScreen(
    vm: WorkoutViewModel,
    onBack: () -> Unit
) {
    var selected by remember { mutableStateOf(vm.userProfile.focusAreas) }

    fun toggle(area: FocusAreaUi) {
        selected =
            when (area) {
                FocusAreaUi.FULL_BODY -> setOf(FocusAreaUi.FULL_BODY)
                else -> {
                    val withoutFull = selected - FocusAreaUi.FULL_BODY
                    if (area in withoutFull) {
                        val newSet = withoutFull - area
                        if (newSet.isEmpty()) setOf(FocusAreaUi.FULL_BODY) else newSet
                    } else {
                        withoutFull + area
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Área de foco") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Voltar") } }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            FlowRow(
                maxItemsInEachRow = 3,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FocusAreaUi.values().forEach { area ->
                    FilterChip(
                        selected = area in selected,
                        onClick = { toggle(area) },
                        label = { Text(area.label) }
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = {
                    vm.updateFocusAreas(selected)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("SALVAR")
            }
        }
    }
}

package pt.pc.gymlog.ui.screens.today

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import pt.pc.gymlog.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickDayExercisesScreen(
    vm: WorkoutViewModel,
    day: Int,
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf(TextFieldValue("")) }
    // We want to verify which exercises are already in the day to check them by default?
    // Or just start empty and add what is selected?
    // "Add Selected" implies adding to existing. 
    // Let's pre-select existing ones?
    // If I uncheck an existing one, should I remove it? "Add" suggests only adding.
    // Ideally this screen is "Manage Exercises".
    // Let's stick to "Add".
    
    val existingIds = vm.todayExerciseIds(day).toSet()
    var selected by remember { mutableStateOf(setOf<Long>()) }

    val filtered = vm.exercises.filter { ex ->
        val q = query.text.trim()
        if (q.isBlank()) true
        else ex.name.contains(q, ignoreCase = true) ||
                (ex.muscleGroup?.contains(q, ignoreCase = true) == true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adicionar exercícios") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selected.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        vm.addExercisesToToday(day, selected.toList())
                        onBack()
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Adicionar")
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar exercícios") },
                modifier = Modifier.fillMaxWidth()
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered, key = { it.id }) { ex ->
                    val isAlreadyAdded = ex.id in existingIds
                    val isSelected = ex.id in selected
                    
                    val enabled = !isAlreadyAdded // Disable if already in workout

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAlreadyAdded) Color.LightGray.copy(alpha=0.3f) else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = enabled) {
                                selected = if (isSelected) selected - ex.id else selected + ex.id
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(
                                checked = isSelected || isAlreadyAdded,
                                onCheckedChange = if (enabled) { isOn ->
                                    selected = if (isOn) selected + ex.id else selected - ex.id
                                } else null,
                                enabled = enabled
                            )
                            Column(Modifier.weight(1f)) {
                                Text(ex.name, style = MaterialTheme.typography.titleMedium)
                                ex.muscleGroup?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                            }
                        }
                    }
                }
            }
        }
    }
}

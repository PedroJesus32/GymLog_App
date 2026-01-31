package pt.pc.gymlog.ui.screens.customize

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pt.pc.gymlog.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomWorkoutEditScreen(
    vm: WorkoutViewModel,
    workoutId: Long,
    onBack: () -> Unit
) {
    val workout = vm.getCustomWorkout(workoutId)

    if (workout == null) {
        // se der -1L por algum bug, não crasha
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Treino") },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                    }
                )
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                Text("Treino não encontrado.", modifier = Modifier.padding(16.dp))
            }
        }
        return
    }

    var showPicker by remember { mutableStateOf(false) }

    val exerciseList = workout.exerciseIds
        .mapNotNull { id -> vm.exercises.firstOrNull { it.id == id } }
    val setsMap = workout.setsByExercise

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showPicker = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { showPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Adicionar exercícios")
            }

            if (exerciseList.isEmpty()) {
                Text("Adicione o seu primeiro exercício", modifier = Modifier.padding(8.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(exerciseList, key = { it.id }) { ex ->
                        Card {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(ex.name, style = MaterialTheme.typography.titleMedium)
                                    Text(ex.muscleGroup ?: "", style = MaterialTheme.typography.bodyMedium)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("${setsMap[ex.id] ?: 1} Set")
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remover",
                                        modifier = Modifier
                                            .clickable { vm.removeExerciseFromCustomWorkout(workoutId, ex.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPicker) {
        ExercisePickerBottomSheet(
            vm = vm,
            onClose = { showPicker = false },
            onAdd = { pickedIds ->
                vm.addExercisesToCustomWorkout(workoutId, pickedIds)
                showPicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExercisePickerBottomSheet(
    vm: WorkoutViewModel,
    onClose: () -> Unit,
    onAdd: (List<Long>) -> Unit
) {
    var search by remember { mutableStateOf("") }
    val all = vm.exercises

    val filtered = remember(search, all) {
        if (search.isBlank()) all
        else all.filter {
            it.name.contains(search, ignoreCase = true) ||
                    (it.muscleGroup?.contains(search, ignoreCase = true) == true)
        }
    }

    val selected = remember { mutableStateMapOf<Long, Boolean>() }

    ModalBottomSheet(onDismissRequest = onClose) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Adicionar exercícios", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = onClose) { Icon(Icons.Default.Close, null) }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar exercícios") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered, key = { it.id }) { ex ->
                    val checked = selected[ex.id] == true
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selected[ex.id] = !checked }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { selected[ex.id] = it }
                            )
                            Column {
                                Text(ex.name, style = MaterialTheme.typography.titleMedium)
                                Text(ex.muscleGroup ?: "", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    val picked = selected.filterValues { it }.keys.toList()
                    onAdd(picked)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Adicionar")
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

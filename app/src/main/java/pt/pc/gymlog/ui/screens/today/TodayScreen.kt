package pt.pc.gymlog.ui.screens.today

import pt.pc.gymlog.viewmodel.PlanDayType
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pt.pc.gymlog.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    vm: WorkoutViewModel,
    day: Int,
    onGoReport: () -> Unit,
    onBackToPlan: () -> Unit
) {




    LaunchedEffect(day) {
        vm.openDay(
            day = day
        )
    }

    val allExercises = vm.exercises
    val selectedExerciseIds = vm.todayExerciseIds(day)
    val sets = vm.sets(day)

    val plan = vm.planDays.firstOrNull { it.dayNumber == day }
    val isRestDay = plan?.type == PlanDayType.REST
    val focusTitle = plan?.title ?: ""


    var showPickExercise by remember { mutableStateOf(false) }
    var showAddSet by remember { mutableStateOf(false) }
    var addSetExerciseId by remember { mutableStateOf<Long?>(null) }

    val todayStr = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Treino • Dia $day • $focusTitle") }) },
        floatingActionButton = {
            if (!isRestDay) {
                FloatingActionButton(onClick = { showPickExercise = true }) { Text("+") }
            }
        }
    ) { padding ->

        val selectedExercises = allExercises.filter { it.id in selectedExerciseIds }

        if (selectedExercises.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sem exercícios no treino de hoje.")
                Spacer(Modifier.height(12.dp))
                if (isRestDay) {
                    Button(
                        onClick = {
                            vm.unlockNextDay(day)
                            onBackToPlan()
                        }
                    ) {
                        Text("Terminar dia de descanso")
                    }
                } else {
                    Button(onClick = { showPickExercise = true }) {
                        Text("Adicionar exercício")
                    }
                }



            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(selectedExercises, key = { it.id }) { ex ->
                    Card {
                        Column(modifier = Modifier.padding(14.dp)) {

                            Text(ex.name, style = MaterialTheme.typography.titleMedium)
                            Text(ex.muscleGroup ?: "Sem grupo", style = MaterialTheme.typography.bodyMedium)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { vm.removeFromToday(day,ex.id) }) {
                                    Text("Remover")
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            val exerciseSets = sets.filter { it.exerciseId == ex.id }

                            if (exerciseSets.isEmpty()) {
                                Text("Sem séries ainda.")
                            } else {
                                exerciseSets.forEach { s ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Set ${s.setNumber}: ${s.weight} kg • ${s.reps} reps",
                                            modifier = Modifier.weight(1f)
                                        )
                                        TextButton(onClick = { vm.deleteSet(day,s.id) }) {
                                            Text("Apagar")
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(10.dp))

                            // ✅ ESTE é o botão do card
                            Button(onClick = {
                                addSetExerciseId = ex.id
                                showAddSet = true
                            }) {
                                Text("Adicionar série")
                            }
                        }
                    }
                }

                // ✅ ESTE é o único botão "Guardar treino" (fora dos cards)
                item {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            vm.saveTodayWorkout(day, todayStr)
                            onGoReport()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar treino")
                    }
                }
            }
        } //aqui
    }

    if (showPickExercise) {
        AlertDialog(
            onDismissRequest = { showPickExercise = false },
            title = { Text("Escolher exercício") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    allExercises.forEach { ex ->
                        val disabled = ex.id in selectedExerciseIds
                        Button(
                            onClick = {
                                vm.addToToday(day,ex.id)
                                showPickExercise = false
                            },
                            enabled = !disabled,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (disabled) "${ex.name} (já adicionado)" else ex.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPickExercise = false }) { Text("Fechar") }
            }
        )
    }

    if (showAddSet && addSetExerciseId != null) {
        AddSetDialog(
            onDismiss = { showAddSet = false },
            onSave = { weight, reps ->
                vm.addSet(day, addSetExerciseId!!, weight, reps)
                showAddSet = false
            }
        )
    }
}

@Composable
private fun AddSetDialog(
    onDismiss: () -> Unit,
    onSave: (weight: Double, reps: Int) -> Unit
) {
    var weightText by remember { mutableStateOf("0") }
    var repsText by remember { mutableStateOf("") }

    var weightError by remember { mutableStateOf<String?>(null) }
    var repsError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar série") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = weightError != null,
                    supportingText = { if (weightError != null) Text(weightError!!) }
                )
                OutlinedTextField(
                    value = repsText,
                    onValueChange = { repsText = it },
                    label = { Text("Repetições *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = repsError != null,
                    supportingText = { if (repsError != null) Text(repsError!!) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                weightError = null
                repsError = null

                val weight = weightText.replace(",", ".").toDoubleOrNull()
                val reps = repsText.toIntOrNull()

                if (weight == null || weight < 0) {
                    weightError = "Peso tem de ser número e ≥ 0."
                    return@TextButton
                }
                if (reps == null || reps <= 0) {
                    repsError = "Repetições tem de ser número e > 0."
                    return@TextButton
                }

                onSave(weight, reps)
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

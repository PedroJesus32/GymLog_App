package pt.pc.gymlog.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.pc.gymlog.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsScreen(
    vm: WorkoutViewModel,
    workoutId: Long,
    onBack: () -> Unit,
    onRepeatToday: () -> Unit
) {
    val workout = vm.getWorkoutFromHistory(workoutId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhe do treino") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Voltar") }
                }
            )
        }
    ) { padding ->

        if (workout == null) {
            Column(
                modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Treino não encontrado.")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card {
                    Column(Modifier.padding(14.dp)) {
                        Text(workout.dateLabel, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onRepeatToday, modifier = Modifier.fillMaxWidth()) {
                            Text("Repetir treino hoje")
                        }
                    }
                }
            }

            items(workout.exercisesSnapshot, key = { it.id }) { ex ->
                val exSets = workout.setsSnapshot.filter { it.exerciseId == ex.id }

                Card {
                    Column(Modifier.padding(14.dp)) {
                        Text(ex.name, style = MaterialTheme.typography.titleMedium)
                        if (!ex.muscleGroup.isNullOrBlank()) {
                            Text(ex.muscleGroup!!, style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(Modifier.height(8.dp))

                        if (exSets.isEmpty()) {
                            Text("Sem séries.")
                        } else {
                            exSets.sortedBy { it.setNumber }.forEach { s ->
                                Text("Set ${s.setNumber}: ${s.weight} kg • ${s.reps} reps")
                            }
                        }
                    }
                }
            }
        }
    }
}

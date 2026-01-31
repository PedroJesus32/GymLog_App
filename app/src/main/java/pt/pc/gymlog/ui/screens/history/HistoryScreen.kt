package pt.pc.gymlog.ui.screens.history

import androidx.compose.foundation.clickable
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
fun HistoryScreen(vm: WorkoutViewModel, onOpenWorkout: (Long) -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Histórico") }) }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            HistoryContent(vm = vm, onOpenWorkout = onOpenWorkout)
        }
    }
}

@Composable
fun HistoryContent(vm: WorkoutViewModel, onOpenWorkout: (Long) -> Unit) {
    val history = vm.history

    if (history.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Ainda não tens treinos guardados.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(history, key = { it.id }) { entry ->
                Card(
                    modifier = Modifier.clickable { onOpenWorkout(entry.id) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(entry.dateLabel, style = MaterialTheme.typography.titleMedium)

                        val exCount = entry.exercisesSnapshot.size
                        val setCount = entry.setsSnapshot.size
                        Text("$exCount exercícios • $setCount séries")

                        Spacer(Modifier.height(8.dp))

                        entry.exercisesSnapshot.forEach { ex ->
                            val exSets = entry.setsSnapshot.filter { it.exerciseId == ex.id }
                            val best = exSets.maxByOrNull { it.weight }
                            val bestText = best?.let { "máx ${it.weight}kg x ${it.reps}" } ?: "sem séries"
                            Text("• ${ex.name}: $bestText")
                        }
                    }
                }
            }
        }
    }
}
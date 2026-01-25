package pt.pc.gymlog.ui.screens.report

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.pc.gymlog.ui.screens.history.HistoryScreen
import pt.pc.gymlog.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    vm: WorkoutViewModel,
    onOpenWorkout: (Long) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Relatório") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // “Meu peso” placeholder (como na tua referência)
            Card(modifier = Modifier.padding(12.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text("Meu peso", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text("Em breve: registo de peso + gráficos.")
                }
            }

            // Histórico (já funcional)
            HistoryScreen(vm = vm, onOpenWorkout = onOpenWorkout)
        }
    }
}

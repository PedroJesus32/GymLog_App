package pt.pc.gymlog.ui.screens.plan

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
fun PlanScreen(
    vm: WorkoutViewModel,
    onOpenDay: (Int) -> Unit
) {
    val days = (1..30).toList() // depois ligamos ao teu plano real

    Scaffold(
        topBar = { TopAppBar(title = { Text("Treino") }) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card {
                    Column(Modifier.padding(14.dp)) {
                        Text("PLANO", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(6.dp))
                        Text("Fortalecimento Muscular • 30 dias")
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = { onOpenDay(1) }) {
                            Text("Começar (Dia 1)")
                        }
                    }
                }
            }

            items(days) { day ->
                Card {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Dia $day")
                        TextButton(onClick = { onOpenDay(day) }) { Text("Abrir") }
                    }
                }
            }
        }
    }
}

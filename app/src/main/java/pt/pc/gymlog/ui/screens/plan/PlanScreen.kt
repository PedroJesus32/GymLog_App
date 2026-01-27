package pt.pc.gymlog.ui.screens.plan

import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import pt.pc.gymlog.viewmodel.PlanDayType
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.pc.gymlog.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    vm: WorkoutViewModel,
    onOpenDay: (Int) -> Unit
) {
    val unlockedMax = vm.unlockedMaxDay

    // Plano simples: repete Peito/Costas/Pernas/Descanso

    val days = vm.planDays

    LaunchedEffect(days.size) {
        if (days.isEmpty()) vm.regeneratePlan()
    }


    Scaffold(
        topBar = { TopAppBar(title = { Text("Treino") }) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
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

                        val startDay = unlockedMax.coerceIn(1, 30)
                        Button(onClick = { onOpenDay(startDay) }) {
                            Text("Começar (Dia $startDay)")
                        }

                    }
                }
            }

            items(days, key = { it.dayNumber }) { d ->

                val done = d.dayNumber < unlockedMax
                val locked = d.dayNumber > unlockedMax
                val isCurrent = d.dayNumber == unlockedMax

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !locked) { onOpenDay(d.dayNumber) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Dia ${d.dayNumber}", style = MaterialTheme.typography.titleMedium)
                            Text(d.title, style = MaterialTheme.typography.bodyMedium)
                        }

                        when {
                            locked -> Icon(Icons.Default.Lock, contentDescription = "Bloqueado")
                            isCurrent -> TextButton(onClick = { onOpenDay(d.dayNumber) }) { Text("Abrir") }
                            done -> Icon(Icons.Default.CheckCircle, contentDescription = "Concluído")
                        }
                    }
                }
            }

        }
    }
}

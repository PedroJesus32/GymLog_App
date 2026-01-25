package pt.pc.gymlog.ui.screens.plan

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
    fun focusForDay(day: Int): Pair<String, Boolean> {
        return when ((day - 1) % 4) {
            0 -> "Peito" to false
            1 -> "Costas" to false
            2 -> "Parte inferior do corpo" to false
            else -> "Dia de Descanso" to true
        }
    }

    val days = (1..30).map { day ->
        val (focus, isRest) = focusForDay(day)
        val locked = day > unlockedMax
        DayPlan(
            day = day,
            title = if (isRest) "Dia de Descanso" else "Dia $day",
            focus = focus,
            isRest = isRest,
            locked = locked
        )
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

                        val startDay = unlockedMax.coerceAtMost(30)
                        Button(onClick = { onOpenDay(startDay) }) {
                            Text("Começar (Dia $startDay)")
                        }
                    }
                }
            }

            items(days, key = { it.day }) { item ->
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            Text(item.focus, style = MaterialTheme.typography.bodyMedium)
                        }

                        if (item.locked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Bloqueado"
                            )
                        } else {
                            TextButton(onClick = { onOpenDay(item.day) }) {
                                Text("Abrir")
                            }
                        }
                    }
                }
            }
        }
    }
}

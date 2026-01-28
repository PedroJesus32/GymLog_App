package pt.pc.gymlog.ui.screens.me

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.pc.gymlog.viewmodel.GoalUi
import pt.pc.gymlog.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(
    vm: WorkoutViewModel,
    onBack: () -> Unit
) {
    var selected by remember { mutableStateOf(vm.userProfile.goal) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meta") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Voltar") } }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            GoalUi.values().forEach { g ->
                Card(onClick = { selected = g }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(g.label, style = MaterialTheme.typography.titleMedium)
                        RadioButton(
                            selected = (selected == g),
                            onClick = { selected = g }
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = {
                    vm.updateGoal(selected)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar")
            }
        }
    }
}
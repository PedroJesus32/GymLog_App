package pt.pc.gymlog.ui.screens.me

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.pc.gymlog.ui.components.AppCard
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
                val isSelected = g == selected

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected = g },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(g.label, style = MaterialTheme.typography.titleMedium)
                            Text(
                                if (isSelected) "Selecionado" else "Toque para selecionar",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Selecionado")
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    vm.updateGoal(selected)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}
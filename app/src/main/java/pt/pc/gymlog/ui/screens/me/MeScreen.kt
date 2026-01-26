package pt.pc.gymlog.ui.screens.me

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeScreen(
    onOpenProfile: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Meu") }) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenProfile() }
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text("Meu perfil", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text("Nome, objetivo, nível, etc.")
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenSettings() }
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text("Definições gerais", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text("Dias de descanso, treinos por semana, unidades…")
                    }
                }
            }

            item {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Versão 1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }
    }
}

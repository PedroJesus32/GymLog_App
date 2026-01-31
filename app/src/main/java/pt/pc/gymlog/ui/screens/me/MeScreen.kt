package pt.pc.gymlog.ui.screens.me

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import pt.pc.gymlog.ui.components.AppCard
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeScreen(
    onOpenProfile: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var showAbout by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Meu") }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            AppCard(
                title = "Perfil",
                subtitle = "Meta, foco e 1RM",
                leadingIcon = Icons.Default.Person,
                showChevron = true,
                onClick = onOpenProfile
            )

            AppCard(
                title = "Definições gerais",
                subtitle = "Treinos/semana, descanso, unidades",
                leadingIcon = Icons.Default.Settings,
                showChevron = true,
                onClick = onOpenSettings
            )

            AppCard(
                title = "Sobre",
                subtitle = "Versão e info do app",
                leadingIcon = Icons.Default.Info,
                showChevron = true,
                onClick = { showAbout = true }
            )
        }
    }

    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            title = { Text("Sobre") },
            text = { Text("GymLog (Front-end) • v0.1\n\nBack-end será ligado depois.") },
            confirmButton = {
                TextButton(onClick = { showAbout = false }) { Text("Fechar") }
            }
        )
    }
}

@Composable
private fun MeCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}

package pt.pc.gymlog.ui.screens.me

import pt.pc.gymlog.ui.components.AppCard
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pt.pc.gymlog.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    vm: WorkoutViewModel,
    onBack: () -> Unit,
    onOpenGoal: () -> Unit,
    onOpenFocus: () -> Unit,
    onOpenOneRm: () -> Unit
) {
    val profile = vm.userProfile
    val focusText = profile.focusAreas.joinToString(", ") { it.label }
        .ifBlank { "Todo o corpo" }
    var showOneRmDialog by remember { mutableStateOf(false) }
    var oneRmText by remember { mutableStateOf(profile.oneRmSupinoKg.toString()) }





    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu perfil") },
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

            // ✅ Meta (abre GoalScreen)
            // ✅ Meta
            AppCard(
                title = "Meta",
                subtitle = profile.goal.label,
                showChevron = true,
                onClick = onOpenGoal
            )

            // ✅ Área de foco
            AppCard(
                title = "Área de foco",
                subtitle = focusText,
                showChevron = true,
                onClick = onOpenFocus
            )

            // ✅ 1RM
            AppCard(
                title = "Minha 1RM (Supino)",
                subtitle = "${profile.oneRmSupinoKg} kg",
                trailingText = "Editar",
                showChevron = true,
                onClick = onOpenOneRm
            )

            // ✅ Info básica (voltou)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Informações básicas", style = MaterialTheme.typography.titleMedium)

                    Text("Género: ${profile.gender.label}")
                    Text("Peso atual: ${String.format("%.1f", profile.currentWeightKg)} kg")
                    Text("Meta de peso: ${String.format("%.1f", profile.targetWeightKg)} kg")
                    Text("Altura: ${String.format("%.0f", profile.heightCm)} cm")
                }
            }
        }
    }

    if (showOneRmDialog) {
        AlertDialog(
            onDismissRequest = { showOneRmDialog = false },
            title = { Text("Editar 1RM (Supino)") },
            text = {
                OutlinedTextField(
                    value = oneRmText,
                    onValueChange = { oneRmText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Kg") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val v = oneRmText.toIntOrNull() ?: 0
                    vm.updateOneRmSupinoKg(v)
                    showOneRmDialog = false
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showOneRmDialog = false }) { Text("Cancelar") }
            }
        )
    }

}
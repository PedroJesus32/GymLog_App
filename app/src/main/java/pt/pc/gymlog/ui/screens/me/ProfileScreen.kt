package pt.pc.gymlog.ui.screens.me

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
    onOpenFocus: () -> Unit
) {
    val profile = vm.userProfile

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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenGoal() }
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("Meta", style = MaterialTheme.typography.titleMedium)
                    Text(profile.goal.label, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // ✅ Área de foco (abre FocusScreen)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenFocus() }
            ) {
                val focusText = profile.focusAreas.joinToString(", ") { it.label }
                Column(Modifier.padding(14.dp)) {
                    Text("Área de foco", style = MaterialTheme.typography.titleMedium)
                    Text(focusText.ifBlank { "Todo o corpo" }, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // ✅ 1RM (abre dialog para editar)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        oneRmText = profile.oneRmSupinoKg.toString()
                        showOneRmDialog = true
                    }
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text("Minha 1RM (Supino)", style = MaterialTheme.typography.titleMedium)
                    Text("${profile.oneRmSupinoKg} kg", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // ✅ Info básica (voltou)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Informações básicas", style = MaterialTheme.typography.titleMedium)

                    Text("Género: ${profile.gender.label}")
                    Text("Peso atual: ${profile.currentWeightKg} kg")
                    Text("Meta de peso: ${profile.targetWeightKg} kg")
                    Text("Altura: ${profile.heightCm} cm")
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
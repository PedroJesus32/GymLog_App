package pt.pc.gymlog.ui.screens.me

import kotlinx.coroutines.delay
import androidx.compose.material3.AssistChip
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import pt.pc.gymlog.viewmodel.WorkoutViewModel.WeekDayUi
import pt.pc.gymlog.viewmodel.WorkoutViewModel.UnitSystemUi
import pt.pc.gymlog.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    vm: WorkoutViewModel,
    onBack: () -> Unit
) {
    val current = vm.userSettings

    var workoutsPerWeek by remember { mutableStateOf(current.workoutsPerWeek) }
    var restDay by remember { mutableStateOf(current.restDay) }
    var unitSystem by remember { mutableStateOf(current.unitSystem) }

    var savedSnack by remember { mutableStateOf(false) }
    LaunchedEffect(savedSnack) {
        if (savedSnack) {
            delay(2500)
            savedSnack = false
        }
    }


    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    "Reiniciar App?",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    "Tem a certeza que deseja apagar todos os dados e reiniciar o app como se fosse novo? Esta ação não pode ser desfeita.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        vm.resetAppData()
                        onBack() // This will pop back, but since resetAppData sets onboarding=false, AppNav needs to react or we force nav
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sim, Apagar Tudo", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Definições gerais") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Voltar") } }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = remember { SnackbarHostState() })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Text("Treinos por semana", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(3, 4, 5, 6).forEach { n ->
                    val selected = workoutsPerWeek == n

                    AssistChip(
                        onClick = { workoutsPerWeek = n },
                        label = { Text("$n") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface,
                            labelColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        )

                    )
                }
            }



            Divider()

            Text("Dia de descanso", style = MaterialTheme.typography.titleMedium)
            DropdownSetting(
                value = restDay.label,
                options = WeekDayUi.values().map { it.label },
                onPick = { picked ->
                    restDay = WeekDayUi.values().first { it.label == picked }
                }
            )

            Divider()

            Text("Unidades", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                UnitSystemUi.values().forEach { u ->
                    val selected = unitSystem == u

                    AssistChip(
                        onClick = { unitSystem = u },
                        label = { Text(u.label) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surface,
                            labelColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                        )

                    )
                }
            }



            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    vm.updateSettings(
                        workoutsPerWeek = workoutsPerWeek,
                        restDay = restDay,
                        unitSystem = unitSystem
                    )
                    savedSnack = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            Spacer(modifier = Modifier.weight(1f))
            
            // RESET BUTTON
            OutlinedButton(
                onClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Text("Resetar App", fontWeight = FontWeight.Bold)
            }

            if (savedSnack) {
                // Snack simples sem host state para não complicar:
                Spacer(Modifier.height(8.dp))
                Card {
                    Text(
                        "Definições guardadas ✅",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownSetting(
    value: String,
    options: List<String>,
    onPick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text("Selecionar") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        onPick(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}

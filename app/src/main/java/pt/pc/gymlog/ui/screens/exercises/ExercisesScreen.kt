package pt.pc.gymlog.ui.screens.exercises

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.pc.gymlog.viewmodel.ExerciseUi
import pt.pc.gymlog.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    vm: WorkoutViewModel,
    onOpenDetail: (Long) -> Unit
) {

    val exercises = vm.exercises

    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<ExerciseUi?>(null) }

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<ExerciseUi?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Exercícios") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editing = null
                showForm = true
            }) { Text("+") }
        }
    ) { padding ->

        if (exercises.isEmpty()) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sem exercícios ainda.")
                Spacer(Modifier.height(12.dp))
                Button(onClick = {
                    editing = null
                    showForm = true
                }) { Text("Criar exercício") }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(exercises, key = { it.id }) { ex ->
                    Card {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onOpenDetail(ex.id)
                                    }
                            ) {
                                Text(ex.name, style = MaterialTheme.typography.titleMedium)
                                Text(ex.muscleGroup ?: "Sem grupo", style = MaterialTheme.typography.bodyMedium)
                            }

                            if (!ex.isSystem) {
                                TextButton(onClick = {
                                    editing = ex
                                    showForm = true
                                }) { Text("Editar") }

                                TextButton(onClick = {
                                    deleting = ex
                                    showDeleteConfirm = true
                                }) { Text("Apagar") }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        ExerciseFormDialog(
            initial = editing,
            onDismiss = { showForm = false },
            onSave = { saved ->
                if (editing == null) {
                    vm.addExercise(saved.name, saved.muscleGroup, saved.notes)
                } else {
                    vm.updateExercise(saved)
                }
                showForm = false
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Apagar exercício?") },
            text = { Text("Tens a certeza que queres apagar este exercício?") },
            confirmButton = {
                TextButton(onClick = {
                    val ex = deleting
                    if (ex != null) vm.deleteExercise(ex.id)
                    showDeleteConfirm = false
                }) { Text("Apagar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun ExerciseFormDialog(
    initial: ExerciseUi?,
    onDismiss: () -> Unit,
    onSave: (ExerciseUi) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var muscle by remember { mutableStateOf(initial?.muscleGroup ?: "") }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }
    var nameError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Criar exercício" else "Editar exercício") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (nameError != null) nameError = null
                    },
                    label = { Text("Nome *") },
                    isError = nameError != null,
                    supportingText = { if (nameError != null) Text(nameError!!) }
                )
                OutlinedTextField(
                    value = muscle,
                    onValueChange = { muscle = it },
                    label = { Text("Grupo muscular (opcional)") }
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas (opcional)") },
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val trimmed = name.trim()
                if (trimmed.isEmpty()) {
                    nameError = "O nome do exercício é obrigatório."
                    return@TextButton
                }

                val base = initial ?: ExerciseUi(id = 0, name = trimmed)
                onSave(
                    base.copy(
                        name = trimmed,
                        muscleGroup = muscle.trim().ifBlank { null },
                        notes = notes.trim().ifBlank { null }
                    )
                )
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

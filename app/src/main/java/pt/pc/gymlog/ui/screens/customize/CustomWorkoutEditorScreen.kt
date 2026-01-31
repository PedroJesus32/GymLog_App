package pt.pc.gymlog.ui.screens.customize

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import pt.pc.gymlog.ui.theme.GymGreen
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.pc.gymlog.viewmodel.WorkoutViewModel
import pt.pc.gymlog.viewmodel.SetEntryUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomWorkoutEditorScreen(
    vm: WorkoutViewModel,
    workoutId: Long,
    onBack: () -> Unit,
    onPickExercises: (Long) -> Unit,
    onReplaceExercise: (Long, Long) -> Unit,
    onStartWorkout: (Long) -> Unit // workoutId, exerciseId
) {
    val workout = vm.getCustomWorkout(workoutId)

    if (workout == null) {
        // Fallback if workout doesn't exist
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Treino não encontrado.")
        }
        return
    }

    var nameState by remember { mutableStateOf(TextFieldValue(workout.name)) }
    var isEditingName by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    // Sync name if it changes externally (though unlikely in this flow)
    LaunchedEffect(workout.name) {
        if (nameState.text != workout.name && !isEditingName) {
            nameState = TextFieldValue(workout.name)
        }
    }

    LaunchedEffect(isEditingName) {
        if (isEditingName) {
            focusRequester.requestFocus()
        }
    }

    val selectedExercises = workout.exerciseIds.mapNotNull { id ->
        vm.exercises.firstOrNull { it.id == id }
    }

    val primaryBlue = Color(0xFF4C6EF5)
    val backgroundGray = Color(0xFFF5F5F7)

    Scaffold(
        containerColor = backgroundGray,
        topBar = {
            // Custom Blue Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(primaryBlue)
                    .padding(bottom = 16.dp)
            ) {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        navigationIconContentColor = Color.White
                    )
                )

                // Editable Name Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditingName) {
                        BasicTextField(
                            value = nameState,
                            onValueChange = { nameState = it },
                            textStyle = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            cursorBrush = SolidColor(Color.White),
                            keyboardOptions = KeyboardOptions.Default,
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                if (nameState.text.isEmpty()) {
                                    Text("Nome do Treino", color = Color.White.copy(0.5f))
                                }
                                innerTextField()
                            }
                        )
                    } else {
                        Text(
                            text = nameState.text.ifEmpty { "Novo Treino" },
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            ),
                            color = Color.White,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isEditingName = true }
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (isEditingName) {
                                // Save name
                                vm.updateCustomWorkoutName(workoutId, newName = nameState.text)
                                isEditingName = false
                            } else {
                                isEditingName = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isEditingName) Icons.Default.CheckCircle else Icons.Default.Edit, // Using standard icons for now
                            contentDescription = "Editar Nome",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Text(
                    text = "Modo de edição",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        },
        bottomBar = {
             // Fixed Save Button at Bottom
             Column(
                 modifier = Modifier
                     .fillMaxWidth()
                     .background(backgroundGray)
                     .padding(16.dp)
             ) {
                 Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                     Button(
                         onClick = onBack,
                         modifier = Modifier
                             .weight(1f)
                             .height(50.dp),
                         shape = RoundedCornerShape(12.dp),
                         colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                     ) {
                         Text("SALVAR", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                     }

                     Button(
                         onClick = { onStartWorkout(workoutId) },
                         modifier = Modifier
                             .weight(1f)
                             .height(50.dp),
                         shape = RoundedCornerShape(12.dp),
                         colors = ButtonDefaults.buttonColors(
                             containerColor = GymGreen,
                             disabledContainerColor = GymGreen.copy(alpha = 0.5f),
                             disabledContentColor = Color.White.copy(alpha = 0.5f)
                         ),
                         enabled = selectedExercises.isNotEmpty()
                     ) {
                         Text("INICIAR", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                     }
                 }
             }
        }
    ) { padding ->
        
        if (selectedExercises.isEmpty()) {
            // Empty State
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                 Spacer(modifier = Modifier.height(40.dp))
                 Text(
                     text = "Adicione o seu primeiro exercício",
                     style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
                 )
                 Spacer(modifier = Modifier.height(16.dp))
                 
                 Button(
                     onClick = { onPickExercises(workoutId) },
                     modifier = Modifier
                         .fillMaxWidth(0.9f)
                         .height(56.dp),
                     shape = RoundedCornerShape(28.dp),
                     colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                     elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                 ) {
                     Text(
                         "+ Adicionar exercícios",
                         color = primaryBlue,
                         style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                     )
                 }
             }
        } else {
            // Populated List
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(selectedExercises, key = { it.id }) { ex ->
                    val setsForEx = workout.setsByExercise[ex.id].orEmpty()
                    CustomExerciseCard(
                        exerciseName = ex.name,
                        sets = setsForEx,
                        onAddSet = { vm.addCustomSet(workoutId, ex.id) },
                        onUpdateSet = { setUi, w, r -> 
                            vm.updateCustomSet(workoutId, ex.id, setUi.id, weight = w, reps = r) 
                        },
                        onDeleteSet = { setUi ->
                            vm.deleteCustomSet(workoutId, ex.id, setUi.id)
                        },
                        onRemoveExercise = {
                            vm.removeExerciseFromCustomWorkout(workoutId, ex.id)
                        },
                        onReplaceExercise = {
                            onReplaceExercise(workoutId, ex.id)
                        }
                    )
                }

                item {
                    // Add Exercises Button at bottom of list
                    Button(
                        onClick = { onPickExercises(workoutId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            "+ Adicionar exercícios",
                            color = primaryBlue,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomExerciseCard(
    exerciseName: String,
    sets: List<SetEntryUi>,
    onAddSet: () -> Unit,
    onUpdateSet: (SetEntryUi, Double, Int) -> Unit,
    onDeleteSet: (SetEntryUi) -> Unit,
    onRemoveExercise: () -> Unit,
    onReplaceExercise: () -> Unit // New callback
) {
     var showMenu by remember { mutableStateOf(false) }

     Card(
         shape = RoundedCornerShape(16.dp),
         colors = CardDefaults.cardColors(containerColor = Color.White),
         elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
         modifier = Modifier.fillMaxWidth()
     ) {
         Column(modifier = Modifier.padding(16.dp)) {
             // Header
             Row(
                 modifier = Modifier.fillMaxWidth(),
                 horizontalArrangement = Arrangement.SpaceBetween,
                 verticalAlignment = Alignment.CenterVertically
             ) {
                 Row(verticalAlignment = Alignment.CenterVertically) {
                     // Placeholder image
                     Box(
                         modifier = Modifier
                             .size(40.dp)
                             .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp)),
                         contentAlignment = Alignment.Center
                     ) {
                         // Eventually real image
                     }
                     Spacer(modifier = Modifier.width(12.dp))
                     Text(
                         text = exerciseName,
                         style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                     )
                 }
                 Box {
                     IconButton(onClick = { showMenu = true }) {
                         Icon(Icons.Default.MoreVert, contentDescription = "Opções")
                     }
                     DropdownMenu(
                         expanded = showMenu,
                         onDismissRequest = { showMenu = false }
                     ) {
                         DropdownMenuItem(
                             text = { Text("Trocar") },
                             onClick = {
                                 showMenu = false
                                 onReplaceExercise()
                             }
                         )
                         DropdownMenuItem(
                             text = { Text("Remover") },
                             onClick = {
                                 showMenu = false
                                 onRemoveExercise()
                             }
                         )
                         if (sets.isNotEmpty()) {
                             DropdownMenuItem(
                                 text = { Text("Remover série") },
                                 onClick = {
                                     showMenu = false
                                     // Removes the last set
                                     onDeleteSet(sets.last())
                                 }
                             )
                         }
                     }
                 }
             }
             
             Spacer(modifier = Modifier.height(16.dp))
             
             // Sets List
             sets.forEachIndexed { index, set ->
                 Row(
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(vertical = 6.dp),
                     verticalAlignment = Alignment.CenterVertically
                 ) {
                     Text(
                         "${index + 1}",
                         fontWeight = FontWeight.Bold,
                         modifier = Modifier.width(30.dp),
                         textAlign = TextAlign.Center
                     )
                     
                     Spacer(modifier = Modifier.weight(1f))
                     
                     // Weights Input
                     Box(
                         modifier = Modifier
                             .width(80.dp)
                             .height(36.dp)
                             .clip(RoundedCornerShape(8.dp))
                             .background(Color(0xFFEEEEEE)),
                         contentAlignment = Alignment.CenterStart
                     ) {
                         BasicTextField(
                             value = if (set.weight == 0.0) "" else set.weight.toString(),
                             onValueChange = { s ->
                                 val w = s.toDoubleOrNull() ?: 0.0
                                 onUpdateSet(set, w, set.reps)
                             },
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .padding(horizontal = 12.dp),
                             textStyle = TextStyle(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                             keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                             singleLine = true
                         )
                     }
                     Spacer(modifier = Modifier.width(4.dp))
                     Text("KG", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))

                     Spacer(modifier = Modifier.width(16.dp))

                     // Reps Input
                     Box(
                         modifier = Modifier
                             .width(80.dp)
                             .height(36.dp)
                             .clip(RoundedCornerShape(8.dp))
                             .background(Color(0xFFEEEEEE)),
                         contentAlignment = Alignment.CenterStart
                     ) {
                         BasicTextField(
                             value = if (set.reps == 0) "" else set.reps.toString(),
                             onValueChange = { s ->
                                 val r = s.toIntOrNull() ?: 0
                                 onUpdateSet(set, set.weight, r)
                             },
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .padding(horizontal = 12.dp),
                             textStyle = TextStyle(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                             keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                             singleLine = true
                         )
                     }
                     Spacer(modifier = Modifier.width(4.dp))
                     Text("REP.", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                 }
             }

             Spacer(modifier = Modifier.height(12.dp))

             // Add Set Button
             Button(
                 onClick = onAddSet,
                 colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F7), contentColor = Color.Black),
                 shape = RoundedCornerShape(12.dp),
                 modifier = Modifier.fillMaxWidth().height(44.dp),
                 elevation = ButtonDefaults.buttonElevation(0.dp)
             ) {
                 Text("+ Adicionar uma série")
             }
         }
     }
}

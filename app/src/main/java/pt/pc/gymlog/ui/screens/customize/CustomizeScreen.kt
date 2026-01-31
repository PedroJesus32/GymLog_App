package pt.pc.gymlog.ui.screens.customize

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.sp
import pt.pc.gymlog.viewmodel.WorkoutViewModel
import pt.pc.gymlog.viewmodel.CustomWorkoutUi

@Composable
fun CustomizeScreen(
    vm: WorkoutViewModel,
    onOpenWorkout: (Long) -> Unit
) {
    val workouts = vm.customWorkouts
    val primaryBlue = Color(0xFF4C6EF5)
    val backgroundGray = Color(0xFFF5F5F7)

    var showDeleteDialog by remember { mutableStateOf(false) }
    var workoutToDelete by remember { mutableStateOf<CustomWorkoutUi?>(null) }

    if (showDeleteDialog && workoutToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false 
                workoutToDelete = null
            },
            title = {
                Text(text = "Apagar treino")
            },
            text = {
                Text(text = "Quer mesmo apagar este treino?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        workoutToDelete?.let { vm.deleteCustomWorkout(it.id) }
                        showDeleteDialog = false
                        workoutToDelete = null
                    }
                ) {
                    Text("Sim", color = Color.Red)
                }
            },
            dismissButton = {
                 TextButton(
                    onClick = {
                        showDeleteDialog = false
                        workoutToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        containerColor = backgroundGray,
        floatingActionButton = {
            if (workouts.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        val id = vm.createCustomWorkout()
                        onOpenWorkout(id)
                    },
                    containerColor = primaryBlue,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Novo Treino")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // "Header" Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 12.dp, start = 20.dp),
            ) {
                Text(
                    text = "PERSONALIZAR",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    ),
                    color = Color.Black
                )
            }

            if (workouts.isEmpty()) {
                // Empty State Design
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 32.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Illustration Circle
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .background(Color(0xFFF5F5F7), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    tint = primaryBlue.copy(alpha = 0.8f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))

                            Text(
                                text = "Crie o seu primeiro\ntreino personalizado",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                ),
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = "Defina a sua própria rotina única",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(40.dp))

                            Button(
                                onClick = {
                                    val id = vm.createCustomWorkout()
                                    onOpenWorkout(id)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = primaryBlue),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                            ) {
                                Text(
                                    "+ INÍCIO",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            } else {
                // List State (styled nicely)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                items(workouts, key = { it.id }) { w ->
                    val exerciseCount = w.exerciseIds.size
                    // Calculate total sets
                    val totalSets = w.setsByExercise.values.sumOf { it.size }
                    
                    CustomWorkoutCard(
                        name = w.name,
                        exerciseCount = exerciseCount,
                        setCount = totalSets,
                        onClick = { onOpenWorkout(w.id) },
                        onDelete = {
                            workoutToDelete = w
                            showDeleteDialog = true
                        }
                    )
                }
                    
                    item {
                        Spacer(Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CustomWorkoutCard(
    name: String,
    exerciseCount: Int,
    setCount: Int,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    // Generate initials
    val initials = name.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.toString() }
        .joinToString("")
        .uppercase()

    val primaryBlue = Color(0xFF4C6EF5)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Let's rewrite the Row content to match Image 4 structure better
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
             Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$exerciseCount exercício${if (exerciseCount != 1) "s" else ""}",
                     style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                )
                 Spacer(modifier = Modifier.height(8.dp))
                 
                 Text(
                     text = "Toque para ver detalhes", // or passed first exercise name
                     style = MaterialTheme.typography.bodyMedium,
                     color = Color.Black
                 )
            }
            
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(80.dp) 
            ) {
                 Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (name.contains("2")) Color(0xFFE57373) else primaryBlue, // Mock color
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                     Text(
                         text = initials,
                         color = Color.White,
                         fontWeight = FontWeight.Bold,
                         fontSize = 14.sp
                     )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Set Count and Delete Icon Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                             imageVector = Icons.Default.Delete,
                             contentDescription = "Apagar",
                             tint = Color.Gray,
                             modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$setCount Set${if (setCount != 1) "s" else ""}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

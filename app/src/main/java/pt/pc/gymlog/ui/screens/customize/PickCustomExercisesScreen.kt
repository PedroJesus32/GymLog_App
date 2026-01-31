package pt.pc.gymlog.ui.screens.customize

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.pc.gymlog.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickCustomExercisesScreen(
    vm: WorkoutViewModel,
    workoutId: Long,
    replaceId: Long? = null,
    onBack: () -> Unit,
    onOpenDetail: (Long) -> Unit
) {
    val workout = vm.getCustomWorkout(workoutId)

    if (workout == null) {
        onBack()
        return
    }

    var query by remember { mutableStateOf(TextFieldValue("")) }
    var selected by remember { mutableStateOf(workout.exerciseIds.toSet()) }

    val filtered = vm.exercises.filter { ex ->
        val q = query.text.trim()
        if (q.isBlank()) true
        else ex.name.contains(q, ignoreCase = true) ||
                (ex.muscleGroup?.contains(q, ignoreCase = true) == true)
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F7),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(bottom = 8.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (replaceId != null) "Trocar exercício" else "Adicionar exercícios",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color.Gray)
                    }
                }

                // Search Bar
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Buscar exercícios") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF0F0F0),
                        unfocusedContainerColor = Color(0xFFF0F0F0),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Filter Tags
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Surface(
                         color = Color(0xFFE0E0E0),
                         shape = RoundedCornerShape(8.dp),
                         modifier = Modifier.height(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                            Text("Todos (${vm.exercises.size})", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        },
        bottomBar = {
             if (replaceId == null) {
                 Surface(
                     tonalElevation = 8.dp,
                     color = Color.White
                 ) {
                     Box(Modifier.padding(16.dp).navigationBarsPadding()) {
                         Button(
                             onClick = {
                                 vm.updateCustomWorkoutExercises(workoutId, newIds = selected.toList())
                                 onBack()
                             },
                             modifier = Modifier.fillMaxWidth().height(50.dp),
                             shape = RoundedCornerShape(12.dp),
                             colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C6EF5))
                         ) {
                             Text("ADICIONAR (${selected.size})", fontWeight = FontWeight.Bold)
                         }
                     }
                 }
             }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered, key = { it.id }) { ex ->
                val checked = ex.id in selected
                
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Selection Click Area (Left side)
                        Box(
                            modifier = Modifier
                                .size(40.dp) // Larger touch area for checkbox
                                .clip(CircleShape)
                                .clickable { 
                                     if (replaceId != null) {
                                         // REPLACE MODE
                                         vm.replaceExerciseInCustomWorkout(workoutId, replaceId, ex.id)
                                         onBack()
                                     } else {
                                         // ADD MODE
                                         selected = if (checked) selected - ex.id else selected + ex.id
                                     }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (replaceId == null) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (checked) Color(0xFF4C6EF5) else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .then(
                                            if (!checked) Modifier.border(2.dp, Color.Gray.copy(0.5f), CircleShape) else Modifier
                                        )
                                )
                            } else {
                                // In replace mode, show an icon like "Swap" or just nothing/radio?
                                // Let's show a radio-like circle or just an arrow.
                                // Minimalist: empty circle
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .border(2.dp, Color.Gray.copy(0.5f), CircleShape)
                                )
                            }
                        }
                        
                        // Details Click Area (Rest of the card)
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onOpenDetail(ex.id) }
                                .padding(start = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Image Placeholder
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Color(0xFFF5F5F7), RoundedCornerShape(8.dp))
                            )
    
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column {
                                Text(ex.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                ex.muscleGroup?.let {
                                    Text(it, style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package pt.pc.gymlog.ui.screens.today

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.pc.gymlog.viewmodel.WorkoutViewModel
import pt.pc.gymlog.viewmodel.SetEntryUi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WorkoutSummaryScreen(
    vm: WorkoutViewModel,
    day: Int,
    onClose: () -> Unit
) {
    val planDay = vm.planDays.find { it.dayNumber == day }
    val planTitle = planDay?.title ?: "Treino do Dia"
    
    val todayIds = vm.todayExerciseIds(day)
    val sets = vm.sets(day)
    
    // Stats Calculation
    val volume = sets.filter { it.weight > 0 }.sumOf { it.weight * it.reps }
    // As we don't store duration yet, we'll mock it or leave placeholder. 
    // Screenshot shows "00:42". In a real app we'd track this. 
    // For now, I'll put a placeholder or basic estimation (e.g. 2 mins per set).
    val estimatedDurationMinutes = sets.size * 2 
    val durationFormatted = String.format("%02d:%02d", estimatedDurationMinutes / 60, estimatedDurationMinutes % 60)
    
    val currentDate = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date())
    val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

    val exercises = vm.exercises.filter { it.id in todayIds }

    // Colors
    val headerBlue = Color(0xFF4C6EF5) // Matches Screenshot Blue
    val detailTextColor = Color.Black
    val surfaceColor = Color.White
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F7)) // Light Gray BG
    ) {
        // --- HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp) // Large header
                .background(headerBlue)
        ) {
             // Circles or Decor? (Simulated by Icon placement)
             Column(
                 modifier = Modifier
                     .align(Alignment.CenterStart)
                     .padding(start = 24.dp, bottom = 40.dp)
             ) {
                 Text(
                     text = "Bom trabalho!",
                     style = MaterialTheme.typography.titleMedium,
                     color = Color.White.copy(alpha = 0.9f)
                 )
                 Spacer(modifier = Modifier.height(4.dp))
                 Text(
                     text = "TREINO\nCONCLUÍDO!",
                     style = MaterialTheme.typography.displaySmall.copy(
                         fontWeight = FontWeight.Black,
                         lineHeight = 36.sp
                     ),
                     color = Color.White
                 )
             }
             
             // Trophy Icon (Right side)
             Icon(
                 imageVector = Icons.Default.EmojiEvents,
                 contentDescription = "Troféu",
                 tint = Color(0xFFFFC107), // Gold
                 modifier = Modifier
                     .size(120.dp)
                     .align(Alignment.BottomEnd)
                     .offset(x = 10.dp, y = 10.dp) // Slight overlay
                     .padding(bottom = 20.dp, end = 10.dp)
             )
        }
        
        // --- CONTENT ---
        // Overlap the header with a Card/Surface? 
        // Screenshot shows a rounded white container starting below header text.
        
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-20).dp), // Slight overlap upwards
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = surfaceColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Workout Title
                Text(
                    text = "Dia $day-$planTitle",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = detailTextColor
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        value = "${volume.toInt()} kg",
                        label = "Volume"
                    )
                    StatItem(
                        value = durationFormatted,
                        label = "Duração"
                    )
                    StatItem(
                        value = currentDate,
                        label = currentTime
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color.LightGray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(24.dp))
                
                // Exercise List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    items(exercises) { ex ->
                        val exSets = sets.filter { it.exerciseId == ex.id }.sortedBy { it.setNumber }
                        if (exSets.isNotEmpty()) {
                            ExerciseSummaryItem(ex.name, exSets)
                        }
                    }
                    
                    item {
                        Spacer(Modifier.height(80.dp))
                    }
                }
                
                // Finish Button
                Button(
                    onClick = onClose,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = headerBlue)
                ) {
                    Text(
                        "ACABADO",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun ExerciseSummaryItem(name: String, sets: List<SetEntryUi>) {
    Column {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        sets.forEachIndexed { index, set ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                 Row {
                     // Black Circle Number
                     Box(
                         modifier = Modifier
                            .size(20.dp)
                            .background(Color.Black, androidx.compose.foundation.shape.CircleShape),
                         contentAlignment = Alignment.Center
                     ) {
                         Text(
                             "${set.setNumber}", 
                             color = Color.White, 
                             style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                         )
                     }
                     Spacer(modifier = Modifier.width(8.dp))
                     Text(
                         "${set.weight.toInt()} kg x ${set.reps}", 
                         style = MaterialTheme.typography.bodyLarge,
                         fontWeight = FontWeight.Medium
                     )
                 }
                 
                 // 1RM Calc: w * (1 + r/30)
                 val oneRm = if (set.weight > 0) set.weight * (1 + set.reps / 30.0) else 0.0
                 Text(
                     text = "1 RM = ${String.format("%.1f", oneRm)} kg",
                     style = MaterialTheme.typography.bodyMedium,
                     color = Color.Gray
                 )
            }
        }
    }
}

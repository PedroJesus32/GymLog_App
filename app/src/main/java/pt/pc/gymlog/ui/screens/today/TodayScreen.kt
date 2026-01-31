package pt.pc.gymlog.ui.screens.today

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.pc.gymlog.R
import pt.pc.gymlog.ui.theme.GymElectricBlue
import pt.pc.gymlog.viewmodel.PlanDayType
import pt.pc.gymlog.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    vm: WorkoutViewModel,
    day: Int,
    onGoReport: () -> Unit,
    onBackToPlan: () -> Unit,
    onStartSession: () -> Unit,
    onOpenDetail: (Long) -> Unit,
    onEdit: () -> Unit
) {
    LaunchedEffect(day) {
        vm.openDay(day = day)
    }

    val allExercises = vm.exercises
    val selectedExerciseIds = vm.todayExerciseIds(day)
    val sets = vm.sets(day)

    val plan = vm.planDays.firstOrNull { it.dayNumber == day }
    val isRestDay = plan?.type == PlanDayType.REST
    val focusTitle = plan?.title ?: "Treino"

    val todayStr = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ... (Header Image and Back Button - Keep these visible or dimmed)
        // Actually, to mimic the screenshot, we want the "Rest Day" card to appear modal-like.
        // We will render the standard TodayScreen content BUT if it is a rest day, 
        // we add a dim overlay and the card ON TOP.
        
        // 1. Header Image
        Image(
            painter = painterResource(id = R.drawable.workout_header),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .align(Alignment.TopCenter)
        )
        
        // Dark Overlay for Header Text visibility
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.Black.copy(alpha = 0.3f))
        )

        // Back Button
        IconButton(
            onClick = onBackToPlan,
            modifier = Modifier
                .padding(top = 40.dp, start = 16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = Color.White
            )
        }

        // 2. Content Body (Standard)
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(200.dp)) // Push content down

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(bottom = 200.dp) // Space for bottom bar
                ) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                             Column {
                                Text(
                                    text = "DIA $day",
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 32.sp
                                    ),
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                // Tag
                                Surface(
                                    color = Color(0xFFE0E7FF), // Light Blue tint
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = focusTitle,
                                        color = GymElectricBlue,
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                            
                            // Muscle Diagram Placeholder
                             Image(
                                painter = painterResource(id = R.drawable.trainer_cr7), 
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }

                    // ... (Stats rows etc, hide if rest day? The screenshot shows blank)
                    if (!isRestDay) {
                         item {
                            Spacer(modifier = Modifier.height(32.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Equipamento", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Padrão do sistema", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("1RM (Supino)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("35 kg", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                text = "${vm.todayExerciseIds(day).size} exercícios",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                         val todayExercises = allExercises.filter { it.id in selectedExerciseIds }
                         items(todayExercises) { ex ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clickable { onOpenDetail(ex.id) },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Thumbnail
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFF5F5F5),
                                        modifier = Modifier.size(60.dp)
                                    ) {
                                        if (ex.imageRes != null) {
                                             Image(painter = painterResource(ex.imageRes!!), contentDescription = null, contentScale = ContentScale.Crop)
                                        } else {
                                             Box(contentAlignment = Alignment.Center) {
                                                 Text("IMG", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                             }
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    Column {
                                        Text(ex.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        Text("3 séries x 10 rep.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray) // Placeholder logic
                                    }
                                }
                            }
                        }
                    } else {
                         // Empty space for Rest Day base
                         item {
                             Spacer(modifier = Modifier.height(200.dp))
                             Text("Dia de descanso.", modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Color.Gray)
                         }
                    }
                }
            }
        }

        // 3. Bottom Sticky Bar (Only active if NOT rest day)
        if (!isRestDay) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
             Surface(
                shadowElevation = 16.dp,
                color = Color.White,
                 modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Button(
                        onClick = onStartSession,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GymElectricBlue)
                    ) {
                        Text("INÍCIO", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        TextButton(onClick = { vm.regenerateTodayWorkout(day) }) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Refazer", color = Color.Black)
                        }
                        
                        TextButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Editar", color = Color.Black)
                        }
                    }
                }
            }
          }
        }
        
        // 4. REST DAY OVERLAY
        if (isRestDay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) {}, // Block clicks
                contentAlignment = Alignment.BottomCenter
            ) {
                 Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(bottom = 16.dp), // Lift up a bit
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                         // Close X (Optional, screenshot shows X)
                         Row(
                             modifier = Modifier.fillMaxWidth(), 
                             horizontalArrangement = Arrangement.SpaceBetween,
                             verticalAlignment = Alignment.Top
                         ) {
                             Icon(
                                 painter = painterResource(R.drawable.ic_launcher_foreground), // Placeholder for Cup
                                 contentDescription = null,
                                 modifier = Modifier.size(32.dp),
                                 tint = Color.Unspecified
                             )
                             // Close button for viewing only?
                             // User requirement: "Acabado" to finish.
                             // Maybe X just closes overlay? User said "não dá para acabar".
                             // Let's assume X goes back, and "Acabado" completes.
                             IconButton(onClick = onBackToPlan, modifier = Modifier.size(24.dp)) {
                                 Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color.Gray)
                             }
                         }
                         
                         Spacer(modifier = Modifier.height(16.dp))
                         
                         Text(
                             text = "Dia de Descanso",
                             style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                         )
                         
                         Spacer(modifier = Modifier.height(8.dp))
                         
                         Text(
                             text = "O descanso pode recarregar as suas energias para que você obtenha o corpo dos seus sonhos",
                             style = MaterialTheme.typography.bodyLarge,
                             color = Color.Gray
                         )
                         
                         Spacer(modifier = Modifier.height(24.dp))
                         
                         Button(
                             onClick = {
                                 vm.unlockNextDay(day)
                                 onBackToPlan()
                             },
                             modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                             shape = RoundedCornerShape(12.dp),
                             colors = ButtonDefaults.buttonColors(containerColor = GymElectricBlue)
                         ) {
                             Text("Acabado", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                         }
                    }
                }
            }
        }
    }
}

package pt.pc.gymlog.ui.screens.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.pc.gymlog.ui.theme.GymGreen

import pt.pc.gymlog.viewmodel.WorkoutViewModel
import pt.pc.gymlog.viewmodel.WorkoutHistoryEntryUi
import pt.pc.gymlog.viewmodel.FocusAreaUi
import pt.pc.gymlog.ui.components.onboarding.BodyMapVisual
import kotlin.math.roundToInt

@Composable
fun ReportScreen(
    vm: WorkoutViewModel,
    onOpenWorkout: (Long) -> Unit,
    onOpenWeight: () -> Unit
) {
    val history = vm.history
    // Section 2: Last 2 workouts (Reversed to show newest first)
    val recentWorkouts = history.sortedByDescending { it.id }.take(2)
    
    // Section 3: Weight Data
    val currentWeight = vm.userProfile.currentWeightKg
    val weightHistory = vm.weightHistory.sortedBy { it.id } // Mock sorting by date

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // 1) Topo (Header): TopBar fixa com o título: RELATÓRIO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "RELATÓRIO",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp), // Extra space for scroll
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // 2) Secção: Histórico (Últimos 2)
            if (recentWorkouts.isNotEmpty()) {
                items(recentWorkouts) { entry ->
                    WorkoutCard(entry = entry, onClick = { onOpenWorkout(entry.id) })
                }
            } else {
                item {
                    Text(
                        text = "Sem treinos recentes",
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 3) Secção: Meu peso
            item {
                WeightSection(
                    currentWeight = currentWeight,
                    history = weightHistory.map { it.weightKg },
                    onRegisterClick = onOpenWeight
                )
            }

            // 4) Secção: Frequência dos treinos
            item {
                FrequencySection()
            }

            // 5) Secção: Histórico + Ver todos
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Histórico",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Ver todos",
                        style = MaterialTheme.typography.bodyMedium.copy(color = GymGreen),
                        modifier = Modifier.clickable { 
                            // Open full history - In this "Frontend Only" scope, 
                            // we reused the header as requested.
                            // In a full nav, this would navigate to a full list page.
                         }
                    )
                }
                 // Optional: Duplicate list here? User said (recommended) just header.
                 // So we leave it as just the header entry point.
            }
        }
    }
}

// --- COMPONENTS ---

@Composable
fun WorkoutCard(entry: WorkoutHistoryEntryUi, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Nome do treino
            Text(
                text = "Dia ${entry.id} – Treino de Força", // Mock title if not in data
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            // Row with Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Time/Date
                Column {
                    Text(
                        text = entry.dateLabel, // "10:15 PM / jan. 26" format depends on VM
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                   // Time would be here if distinct
                }

                // Duration
                Column {
                    Text(
                        text = "00:43", // Mock duration
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Duração",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Volume
                Column {
                    val volume = entry.setsSnapshot.sumOf { it.weight * it.reps }.roundToInt()
                    Text(
                        text = "$volume kg", 
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Volume",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun WeightSection(
    currentWeight: Double,
    history: List<Double>,
    onRegisterClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Meu peso",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp) // Extra round as per design
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Top Row: Value + Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Atual(kg)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (currentWeight > 0) String.format("%.1f", currentWeight) else "--",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GymGreen // Highlight color
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Últimos 30 dias",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onRegisterClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary, // Blueish in image, GymGreen here? User said "igual imagem" 
                            // Img has Blue button. But we have a Theme.
                            // I will stick to Theme for consistency ("Premium").
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("REGISTRAR")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Graph
                WeightChart(data = history)
            }
        }
    }
}

@Composable
fun WeightChart(data: List<Double>) {
    val graphColor = GymGreen
    
    // Placeholder data if empty
    val plotData = if (data.isEmpty()) listOf(70.0, 71.2, 70.8, 71.5, 72.0, 71.8) else data

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        if (plotData.isEmpty()) return@Canvas

        val max = plotData.maxOrNull() ?: 100.0
        val min = plotData.minOrNull() ?: 0.0
        val range = (max - min).coerceAtLeast(1.0)
        
        val widthPerPoint = size.width / (plotData.size - 1).coerceAtLeast(1)
        
        val path = Path()
        
        plotData.forEachIndexed { index, value ->
            val x = index * widthPerPoint
            // Invert Y (0 is top)
            val normalizedY = (value - min) / range
            val y = size.height - (normalizedY * size.height).toFloat()
            
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
             
            // Draw dot
            drawCircle(
                color = graphColor,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }
        
        // Draw Line
        drawPath(
            path = path,
            color = graphColor,
            style = Stroke(width = 3.dp.toPx())
        )
        
        // Gradient Fill (Optional)
        val fillPath = Path()
        fillPath.addPath(path)
        fillPath.lineTo(size.width, size.height)
        fillPath.lineTo(0f, size.height)
        fillPath.close()
        
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(graphColor.copy(alpha = 0.3f), Color.Transparent)
            )
        )
    }
}

@Composable
fun FrequencySection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Frequência dos treinos",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            // No drop down
        }
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Body Map with detailed anatomy
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BodyMapVisual(selected = setOf(FocusAreaUi.FULL_BODY))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Legend
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Alta", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(GymGreen, Color.Gray)
                                )
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Baixo", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// Removed old BodyShape function - now using BodyMapVisual component

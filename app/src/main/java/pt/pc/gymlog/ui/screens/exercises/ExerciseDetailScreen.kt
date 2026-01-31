package pt.pc.gymlog.ui.screens.exercises

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.pc.gymlog.R
import pt.pc.gymlog.ui.theme.GymElectricBlue
import pt.pc.gymlog.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    vm: WorkoutViewModel,
    exerciseId: Long,
    onClose: () -> Unit
) {
    val exercise = remember(exerciseId) { vm.exercises.find { it.id == exerciseId } }

    if (exercise == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Exercício não encontrado.")
            Button(onClick = onClose) { Text("Voltar") }
        }
        return
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f) // Occupy most of the height but leave some space
            .clip(RoundedCornerShape(16.dp)),
        color = Color.White,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header with Back Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.Black
                    )
                }
                
                 Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "INSTRUÇÕES",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        ),
                        color = Color.Black
                    )
                    // Blue underline
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .width(48.dp)
                            .height(3.dp)
                            .background(GymElectricBlue, RoundedCornerShape(2.dp))
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f) // Push content up, button down
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                // Image Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth().aspectRatio(1.3f)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (exercise.imageRes != null) {
                             Image(
                                painter = painterResource(id = exercise.imageRes), 
                                contentDescription = exercise.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Placeholder
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFF2F2F7)),
                                contentAlignment = Alignment.Center
                            ) {
                                 Image(
                                    painter = painterResource(id = R.drawable.trainer_cr7), 
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop, // Changed to Crop to fill like the screenshot
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        // Play icon
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Play",
                            tint = GymElectricBlue,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Title
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Details Table
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "ÁREA DE FOCO",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        modifier = Modifier.width(130.dp)
                    )
                    Text(
                        text = exercise.muscleGroup ?: "Geral",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "EQUIPAMENTO",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        modifier = Modifier.width(130.dp)
                    )
                    Text(
                        text = exercise.equipment ?: "Nenhum",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Instructions text
                if (!exercise.instructions.isNullOrBlank()) {
                     Text(
                        text = exercise.instructions,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                } else {
                    Text(
                        text = "Lie on a bench with feet on the floor. Hold dumbbells directly above your chest with arms fully extended. Lower the dumbbells to your chest in a controlled motion.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            // Bottom Button
            Button(
                onClick = onClose,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GymElectricBlue
                )
            ) {
                Text(
                    "PRONTO",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

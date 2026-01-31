package pt.pc.gymlog.ui.screens.plan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.pc.gymlog.R
import pt.pc.gymlog.viewmodel.PlanDayType
import pt.pc.gymlog.viewmodel.WorkoutViewModel
import pt.pc.gymlog.viewmodel.GenderUi

@Composable
fun PlanScreen(
    vm: WorkoutViewModel,
    onOpenDay: (Int) -> Unit
) {
    val unlockedMax = vm.unlockedMaxDay
    val days = vm.planDays
    val gender = vm.userProfile.gender // Reactive state access

    // Garante que o plano existe
    LaunchedEffect(days.size) {
        if (days.isEmpty()) vm.regeneratePlan()
    }

    val primaryBlue = Color(0xFF4C6EF5)
    val backgroundGray = Color(0xFFF5F5F7)

    Scaffold(
        containerColor = backgroundGray
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Header: PLANO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PLANO",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(3.dp)
                        .background(primaryBlue, RoundedCornerShape(2.dp))
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero Card
                item {
                    HeroCard(gender)
                }

                // Days List
                items(days, key = { it.dayNumber }) { d ->
                    val done = d.dayNumber < unlockedMax
                    val locked = d.dayNumber > unlockedMax
                    val isCurrent = d.dayNumber == unlockedMax

                    DayCard(
                        dayNumber = d.dayNumber,
                        type = d.type,
                        title = d.title,
                        status = when {
                            locked -> DayStatus.LOCKED
                            isCurrent -> DayStatus.CURRENT
                            else -> DayStatus.DONE
                        },
                        onClick = { onOpenDay(d.dayNumber) },
                        primaryBlue = primaryBlue
                    )
                }
                
                // Add some bottom padding for the FAB/NavBar
                item {
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun HeroCard(gender: GenderUi) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            val imageRes = if (gender == GenderUi.MALE) R.drawable.trainer_cr7 else R.drawable.workout_header
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Workout Hero",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Dark overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )

            // Text Content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text(
                    text = "FORTALECIMENTO\nMUSCULAR",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    ),
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "30 Dias",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

enum class DayStatus { LOCKED, CURRENT, DONE }

@Composable
fun DayCard(
    dayNumber: Int,
    type: PlanDayType,
    title: String,
    status: DayStatus,
    onClick: () -> Unit,
    primaryBlue: Color
) {
    val isCurrent = status == DayStatus.CURRENT
    val isLocked = status == DayStatus.LOCKED
    
    val cardColor = if (isCurrent) primaryBlue else Color.White
    val contentColor = if (isCurrent) Color.White else Color.Black
    
    val displayTitle = if (type == PlanDayType.REST) "Dia de Descanso" else "Dia $dayNumber"
    val displaySubtitle = if (type == PlanDayType.REST) "" else title

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrent) 8.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isLocked, onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = contentColor
                )
                if (displaySubtitle.isNotEmpty()) {
                    Text(
                        text = displaySubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
            }

            when (status) {
                DayStatus.LOCKED -> {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Bloqueado",
                        tint = Color.Black
                    )
                }
                DayStatus.CURRENT -> {
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = primaryBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Início",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                DayStatus.DONE -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Concluído",
                        tint = primaryBlue // Or green? Keeping consistent with blue theme or green for done.
                        // Screenshot doesn't show done state clearly, but check circle is standard.
                        // Let's use Green for done to be clear.
                    )
                }
            }
        }
    }
}

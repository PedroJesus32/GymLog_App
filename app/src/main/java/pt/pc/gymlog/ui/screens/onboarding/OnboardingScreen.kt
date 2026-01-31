package pt.pc.gymlog.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import pt.pc.gymlog.R
import pt.pc.gymlog.ui.theme.GymGreen
import pt.pc.gymlog.ui.components.onboarding.BodyMapVisual

import pt.pc.gymlog.viewmodel.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    vm: WorkoutViewModel,
    onFinish: () -> Unit
) {
    // Steps: 
    // 1. Gender 2. Goal 3. Place 4. Focus 5. Height 6. Weight 7. Target 8. Challenge 9. 1RM 10. Frequency 11. Loading
    var step by remember { mutableStateOf(1) }
    val totalSteps = 11
    
    val profile = vm.userProfile
    val settings = vm.userSettings
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    if (step > 1 && step < 11) { // Hide back on loading
                        IconButton(onClick = { step-- }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            if (step < totalSteps) { // Hide button on Loading step (11)
                Button(
                    onClick = {
                        if (step < totalSteps) step++ 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Próximo", 
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Progress Bar (Hide on loading?) - Keeping for continuity until last step
            if (step < 11) {
                LinearProgressIndicator(
                    progress = { step.toFloat() / (totalSteps - 1).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = GymGreen,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            // Content
            AnimatedContent(
                targetState = step,
                label = "onboarding_step"
            ) { currentStep ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (currentStep) {
                        1 -> GenderStep(profile.gender) { vm.updateGender(it) }
                        2 -> GoalStep(profile.goal) { vm.updateGoal(it) }
                        3 -> PlaceStep(profile.place) { vm.updatePlace(it) }
                        4 -> FocusStep(profile.focusAreas) { vm.updateFocusAreas(it) }
                        5 -> HeightStep(profile.heightCm, profile.gender) { vm.updateHeightCm(it) }
                        6 -> WeightStep(profile.currentWeightKg, profile.heightCm, profile.gender) { vm.updateCurrentWeightKg(it) }
                        7 -> TargetWeightStep(profile.targetWeightKg, profile.currentWeightKg, profile.gender) { vm.updateTargetWeightKg(it) }
                        8 -> ChallengeStep(profile.challenge) { vm.updateChallenge(it) }
                        9 -> OneRmStep(profile.oneRmSupinoKg, { vm.updateOneRmSupinoKg(it) }, { step++ })
                        10 -> FrequencyStep(settings.workoutsPerWeek, settings.restDay) { w, r -> 
                            vm.updateSettings(w, r, settings.unitSystem)
                        }
                        11 -> LoadingStep(profile.gender) { 
                            vm.completeOnboarding()
                            onFinish()
                        }
                    }
                }
            }
        }
    }
}
// --- STEP 1: GENDER ---
@Composable
fun GenderStep(selected: GenderUi, onSelect: (GenderUi) -> Unit) {
    Text(
        text = "Qual é o seu sexo?",
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Saber o seu gênero nos ajudará a adaptar a intensidade para você com base nas diferentes taxas metabólicas.",
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(16.dp)
    )
    Spacer(modifier = Modifier.height(32.dp))
    
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        GenderCard(GenderUi.MALE, selected == GenderUi.MALE) { onSelect(GenderUi.MALE) }
        GenderCard(GenderUi.FEMALE, selected == GenderUi.FEMALE) { onSelect(GenderUi.FEMALE) }
    }
}

@Composable
fun RowScope.GenderCard(gender: GenderUi, isSelected: Boolean, onClick: () -> Unit) {
    val icon = if (gender == GenderUi.MALE) Icons.Default.Male else Icons.Default.Female
    val containerColor = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface
    val borderColor = if (isSelected) GymGreen else Color.LightGray

    Card(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(0.75f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                 imageVector = icon,
                 contentDescription = null,
                 modifier = Modifier.size(100.dp),
                 tint = if (isSelected) GymGreen else Color.Gray
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                gender.label,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface 
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) GymGreen else Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Default.Check, 
                        contentDescription = null, 
                        tint = Color.White, 
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// --- STEP 2: GOAL ---
@Composable
fun GoalStep(selected: GoalUi, onSelect: (GoalUi) -> Unit) {
    Text(
        text = "Qual é o seu principal objetivo?",
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(32.dp))
    
    GoalUi.values().forEach { goal ->
        SelectableCard(
            text = goal.label,
            icon = getGoalIcon(goal),
            isSelected = selected == goal,
            onClick = { onSelect(goal) }
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

fun getGoalIcon(goal: GoalUi): androidx.compose.ui.graphics.vector.ImageVector {
    return when(goal) {
        GoalUi.STRONGER -> Icons.Default.FitnessCenter
        GoalUi.MUSCLE -> Icons.Default.AccessibilityNew
        GoalUi.LEAN -> Icons.Default.DirectionsRun
        GoalUi.LOSE_WEIGHT -> Icons.Default.MonitorWeight
        GoalUi.HEALTH -> Icons.Default.Favorite
        GoalUi.PERFORMANCE -> Icons.Default.Bolt
    }
}

// --- STEP 3: PLACE ---
@Composable
fun PlaceStep(selected: PlaceUi?, onSelect: (PlaceUi) -> Unit) {
    Text(
        text = "Onde você prefere se exercitar?",
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(32.dp))
    
    PlaceUi.values().forEach { place ->
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onSelect(place) },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = if (selected == place) BorderStroke(2.dp, GymGreen) else BorderStroke(1.dp, Color.LightGray),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(place.label, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(4.dp))
                Text(place.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

// --- STEP 4: FOCUS ---
@Composable
fun FocusStep(selected: Set<FocusAreaUi>, onSelect: (Set<FocusAreaUi>) -> Unit) {
    Text(
        text = "Selecione as áreas que deseja ter como foco",
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(24.dp))
    
    val options = FocusAreaUi.values().toList()
    val chunks = options.chunked(3)
    
    chunks.forEach { rowItems ->
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            rowItems.forEach { item ->
                val isSelected = item in selected
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newSet = if (isSelected) selected - item else selected + item
                        onSelect(newSet)
                    },
                    label = { Text(item.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GymGreen,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Dynamic Body Map
    Box(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        BodyMapVisual(selected)
        Text("Mapa Corporal", modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp), color = Color.Gray, fontSize = 12.sp)
    }
}

// BodyMapVisual removed (moved to separate component)


// --- STEP 5: HEIGHT ---
@Composable
fun HeightStep(current: Double, gender: GenderUi, onChange: (Double) -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Qual é sua altura?",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "${current.toInt()} cm",
            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        Slider(
            value = current.toFloat(),
            onValueChange = { onChange(it.toDouble()) },
            valueRange = 100f..250f,
            colors = SliderDefaults.colors(thumbColor = GymGreen, activeTrackColor = GymGreen)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Man/Woman Visual
        Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxWidth().height(250.dp)) {
             Icon(
                imageVector = if (gender == GenderUi.MALE) Icons.Default.Male else Icons.Default.Female,  // Using Generic for now, ideally specific asset
                contentDescription = null,
                modifier = Modifier.size(200.dp),
                tint = Color.LightGray
            )
             // Ruler lines capability could go here
        }
    }
}

// --- STEP 6: WEIGHT ---
@Composable
fun WeightStep(current: Double, heightCm: Double, gender: GenderUi, onChange: (Double) -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Qual é seu peso atual?",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = String.format("%.1f kg", current),
            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        Slider(
            value = current.toFloat(),
            onValueChange = { onChange(it.toDouble()) },
            valueRange = 30f..200f,
            colors = SliderDefaults.colors(thumbColor = GymGreen, activeTrackColor = GymGreen)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // BMI Card
        val bmi = if (heightCm > 0) current / ((heightCm / 100.0) * (heightCm / 100.0)) else 0.0
        val bmiFormatted = String.format("%.1f", bmi)
        
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("IMC ATUAL", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(bmiFormatted, color = GymGreen, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
         Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxWidth().height(200.dp)) {
             Icon(
                imageVector = if (gender == GenderUi.MALE) Icons.Default.Male else Icons.Default.Female,
                contentDescription = null,
                modifier = Modifier.size(180.dp),
                tint = Color.LightGray
            )
        }
    }
}

// --- STEP 7: TARGET WEIGHT ---
@Composable
fun TargetWeightStep(target: Double, current: Double, gender: GenderUi, onChange: (Double) -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        // Ensure accurate initial value
        val effectiveTarget = if (target == 0.0) current else target
        val diff = effectiveTarget - current
        
        // Dynamic messages logic [Kept same]
        val (title, message, color) = when {
            java.lang.Math.abs(diff) < 1.0 -> Triple("MANTER A FORMA", "Ótimo! Manter-se saudável é uma jornada contínua.", GymGreen)
            diff < -5.0 -> Triple("OPÇÃO COM BASTANTE SUOR!", "Vai exigir dedicação, mas os resultados valerão a pena!", Color(0xFFFFA500))
            diff < 0 -> Triple("OBJETIVO REALISTA!", "Uma meta alcançável e saudável para o seu ritmo.", Color(0xFF4CAF50))
            diff > 5.0 -> Triple("GANHO DE MASSA INTENSO!", "Foco na hipertrofia e alimentação forte!", Color(0xFFFFA500))
            else -> Triple("GANHO DE MASSA MODERADO", "Construindo músculos passo a passo.", Color(0xFF4CAF50))
        }
    
        Text(
            text = "Qual é sua meta de peso?",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(verticalAlignment = Alignment.Bottom) {
             Text(text = String.format("%.1f ", current), style = MaterialTheme.typography.titleMedium, color = Color.Gray)
             Text(text = String.format("► %.1f kg", effectiveTarget), style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold))
        }
       
        Spacer(modifier = Modifier.height(32.dp))
        Slider(
            value = effectiveTarget.toFloat(),
            onValueChange = { onChange(it.toDouble()) },
            valueRange = 30f..200f,
            colors = SliderDefaults.colors(thumbColor = GymGreen, activeTrackColor = GymGreen)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(title, color = color, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(message, style = MaterialTheme.typography.bodyMedium)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
         Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxWidth().height(200.dp)) {
             Icon(
                imageVector = if (gender == GenderUi.MALE) Icons.Default.Male else Icons.Default.Female,
                contentDescription = null,
                modifier = Modifier.size(180.dp),
                tint = Color.LightGray
            )
        }
    }
}

// --- STEP 8: CHALLENGE ---
@Composable
fun ChallengeStep(selected: ChallengeUi?, onSelect: (ChallengeUi) -> Unit) {
    Text(
        text = "Qual é o seu maior desafio durante o treino?",
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(32.dp))
    
    ChallengeUi.values().forEach { challenge ->
        SelectableCard(
            text = challenge.label,
            icon = getChallengeIcon(challenge),
            isSelected = selected == challenge,
            onClick = { onSelect(challenge) }
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

fun getChallengeIcon(challenge: ChallengeUi): androidx.compose.ui.graphics.vector.ImageVector {
    return when(challenge) {
        ChallengeUi.MOTIVATION -> Icons.Default.SentimentDissatisfied
        ChallengeUi.GUIDANCE -> Icons.Default.HelpOutline
        ChallengeUi.BOREDOM -> Icons.Default.Timer
    }
}

// --- STEP 9: 1RM ---
@Composable
fun OneRmStep(current: Int, onChange: (Int) -> Unit, onSkip: () -> Unit) {
    // Local state for calculation
    var weightLifted by remember { mutableStateOf("30") }
    var reps by remember { mutableStateOf("5") }
    
    // Auto-calculate 1RM using Epley formula: w * (1 + r/30)
    LaunchedEffect(weightLifted, reps) {
        val w = weightLifted.toDoubleOrNull() ?: 0.0
        val r = reps.toIntOrNull() ?: 0
        if (w > 0 && r > 0) {
            val oneRm = w * (1 + r / 30.0)
            onChange(oneRm.roundToInt())
        }
    }

    Text(
        text = "Estime a sua 1RM",
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    
    Image(
        painter = painterResource(id = R.drawable.bench_press_illustration),
        contentDescription = "Supino",
        modifier = Modifier.size(120.dp),
        contentScale = ContentScale.Fit
    )
    
    Spacer(modifier = Modifier.height(24.dp))

    TextButton(onClick = onSkip) {
        Text("Sou iniciante", color = GymGreen, style = MaterialTheme.typography.titleMedium)
    }

    Spacer(modifier = Modifier.height(24.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Referência", style = MaterialTheme.typography.bodyLarge)
        Text("Supino", fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(8.dp))
    
    // Editable Inputs
    OneRmInput("Ao levantar", weightLifted, "kg") { weightLifted = it }
    Spacer(modifier = Modifier.height(8.dp))
    OneRmInput("Até cansar, consigo fazer", reps, "Rep.") { reps = it }
    
    Spacer(modifier = Modifier.height(24.dp))
    Text("Sua 1RM (Supino)", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
    Text("$current kg", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold))
    
    Slider(
        value = current.toFloat(),
        onValueChange = { onChange(it.toInt()) },
        valueRange = 0f..200f,
         colors = SliderDefaults.colors(thumbColor = GymGreen, activeTrackColor = GymGreen)
    )
}

@Composable
fun OneRmInput(label: String, value: String, suffix: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)).padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = value,
                onValueChange = { if (it.length <= 5 && it.all { c -> c.isDigit() || c == '.' }) onValueChange(it) },
                textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.End),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(60.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(suffix, fontWeight = FontWeight.Bold)
        }
    }
}


// --- STEP 10: FREQUENCY ---
@Composable
fun FrequencyStep(
    workoutsPerWeek: Int, 
    restDay: WorkoutViewModel.WeekDayUi,
    onChange: (Int, WorkoutViewModel.WeekDayUi) -> Unit
) {
    Text(
        text = "Com que frequência você treina?",
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "A consistência é a chave para o progresso.",
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(32.dp))
    
    // Option 2..7
    val options = listOf(2, 3, 4, 5, 6, 7)
    val chunked = options.chunked(3)
    
    chunked.forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            row.forEach { num ->
                 FreqCard(
                     number = num, 
                     isSelected = workoutsPerWeek == num,
                     onClick = { onChange(num, restDay) }
                 )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = "Escolha o dia de início da sua semana de treino.", 
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    )
    Spacer(modifier = Modifier.height(16.dp))
    
    val days = WorkoutViewModel.WeekDayUi.values().toList()
    val dayChunks = days.chunked(2) // 2 columns like in the reference image
    
    dayChunks.forEach { rowDays ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp), 
            modifier = Modifier.fillMaxWidth()
        ) {
             rowDays.forEach { d ->
                 Box(modifier = Modifier.weight(1f)) {
                     SelectableCard(
                         text = d.label,
                         isSelected = restDay == d,
                         onClick = { onChange(workoutsPerWeek, d) }
                     )
                 }
             }
             // Fill empty space if odd number
             if (rowDays.size < 2) {
                 Spacer(modifier = Modifier.weight(1f))
             }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun RowScope.FreqCard(number: Int, isSelected: Boolean, onClick: () -> Unit) {
     Card(
        modifier = Modifier.weight(1f).aspectRatio(1f).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(2.dp, GymGreen) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (number == 7) {
                Text("Todos", fontWeight = FontWeight.Bold)
                Text("os dias")
            } else {
                Text("$number dias/", fontWeight = FontWeight.Bold)
                Text("semana")
            }
            if (isSelected) Icon(Icons.Default.CheckCircle, null, tint = GymGreen)
        }
    }
}

@Composable
fun SelectableCard(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector? = null, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) BorderStroke(2.dp, GymGreen) else BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) GymGreen else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GymGreen)
            }
        }
    }
}

// --- STEP 11: LOADING ---
@Composable
fun LoadingStep(gender: GenderUi, onComplete: () -> Unit) {
    var progress by remember { mutableStateOf(0.0f) }
    
    LaunchedEffect(Unit) {
        val steps = 100
        for (i in 1..steps) {
            progress = i / 100f
            kotlinx.coroutines.delay(30) // 3 seconds total
        }
        kotlinx.coroutines.delay(500)
        onComplete()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.Gray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val imageRes = if (gender == GenderUi.MALE) R.drawable.trainer_cr7 else R.drawable.workout_header // Fallback/Placeholder
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Trainer",
                contentScale = ContentScale.Crop, // Fill the circle
                modifier = Modifier.size(100.dp).clip(CircleShape) // Fill the box size
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Seu treinador está ocupado trabalhando para você...",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Circular Progress
        Box(contentAlignment = Alignment.Center) {
             CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(150.dp),
                color = GymGreen,
                strokeWidth = 12.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        if (progress >= 1f) {
            Text(
                text = "O seu desafio pessoal para a academia está pronto!",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

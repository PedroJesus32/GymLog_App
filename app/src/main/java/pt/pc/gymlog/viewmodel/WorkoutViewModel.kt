package pt.pc.gymlog.viewmodel

import kotlin.math.round
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel

enum class PlanDayType { WORKOUT, REST }

data class PlanDayUi(
    val dayNumber: Int,
    val type: PlanDayType,
    val title: String
)

data class WorkoutHistoryEntryUi(
    val id: Long,
    val dateLabel: String,
    val exercisesSnapshot: List<ExerciseUi>,
    val setsSnapshot: List<SetEntryUi>
)

data class ExerciseUi(
    val id: Long,
    val name: String,
    val muscleGroup: String? = null,
    val notes: String? = null,
    val equipment: String? = null,
    val instructions: String? = null,
    val imageRes: Int? = null,
    val isSystem: Boolean = false
)

data class SetEntryUi(
    val id: Long,
    val exerciseId: Long,
    val setNumber: Int,
    val weight: Double,
    val reps: Int,
    val isCompleted: Boolean = false
)

data class WeightEntryUi(
    val id: Long,
    val dateLabel: String,
    val weightKg: Double
)


enum class GoalUi(val label: String) {
    STRONGER("Ficar mais forte"),
    MUSCLE("Ganhe massa muscular"),
    LEAN("Fique magra e definida"),
    LOSE_WEIGHT("Reduza o peso corporal"),
    HEALTH("Melhore sua saúde e bem-estar"),
    PERFORMANCE("Aumente seu desempenho esportivo")
}

enum class ChallengeUi(val label: String) {
    MOTIVATION("Dificuldade em se manter motivada"),
    GUIDANCE("Falta de orientação clara nos treinos"),
    BOREDOM("Fica entediada facilmente com os treinos")
}

enum class PlaceUi(val label: String, val description: String) {
    BIG_GYM("Academia Grande", "Uma grande instalação com equipamentos variados"),
    SMALL_GYM("Academia Pequena", "Uma pequena instalação com equipamentos limitados"),
    GARAGE("Academia de Garagem", "Um setup personalizado com equipamentos de levantamento de peso"),
    HOME("Em casa", "Um setup pessoal com equipamentos portáteis")
}

enum class FocusAreaUi(val label: String) {
    BACK("Costas"),
    SHOULDERS("Ombros"),
    ARMS("Braço"),
    CHEST("Peito"),
    ABS("Abdómen"),
    GLUTES("Nádega"),
    LEGS("Perna"),
    FULL_BODY("Todo o corpo")
}

enum class GenderUi(val label: String) {
    MALE("Masculino"),
    FEMALE("Feminino"),
}

data class UserProfileUi(
    val goal: GoalUi = GoalUi.STRONGER,
    val focusAreas: Set<FocusAreaUi> = setOf(FocusAreaUi.FULL_BODY),
    val oneRmSupinoKg: Int = 0,
    val gender: GenderUi = GenderUi.MALE,
    val currentWeightKg: Double = 0.0,
    val targetWeightKg: Double = 0.0,
    val heightCm: Double = 0.0,
    val challenge: ChallengeUi? = null,
    val place: PlaceUi? = null
)

data class CustomWorkoutUi(
    val id: Long,
    var name: String,
    val exerciseIds: MutableList<Long> = mutableListOf(),
    val setsByExercise: MutableMap<Long, MutableList<SetEntryUi>> = mutableMapOf()
)

class WorkoutViewModel : ViewModel() {

    // --- CUSTOM WORKOUTS STATE ---
    var customWorkouts = mutableStateListOf<CustomWorkoutUi>()
        private set

    fun createCustomWorkout(name: String = "Novo Treino"): Long {
        val id = System.currentTimeMillis()
        customWorkouts.add(CustomWorkoutUi(id, name))
        return id
    }

    fun deleteCustomWorkout(id: Long) {
        customWorkouts.removeIf { it.id == id }
    }

    fun getCustomWorkout(id: Long): CustomWorkoutUi? {
        return customWorkouts.find { it.id == id }
    }

    fun updateCustomWorkoutName(id: Long, newName: String) {
        val w = getCustomWorkout(id)
        w?.name = newName
    }

    // Called for bulk update (e.g. from PickCustomExercisesScreen)
    fun updateCustomWorkoutExercises(workoutId: Long, newIds: List<Long>) {
        val w = getCustomWorkout(workoutId) ?: return
        
        // 1. Add any new
        newIds.forEach { eid ->
            if (eid !in w.exerciseIds) {
                // Initialize sets if new
                w.setsByExercise[eid] = mutableListOf(
                    SetEntryUi(System.nanoTime(), eid, 1, 0.0, 10),
                    SetEntryUi(System.nanoTime()+1, eid, 2, 0.0, 10),
                    SetEntryUi(System.nanoTime()+2, eid, 3, 0.0, 10)
                )
            }
        }
        
        // 2. Remove any that are no longer in the list (optional, or just update order)
        // Note: The UI passes the full selected list. If we want to support removal by deselection:
        val toRemove = w.exerciseIds.filter { it !in newIds }
        toRemove.forEach {
            w.setsByExercise.remove(it)
        }
        
        // 3. Update the ID list (for order)
        w.exerciseIds.clear()
        w.exerciseIds.addAll(newIds)
    }

    fun addExercisesToCustomWorkout(workoutId: Long, newExerciseIds: List<Long>) {
        // Wrapper or direct logic
        updateCustomWorkoutExercises(workoutId, (getCustomWorkout(workoutId)?.exerciseIds ?: emptyList()) + newExerciseIds)
    }

    fun removeExerciseFromCustomWorkout(workoutId: Long, exerciseId: Long) {
        val w = getCustomWorkout(workoutId) ?: return
        w.exerciseIds.remove(exerciseId)
        w.setsByExercise.remove(exerciseId)
    }

    fun replaceExerciseInCustomWorkout(workoutId: Long, oldExId: Long, newExId: Long) {
        val w = getCustomWorkout(workoutId) ?: return
        val idx = w.exerciseIds.indexOf(oldExId)
        if (idx != -1) {
            w.exerciseIds[idx] = newExId
            
            // Migrate sets
            val oldSets = w.setsByExercise[oldExId]
            if (oldSets != null) {
                val newSets = oldSets.map { it.copy(id = System.nanoTime() + (it.id % 100000), exerciseId = newExId) }.toMutableList()
                w.setsByExercise[newExId] = newSets
                w.setsByExercise.remove(oldExId)
            } else {
                // Default init
                w.setsByExercise[newExId] = mutableListOf(
                    SetEntryUi(System.nanoTime(), newExId, 1, 0.0, 10),
                    SetEntryUi(System.nanoTime()+1, newExId, 2, 0.0, 10),
                    SetEntryUi(System.nanoTime()+2, newExId, 3, 0.0, 10)
                )
            }
        }
    }

    // CRUD for Custom Workout Sets
    fun addCustomSet(workoutId: Long, exerciseId: Long) {
        val w = getCustomWorkout(workoutId) ?: return
        val list = w.setsByExercise.getOrPut(exerciseId) { mutableListOf() }
        val maxNum = list.maxOfOrNull { it.setNumber } ?: 0
        list.add(SetEntryUi(System.nanoTime(), exerciseId, maxNum + 1, 0.0, 10))
    }

    fun deleteCustomSet(workoutId: Long, exerciseId: Long, setId: Long) {
        val w = getCustomWorkout(workoutId) ?: return
        w.setsByExercise[exerciseId]?.removeAll { it.id == setId }
    }

    fun updateCustomSet(workoutId: Long, exerciseId: Long, setId: Long, weight: Double? = null, reps: Int? = null) {
        val w = getCustomWorkout(workoutId) ?: return
        val list = w.setsByExercise[exerciseId] ?: return
        val idx = list.indexOfFirst { it.id == setId }
        if (idx != -1) {
            val old = list[idx]
            list[idx] = old.copy(
                weight = weight ?: old.weight,
                reps = reps ?: old.reps
            )
        }
    }

    var exercises by mutableStateOf(
        listOf(
            ExerciseUi(1, "Supino reto", "Peito", equipment = "Barra", isSystem = true, imageRes = pt.pc.gymlog.R.drawable.bench_press_illustration),
            ExerciseUi(2, "Supino inclinado", "Peito", equipment = "Haltere", isSystem = true),
            ExerciseUi(3, "Agachamento", "Pernas", equipment = "Barra", isSystem = true),
            ExerciseUi(4, "Levantamento Terra", "Costas/Pernas", equipment = "Barra", isSystem = true),
            ExerciseUi(5, "Desenvolvimento", "Ombros", equipment = "Haltere", isSystem = true),
            ExerciseUi(6, "Remada curvada", "Costas", equipment = "Barra", isSystem = true),
            ExerciseUi(7, "Rosca direta", "Bíceps", equipment = "Barra", isSystem = true),
            ExerciseUi(8, "Tríceps testa", "Tríceps", equipment = "Barra", isSystem = true),
            ExerciseUi(9, "Leg Press", "Pernas", equipment = "Máquina", isSystem = true),
            ExerciseUi(10, "Cadeira Extensora", "Pernas", equipment = "Máquina", isSystem = true),
            ExerciseUi(11, "Puxada alta", "Costas", equipment = "Máquina", isSystem = true),
            ExerciseUi(12, "Elevação lateral", "Ombros", equipment = "Haltere", isSystem = true)
        )
    )
        private set

    val planDays = (1..30).map { day ->
        PlanDayUi(day, if (day % 4 == 0) PlanDayType.REST else PlanDayType.WORKOUT, "Treino do Dia $day")
    }

    enum class WeekDayUi(val label: String) {
        SEG("Segunda"), TER("Terça"), QUA("Quarta"), QUI("Quinta"), SEX("Sexta"), SAB("Sábado"), DOM("Domingo")
    }

    enum class UnitSystemUi(val label: String) {
        KG("kg"), LB("lb")
    }

    data class UserSettingsUi(
        val workoutsPerWeek: Int = 4,
        val restDay: WeekDayUi = WeekDayUi.DOM,
        val unitSystem: UnitSystemUi = UnitSystemUi.KG,
        val isOnboardingCompleted: Boolean = false
    )

    var userSettings by mutableStateOf(UserSettingsUi())
        private set

    fun updateSettings(
        workoutsPerWeek: Int,
        restDay: WeekDayUi,
        unitSystem: UnitSystemUi
    ) {
        userSettings = userSettings.copy(
            workoutsPerWeek = workoutsPerWeek,
            restDay = restDay,
            unitSystem = unitSystem
        )
        regeneratePlan()
    }

    // --- ONBOARDING STATE ---
    var userProfile by mutableStateOf(UserProfileUi())
        private set

    fun updateGender(g: GenderUi) { userProfile = userProfile.copy(gender = g) }
    fun updateGoal(g: GoalUi) { userProfile = userProfile.copy(goal = g) }
    fun updatePlace(p: PlaceUi) { userProfile = userProfile.copy(place = p) }
    fun updateFocusAreas(areas: Set<FocusAreaUi>) { userProfile = userProfile.copy(focusAreas = areas) }
    fun updateHeightCm(cm: Double) { userProfile = userProfile.copy(heightCm = cm) }
    fun updateCurrentWeightKg(kg: Double) { userProfile = userProfile.copy(currentWeightKg = kg) }
    fun updateTargetWeightKg(kg: Double) { userProfile = userProfile.copy(targetWeightKg = kg) }
    fun updateChallenge(c: ChallengeUi) { userProfile = userProfile.copy(challenge = c) }
    fun updateOneRmSupinoKg(kg: Int) { userProfile = userProfile.copy(oneRmSupinoKg = kg) }

    fun completeOnboarding() {
        userSettings = userSettings.copy(isOnboardingCompleted = true)
        regeneratePlan()
    }
    
    // --- RESET APP DATA ---
    fun resetAppData() {
        userSettings = UserSettingsUi()
        userProfile = UserProfileUi()
        history.clear()
        exercises = exercises.filter { it.isSystem }
        customWorkouts.clear()
        
        weightHistory.clear()
        if (userProfile.currentWeightKg > 0) {
            weightHistory.add(WeightEntryUi(System.currentTimeMillis(), "Inicial", userProfile.currentWeightKg))
        }
        
        currentDay = 1
        tempTodayExercises.clear()
        tempSets.clear()
    }

    fun regeneratePlan(totalDays: Int = 30) {
        // Public method
    }

    fun weekDayToIndex(d: WeekDayUi): Int {
        return when(d) {
            WeekDayUi.SEG -> 0
            WeekDayUi.TER -> 1
            WeekDayUi.QUA -> 2
            WeekDayUi.QUI -> 3
            WeekDayUi.SEX -> 4
            WeekDayUi.SAB -> 5
            WeekDayUi.DOM -> 6
        }
    }

    fun buildRestIndices(mandatory: Int, restCount: Int): Set<Int> {
        return emptySet() // Simplified
    }


    // --- STATE FOR PLAN EXECUTION ---
    var currentDay by mutableStateOf(1)
        private set
        
    val unlockedMaxDay: Int get() = currentDay // Simplified

    // Map day -> list of exercise IDs
    private val tempTodayExercises = mutableStateMapOf<Int, MutableList<Long>>()
    // Map day -> list of Sets
    private val tempSets = mutableStateMapOf<Int, MutableList<SetEntryUi>>()

    // History
    var history = mutableStateListOf<WorkoutHistoryEntryUi>()
        private set

    // Weight Tracker (UPDATED)
    var weightHistory = mutableStateListOf<WeightEntryUi>()
        private set

    fun addWeight(dateLabel: String, weightKg: Double) {
        val id = System.currentTimeMillis()
        weightHistory.add(0, WeightEntryUi(id, dateLabel, weightKg))
    }

    fun deleteWeight(id: Long) {
        weightHistory.removeAll { it.id == id }
    }
        
    fun unlockNextDay(currentDay: Int) {
        // Logic to mark done
    }

    // Navigation helper
    fun setDay(day: Int) {
        currentDay = day
    }
    
    fun openDay(day: Int) {
        if (tempTodayExercises[day] == null) {
            regenerateTodayWorkout(day)
        }
    }

    fun regenerateTodayWorkout(day: Int) {
        val picks = exercises.shuffled().take(4).map { it.id }
        tempTodayExercises[day] = picks.toMutableList()
        
        picks.forEach { exId ->
            tempSets.getOrPut(day) { mutableListOf() }.addAll(
                listOf(
                    SetEntryUi(System.nanoTime(), exId, 1, 0.0, 10),
                    SetEntryUi(System.nanoTime()+1, exerciseId = exId, 2, 0.0, 10),
                    SetEntryUi(System.nanoTime()+2, exerciseId = exId, 3, 0.0, 10)
                )
            )
        }
    }
    
    fun todayExerciseIds(day: Int): List<Long> = tempTodayExercises[day] ?: emptyList()
    
    fun sets(day: Int): List<SetEntryUi> = tempSets[day] ?: emptyList()


    fun saveTodayWorkout(day: Int, dateLabel: String) {
        val exs = exercises.filter { it.id in todayExerciseIds(day) }
        val s = sets(day)
        
        history.add(0, WorkoutHistoryEntryUi(
            id = System.currentTimeMillis(),
            dateLabel = dateLabel,
            exercisesSnapshot = exs,
            setsSnapshot = s
        ))
        
        currentDay = day + 1
    }

    // ... CRUD methods ...
    fun addExercise(name: String, muscleGroup: String?, notes: String?) {
        val newId = (exercises.maxOfOrNull { it.id } ?: 0) + 1
        val newEx = ExerciseUi(newId, name, muscleGroup, notes)
        exercises = exercises + newEx
    }
    
    fun updateExercise(updated: ExerciseUi) {
        exercises = exercises.map { if (it.id == updated.id) updated else it }
    }
    
    fun deleteExercise(id: Long) {
        exercises = exercises.filter { it.id != id }
    }

    // Manage Today
    fun addToToday(day: Int, exerciseId: Long) {
        val current = tempTodayExercises.getOrPut(day) { mutableListOf() }
        if (exerciseId !in current) {
            current.add(exerciseId)
            // Add default sets
             tempSets.getOrPut(day) { mutableListOf() }.addAll(
                listOf(
                    SetEntryUi(System.nanoTime(), exerciseId, 1, 0.0, 10),
                    SetEntryUi(System.nanoTime()+1, exerciseId, 2, 0.0, 10),
                    SetEntryUi(System.nanoTime()+2, exerciseId, 3, 0.0, 10)
                )
            )
        }
    }
    
    fun addExercisesToToday(day: Int, newIds: List<Long>) {
        newIds.forEach { addToToday(day, it) }
    }

    fun addSet(day: Int, exerciseId: Long, weight: Double, reps: Int) {
        val list = tempSets.getOrPut(day) { mutableListOf() }
        val maxSetNum = list.filter { it.exerciseId == exerciseId }.maxOfOrNull { it.setNumber } ?: 0
        list.add(SetEntryUi(System.nanoTime(), exerciseId, maxSetNum + 1, weight, reps))
    }
    
    fun deleteSet(day: Int, setId: Long) {
        val list = tempSets[day] ?: return
        list.removeAll { it.id == setId }
    }
    
    fun updateSet(day: Int, setId: Long, weight: Double, reps: Int) {
        val list = tempSets[day] ?: return
        val idx = list.indexOfFirst { it.id == setId }
        if (idx != -1) {
            list[idx] = list[idx].copy(weight = weight, reps = reps)
        }
    }
    
    fun toggleSetCompletion(day: Int, setId: Long) {
       val list = tempSets[day] ?: return
        val idx = list.indexOfFirst { it.id == setId }
        if (idx != -1) {
            list[idx] = list[idx].copy(isCompleted = !list[idx].isCompleted)
        }
    }
    
    fun removeFromToday(day: Int, exerciseId: Long) {
        tempTodayExercises[day]?.remove(exerciseId)
        tempSets[day]?.removeAll { it.exerciseId == exerciseId }
    }
    
    fun getWorkoutFromHistory(id: Long) = history.find { it.id == id }

    fun repeatWorkoutFromHistory(historyId: Long, targetDay: Int) {
        val h = getWorkoutFromHistory(historyId) ?: return
        tempTodayExercises[targetDay] = h.exercisesSnapshot.map { it.id }.toMutableList()
        // Clone sets
        val newSets = h.setsSnapshot.map { 
             it.copy(id = System.nanoTime() + (it.id % 10000), isCompleted = false)
        }.toMutableList()
        tempSets[targetDay] = newSets
    }

    fun loadCustomWorkoutToDay(customWorkoutId: Long, targetDay: Int) {
        val w = getCustomWorkout(customWorkoutId) ?: return
        // 1. Set exercises for the day
        tempTodayExercises[targetDay] = w.exerciseIds.toMutableList()
        
        // 2. Clone sets from the custom workout definition
        val newSets = mutableListOf<SetEntryUi>()
        w.exerciseIds.forEach { eid ->
            val originalSets = w.setsByExercise[eid]
            if (originalSets != null) {
                newSets.addAll(
                    originalSets.map { it.copy(id = System.nanoTime() + (it.id % 100000), isCompleted = false) }
                )
            }
        }
        tempSets[targetDay] = newSets
    }
}

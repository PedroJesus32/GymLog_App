package pt.pc.gymlog.viewmodel

import kotlin.math.round
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    val notes: String? = null
)

data class SetEntryUi(
    val id: Long,
    val exerciseId: Long,
    val setNumber: Int,
    val weight: Double,
    val reps: Int
)

enum class GoalUi(val label: String) {
    STRONGER("Ficar mais forte"),
    MUSCLE("Ganhe massa muscular"),
    LEAN("Fique magra e definida"),
    LOSE_WEIGHT("Reduza o peso corporal"),
    HEALTH("Melhore sua saúde e bem-estar"),
    PERFORMANCE("Aumente seu desempenho esportivo")
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

    // ✅ básicos (voltou o que tinhas antes)
    val gender: GenderUi = GenderUi.MALE,
    val currentWeightKg: Double = 0.0,
    val targetWeightKg: Double = 0.0,
    val heightCm: Double = 0.0
)

class WorkoutViewModel : ViewModel() {

    var exercises by mutableStateOf(
        listOf(
            ExerciseUi(1, "Supino", "Peito"),
            ExerciseUi(2, "Agachamento", "Pernas"),
            ExerciseUi(3, "Remada", "Costas"),
            ExerciseUi(4, "Desenvolvimento", "Ombros")
        )
    )
        private set


    var history by mutableStateOf<List<WorkoutHistoryEntryUi>>(emptyList())
        private set

    var todayExerciseIds by mutableStateOf<List<Long>>(emptyList())
        private set

    var sets by mutableStateOf<List<SetEntryUi>>(emptyList())
        private set

    enum class WeekDayUi(val label: String) {
        SEG("Segunda"),
        TER("Terça"),
        QUA("Quarta"),
        QUI("Quinta"),
        SEX("Sexta"),
        SAB("Sábado"),
        DOM("Domingo")
    }

    enum class UnitSystemUi(val label: String) {
        KG("kg"),
        LB("lb")
    }

    data class UserSettingsUi(
        val workoutsPerWeek: Int = 4,
        val restDay: WeekDayUi = WeekDayUi.DOM,
        val unitSystem: UnitSystemUi = UnitSystemUi.KG
    )

    var userSettings by mutableStateOf(UserSettingsUi())
        private set

    fun updateSettings(
        workoutsPerWeek: Int,
        restDay: WeekDayUi,
        unitSystem: UnitSystemUi
    ) {
        userSettings = UserSettingsUi(
            workoutsPerWeek = workoutsPerWeek,
            restDay = restDay,
            unitSystem = unitSystem
        )
    }

    var planDays by mutableStateOf<List<PlanDayUi>>(emptyList())
        private set

    fun regeneratePlan(totalDays: Int = 30) {
        val workouts = userSettings.workoutsPerWeek.coerceIn(1, 6)
        val restCount = (7 - workouts).coerceIn(1, 6)

        val mandatoryRest = weekDayToIndex(userSettings.restDay) // 0..6
        val restIndices = buildRestIndices(mandatoryRest, restCount)

        val split = listOf("Peito", "Costas", "Pernas", "Ombros/Braços")
        var cursor = 0

        val out = mutableListOf<PlanDayUi>()

        for (day in 1..totalDays) {
            val weekIdx = (day - 1) % 7 // Dia 1 = SEG (0)

            if (weekIdx in restIndices) {
                out += PlanDayUi(day, PlanDayType.REST, "Dia de Descanso")
            } else {
                val title = split[cursor % split.size]
                cursor++
                out += PlanDayUi(day, PlanDayType.WORKOUT, title)
            }
        }

        planDays = out
    }

    private fun weekDayToIndex(d: WeekDayUi): Int = when (d) {
        WeekDayUi.SEG -> 0
        WeekDayUi.TER -> 1
        WeekDayUi.QUA -> 2
        WeekDayUi.QUI -> 3
        WeekDayUi.SEX -> 4
        WeekDayUi.SAB -> 5
        WeekDayUi.DOM -> 6
    }

    private fun buildRestIndices(mandatory: Int, restCount: Int): Set<Int> {
        val set = linkedSetOf<Int>()
        set += mandatory

        val step = 7.0 / restCount.toDouble()
        var i = 1
        while (set.size < restCount) {
            var idx = ((mandatory + kotlin.math.round(i * step).toInt()) % 7 + 7) % 7
            while (idx in set) idx = (idx + 1) % 7
            set += idx
            i++
        }
        return set
    }


    // dia atual aberto (para "Repetir treino hoje" saber onde meter)
    var currentDay by mutableStateOf(1)
        private set

    var unlockedMaxDay by mutableStateOf(1)
        private set

    fun unlockNextDay(currentDay: Int) {
        val next = (currentDay + 1).coerceAtMost(30)

        // só aumenta, nunca diminui
        if (next > unlockedMaxDay) {
            unlockedMaxDay = next
        }
    }



    // Estado por dia
    private var todayExerciseIdsByDay by mutableStateOf<Map<Int, List<Long>>>(emptyMap())
    private var setsByDay by mutableStateOf<Map<Int, List<SetEntryUi>>>(emptyMap())

    fun setDay(day: Int) {
        currentDay = day
    }

    fun openDay(day: Int) {
        setDay(day)

        // garante que o plano existe
        if (planDays.isEmpty()) regeneratePlan()

        val plan = planDays.firstOrNull { it.dayNumber == day }

        // descanso: deixa vazio
        if (plan == null || plan.type == PlanDayType.REST) {
            todayExerciseIdsByDay = todayExerciseIdsByDay + (day to emptyList())
            setsByDay = setsByDay + (day to emptyList())
            return
        }

        // se já existe treino nesse dia, não sobrescreve
        val existing = todayExerciseIdsByDay[day]
        if (!existing.isNullOrEmpty()) return

        val ids = recommendedForFocus(plan.title).map { it.id }.distinct()
        todayExerciseIdsByDay = todayExerciseIdsByDay + (day to ids)
        setsByDay = setsByDay + (day to emptyList())
    }



    fun todayExerciseIds(day: Int): List<Long> = todayExerciseIdsByDay[day] ?: emptyList()

    fun sets(day: Int): List<SetEntryUi> = setsByDay[day] ?: emptyList()


    fun saveTodayWorkout(day: Int, dateLabel: String) {
        val ids = todayExerciseIds(day)
        val daySets = sets(day)

        if (ids.isEmpty() || daySets.isEmpty()) return

        val selectedExercises = exercises.filter { it.id in ids }
        val nextId = (history.maxOfOrNull { it.id } ?: 0L) + 1L

        history = history + WorkoutHistoryEntryUi(
            id = nextId,
            dateLabel = "$dateLabel • Dia $day",
            exercisesSnapshot = selectedExercises,
            setsSnapshot = daySets
        )

        // limpar treino desse dia
        todayExerciseIdsByDay = todayExerciseIdsByDay - day
        setsByDay = setsByDay - day

        unlockNextDay(day)
    }


    fun addExercise(name: String, muscleGroup: String?, notes: String?) {
        val nextId = (exercises.maxOfOrNull { it.id } ?: 0L) + 1L
        exercises = exercises + ExerciseUi(nextId, name, muscleGroup, notes)
    }

    fun updateExercise(updated: ExerciseUi) {
        exercises = exercises.map { if (it.id == updated.id) updated else it }
    }

    fun deleteExercise(id: Long) {
        exercises = exercises.filterNot { it.id == id }

        todayExerciseIdsByDay = todayExerciseIdsByDay.mapValues { (_, list) ->
            list.filterNot { it == id }
        }.filterValues { it.isNotEmpty() }

        setsByDay = setsByDay.mapValues { (_, list) ->
            list.filterNot { it.exerciseId == id }
        }.filterValues { it.isNotEmpty() }
    }


    fun addToToday(day: Int, exerciseId: Long) {
        val list = todayExerciseIds(day)
        if (exerciseId !in list) {
            todayExerciseIdsByDay = todayExerciseIdsByDay + (day to (list + exerciseId))
        }
    }

    fun addSet(day: Int, exerciseId: Long, weight: Double, reps: Int) {
        val daySets = sets(day)
        val nextId = ((setsByDay.values.flatten().maxOfOrNull { it.id }) ?: 0L) + 1L
        val nextSetNumber = daySets.count { it.exerciseId == exerciseId } + 1

        setsByDay = setsByDay + (day to (daySets + SetEntryUi(nextId, exerciseId, nextSetNumber, weight, reps)))
    }

    fun deleteSet(day: Int, setId: Long) {
        setsByDay = setsByDay + (day to sets(day).filterNot { it.id == setId })
    }

    fun removeFromToday(day: Int, exerciseId: Long) {
        todayExerciseIdsByDay = todayExerciseIdsByDay + (day to todayExerciseIds(day).filterNot { it == exerciseId })
        setsByDay = setsByDay + (day to sets(day).filterNot { it.exerciseId == exerciseId })
    }

    fun getWorkoutFromHistory(id: Long): WorkoutHistoryEntryUi? {
        return history.firstOrNull { it.id == id }
    }

    fun repeatWorkoutFromHistory(historyId: Long, targetDay: Int) {
        val entry = history.firstOrNull { it.id == historyId } ?: return

        val existingIds = exercises.map { it.id }.toSet()
        val missing = entry.exercisesSnapshot.filter { it.id !in existingIds }
        if (missing.isNotEmpty()) {
            exercises = exercises + missing
        }

        todayExerciseIdsByDay = todayExerciseIdsByDay + (targetDay to entry.exercisesSnapshot.map { it.id })

        // recriar sets com IDs novos
        var nextSetId = ((setsByDay.values.flatten().maxOfOrNull { it.id }) ?: 0L) + 1L
        val rebuilt = entry.setsSnapshot
            .groupBy { it.exerciseId }
            .flatMap { (exerciseId, list) ->
                list.sortedBy { it.setNumber }.mapIndexed { index, s ->
                    SetEntryUi(
                        id = nextSetId++,
                        exerciseId = exerciseId,
                        setNumber = index + 1,
                        weight = s.weight,
                        reps = s.reps
                    )
                }
            }

        setsByDay = setsByDay + (targetDay to rebuilt)
    }


    fun completeRestDay(day: Int) {
        // desbloqueia o próximo dia (máx 30)
        if (day >= unlockedMaxDay) {
            unlockedMaxDay = (day + 1).coerceAtMost(30)
        }
    }

    private fun recommendedForFocus(title: String): List<ExerciseUi> {
        val t = title.lowercase()

        fun byName(vararg names: String) =
            exercises.filter { ex -> names.any { it.equals(ex.name, ignoreCase = true) } }

        return when {
            t.contains("peito") -> byName("Supino")
            t.contains("costas") -> byName("Remada")
            t.contains("pernas") || t.contains("inferior") -> byName("Agachamento")
            t.contains("ombros") -> byName("Desenvolvimento")
            else -> exercises.take(3)
        }.ifEmpty { exercises.take(3) }
    }

    var userProfile by mutableStateOf(UserProfileUi())
        private set

    fun updateProfile(updated: UserProfileUi) {
        userProfile = updated
    }



    fun updateGoal(goal: GoalUi) {
        userProfile = userProfile.copy(goal = goal)
    }

    fun updateFocusAreas(areas: Set<FocusAreaUi>) {
        // garante regra: se FULL_BODY está selecionado, fica sozinho
        val normalized =
            if (FocusAreaUi.FULL_BODY in areas) setOf(FocusAreaUi.FULL_BODY)
            else if (areas.isEmpty()) setOf(FocusAreaUi.FULL_BODY)
            else areas

        userProfile = userProfile.copy(focusAreas = normalized)
    }

    fun updateOneRmSupinoKg(value: Int) {
        userProfile = userProfile.copy(oneRmSupinoKg = value.coerceAtLeast(0))
    }

    fun updateGender(g: GenderUi) {
        userProfile = userProfile.copy(gender = g)
    }

    fun updateCurrentWeightKg(value: Double) {
        userProfile = userProfile.copy(currentWeightKg = value.coerceAtLeast(0.0))
    }

    fun updateTargetWeightKg(value: Double) {
        userProfile = userProfile.copy(targetWeightKg = value.coerceAtLeast(0.0))
    }

    fun updateHeightCm(value: Double) {
        userProfile = userProfile.copy(heightCm = value.coerceAtLeast(0.0))
    }
}

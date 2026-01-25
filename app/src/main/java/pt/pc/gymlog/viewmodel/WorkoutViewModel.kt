package pt.pc.gymlog.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


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

class WorkoutViewModel : ViewModel() {

    var exercises by mutableStateOf(
        listOf(
            ExerciseUi(1, "Supino", "Peito"),
            ExerciseUi(2, "Agachamento", "Pernas")
        )
    )
        private set


    var history by mutableStateOf<List<WorkoutHistoryEntryUi>>(emptyList())
        private set

    var todayExerciseIds by mutableStateOf<List<Long>>(emptyList())
        private set

    var sets by mutableStateOf<List<SetEntryUi>>(emptyList())
        private set

    // dia atual aberto (para "Repetir treino hoje" saber onde meter)
    var currentDay by mutableStateOf(1)
        private set

    var unlockedMaxDay by mutableStateOf(1)
        private set

    fun unlockNextDay(currentDay: Int) {
        if (currentDay >= unlockedMaxDay) unlockedMaxDay = currentDay + 1
    }


    // Estado por dia
    private var todayExerciseIdsByDay by mutableStateOf<Map<Int, List<Long>>>(emptyMap())
    private var setsByDay by mutableStateOf<Map<Int, List<SetEntryUi>>>(emptyMap())

    fun setDay(day: Int) {
        currentDay = day
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



}

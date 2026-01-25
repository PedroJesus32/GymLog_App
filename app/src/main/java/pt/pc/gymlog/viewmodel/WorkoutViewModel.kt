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

    fun saveTodayWorkout(dateLabel: String) {
        // Exige pelo menos 1 exercício e 1 série (podes relaxar isto se quiseres)
        if (todayExerciseIds.isEmpty() || sets.isEmpty()) return

        val selectedExercises = exercises.filter { it.id in todayExerciseIds }

        val nextId = (history.maxOfOrNull { it.id } ?: 0L) + 1L
        history = history + WorkoutHistoryEntryUi(
            id = nextId,
            dateLabel = dateLabel,
            exercisesSnapshot = selectedExercises,
            setsSnapshot = sets
        )

        // limpar o treino de hoje
        todayExerciseIds = emptyList()
        sets = emptyList()
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
        todayExerciseIds = todayExerciseIds.filterNot { it == id }
        sets = sets.filterNot { it.exerciseId == id }
    }

    fun addToToday(exerciseId: Long) {
        if (exerciseId !in todayExerciseIds) {
            todayExerciseIds = todayExerciseIds + exerciseId
        }
    }

    fun addSet(exerciseId: Long, weight: Double, reps: Int) {
        val nextId = (sets.maxOfOrNull { it.id } ?: 0L) + 1L
        val nextSetNumber = sets.count { it.exerciseId == exerciseId } + 1
        sets = sets + SetEntryUi(nextId, exerciseId, nextSetNumber, weight, reps)
    }

    fun deleteSet(setId: Long) {
        sets = sets.filterNot { it.id == setId }
    }

    fun removeFromToday(exerciseId: Long) {
        todayExerciseIds = todayExerciseIds.filterNot { it == exerciseId }
        sets = sets.filterNot { it.exerciseId == exerciseId }
    }

    fun getWorkoutFromHistory(id: Long): WorkoutHistoryEntryUi? {
        return history.firstOrNull { it.id == id }
    }

    fun repeatWorkoutFromHistory(id: Long) {
        val entry = getWorkoutFromHistory(id) ?: return

        // garantir que exercícios existem na lista atual (caso tenhas apagado algum)
        val existingIds = exercises.map { it.id }.toSet()
        val missing = entry.exercisesSnapshot.filter { it.id !in existingIds }
        if (missing.isNotEmpty()) {
            exercises = exercises + missing
        }

        todayExerciseIds = entry.exercisesSnapshot.map { it.id }

        // recriar séries com IDs novos e setNumber correto
        var nextSetId = (sets.maxOfOrNull { it.id } ?: 0L) + 1L
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

        sets = rebuilt
    }


}

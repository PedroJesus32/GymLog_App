package pt.pc.gymlog.ui.navigation
import pt.pc.gymlog.ui.navigation.Route
sealed class Route(val path: String) {
    // Tabs (BottomBar)

    data object CustomWorkoutDetail : Route("custom_workout/{id}") {
        fun create(id: Long) = "custom_workout/$id"
    }

    data object PickCustomExercises : Route("pick_custom_exercises") {
        fun create(id: Long, replaceId: Long? = null): String {
            return if (replaceId != null) "$path?id=$id&replaceId=$replaceId" else "$path?id=$id"
        }
    }

    data object ExerciseDetail : Route("exercise_detail/{id}") {
        fun create(id: Long) = "exercise_detail/$id"
    }
    
    data object WorkoutSession : Route("workout_session/{day}") {
        fun create(day: Int) = "workout_session/$day"
    }


    data object CustomWorkoutEdit : Route("custom_workout/{id}") {
        fun create(id: Long) = "custom_workout/$id"
    }
    data object Profile : Route("me/profile")
    data object Settings : Route("me/settings")
    data object Plan : Route("plan")               // Treino (Plano)
    data object Customize : Route("customize")     // Personalizar
    data object Exercises : Route("exercises")     // Exercícios
    data object Report : Route("report")           // Relatório
    data object Me : Route("me")                   // Meu
    data object Goal : Route("goal")
    data object Focus : Route("focus")
    data object OneRmSupino : Route("one_rm_supino")
    data object Weight : Route("weight")
    data object Onboarding : Route("onboarding")

    // Sub-páginas
    data object Today : Route("today/{day}") {
        fun create(day: Int) = "today/$day"
    }

    data object DayWorkoutEdit : Route("today/{day}/edit") {
        fun create(day: Int) = "today/$day/edit"
    }

    data object PickDayExercises : Route("today/{day}/pick") {
        fun create(day: Int) = "today/$day/pick"
    }

    data object WorkoutSummary : Route("today/{day}/summary") {
        fun create(day: Int) = "today/$day/summary"
    }

    data object HistoryDetail : Route("history_detail/{workoutId}") {
        fun create(workoutId: Long) = "history_detail/$workoutId"
    }
}

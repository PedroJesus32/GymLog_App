package pt.pc.gymlog.ui.navigation
import pt.pc.gymlog.ui.navigation.Route
sealed class Route(val path: String) {
    // Tabs (BottomBar)

    data object Profile : Route("me/profile")
    data object Settings : Route("me/settings")
    data object Plan : Route("plan")               // Treino (Plano)
    data object Customize : Route("customize")     // Personalizar
    data object Exercises : Route("exercises")     // Exercícios
    data object Report : Route("report")           // Relatório
    data object Me : Route("me")                   // Meu
    data object Goal : Route("goal")
    data object Focus : Route("focus")

    // Sub-páginas
    data object Today : Route("today/{day}") {
        fun create(day: Int) = "today/$day"
    }

    data object HistoryDetail : Route("history_detail/{workoutId}") {
        fun create(workoutId: Long) = "history_detail/$workoutId"
    }
}

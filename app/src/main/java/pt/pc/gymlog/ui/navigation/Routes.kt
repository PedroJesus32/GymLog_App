package pt.pc.gymlog.ui.navigation
import pt.pc.gymlog.ui.navigation.Route
sealed class Route(val path: String) {
    // Tabs (BottomBar)

    data object Profile : Route("profile")
    data object Settings : Route("settings")
    data object Plan : Route("plan")               // Treino (Plano)
    data object Customize : Route("customize")     // Personalizar
    data object Exercises : Route("exercises")     // Exercícios
    data object Report : Route("report")           // Relatório
    data object Me : Route("me")                   // Meu

    // Sub-páginas
    data object Today : Route("today/{day}") {
        fun create(day: Int) = "today/$day"
    }

    data object HistoryDetail : Route("history_detail/{workoutId}") {
        fun create(workoutId: Long) = "history_detail/$workoutId"
    }
}

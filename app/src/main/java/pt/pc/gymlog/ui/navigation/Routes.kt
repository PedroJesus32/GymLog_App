package pt.pc.gymlog.ui.navigation
import pt.pc.gymlog.ui.navigation.Route
sealed class Route(val path: String) {
    // Tabs (BottomBar)
    data object Plan : Route("plan")               // Treino (Plano)
    data object Customize : Route("customize")     // Personalizar
    data object Exercises : Route("exercises")     // Exercícios
    data object Report : Route("report")           // Relatório
    data object Me : Route("me")                   // Meu

    // Sub-páginas
    data object Today : Route("today")             // Treino do dia (abre a partir do plano)

    data object HistoryDetail : Route("history_detail/{workoutId}") {
        fun create(workoutId: Long) = "history_detail/$workoutId"
    }
}

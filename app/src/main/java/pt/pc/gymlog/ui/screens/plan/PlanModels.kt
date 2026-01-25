package pt.pc.gymlog.ui.screens.plan

data class DayPlan(
    val day: Int,
    val title: String,
    val focus: String,
    val isRest: Boolean = false,
    val locked: Boolean = false
)

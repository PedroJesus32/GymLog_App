package pt.pc.gymlog.viewmodel

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

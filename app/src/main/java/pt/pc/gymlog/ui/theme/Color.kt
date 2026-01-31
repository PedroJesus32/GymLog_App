package pt.pc.gymlog.ui.theme

import androidx.compose.ui.graphics.Color

// Premium Gym Palette
// Premium Gym Palette
// Premium Gym Palette v3 (Electric Blue / Light)
val GymBackgroundWhite = Color(0xFFFFFFFF) // Fundo total
val GymElectricBlue = Color(0xFF007AFF)    // Botões e destaques
val GymDarkText = Color(0xFF1C1C1E)        // Textos e títulos
val GymInputGray = Color(0xFFF2F2F7)       // Standard iOS-like input background (Optional/Derived)

// Mapping to generic names for Theme usage
val GymGreen = GymElectricBlue // Keeping var name to avoid breaking imports, but mapping to Blue
val GymBlack = GymDarkText
val GymWhite = GymBackgroundWhite

val GymError = Color(0xFFFF3B30) // Standard iOS Red since we are going for that look

// Legacy (kept to avoid breakages if ref ignored, but shouldn't be used)
val Purple80 = GymElectricBlue
val PurpleGrey80 = GymDarkText
val Pink80 = GymElectricBlue

val Purple40 = GymElectricBlue
val PurpleGrey40 = GymDarkText
val Pink40 = GymElectricBlue
// End of Palette v3

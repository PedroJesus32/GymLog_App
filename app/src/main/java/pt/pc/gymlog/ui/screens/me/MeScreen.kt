package pt.pc.gymlog.ui.screens.me

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("Meu") }) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Perfil, definições, unidades, etc. (em breve).")
        }
    }
}

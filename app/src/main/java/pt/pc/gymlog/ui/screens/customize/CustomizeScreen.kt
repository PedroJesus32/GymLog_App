package pt.pc.gymlog.ui.screens.customize

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("Personalizar") }) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Aqui vais criar o teu plano personalizado (em breve).")
        }
    }
}

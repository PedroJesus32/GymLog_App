package pt.pc.gymlog.ui.screens.me

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pt.pc.gymlog.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneRmSupinoScreen(
    vm: WorkoutViewModel,
    onBack: () -> Unit
) {
    val current = vm.userProfile.oneRmSupinoKg
    var valueText by remember { mutableStateOf(if (current == 0) "" else current.toString()) }
    var saved by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minha 1RM (Supino)") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Voltar") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = valueText,
                onValueChange = { txt ->
                    // só deixa números
                    valueText = txt.filter { it.isDigit() }
                    saved = false
                },
                label = { Text("1RM em kg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val v = valueText.toIntOrNull() ?: 0
                    vm.updateOneRmSupinoKg(v)
                    saved = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            if (saved) {
                Card {
                    Text("Guardado ✅", modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

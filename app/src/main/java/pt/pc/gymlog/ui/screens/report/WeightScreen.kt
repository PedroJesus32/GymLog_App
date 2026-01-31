package pt.pc.gymlog.ui.screens.report

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pt.pc.gymlog.viewmodel.WorkoutViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(
    vm: WorkoutViewModel,
    onBack: () -> Unit
) {
    var value by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu peso") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
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
                value = value,
                onValueChange = {
                    value = it
                    error = null
                },
                label = { Text("kg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                isError = error != null
            )
            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    val parsed = parseDecimal(value)
                    if (parsed == null || parsed <= 0.0) {
                        error = "Insere um peso válido (ex: 72.5)"
                        return@Button
                    }
                    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    vm.addWeight(dateLabel = date, weightKg = parsed)
                    value = ""
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            Divider()

            Text("Histórico", style = MaterialTheme.typography.titleMedium)

            if (vm.weightHistory.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ainda não tens registos.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(vm.weightHistory, key = { it.id }) { w ->
                        Card(Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(w.dateLabel, style = MaterialTheme.typography.titleMedium)
                                    Text("${format1(w.weightKg)} kg")
                                }
                                IconButton(onClick = { vm.deleteWeight(w.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Apagar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseDecimal(raw: String): Double? {
    val cleaned = raw.trim().replace(',', '.')
    return cleaned.toDoubleOrNull()
}

private fun format1(v: Double): String =
    if (kotlin.math.abs(v - v.toInt()) < 0.000001) v.toInt().toString() else String.format("%.1f", v)

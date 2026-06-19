package com.asistent.ui.meds

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asistent.ui.theme.*

@Composable
fun MedsScreen(padding: PaddingValues, vm: MedsViewModel = hiltViewModel()) {
    val items by vm.items.collectAsState()
    val takenCount = items.count { it.isTakenToday }
    val total = items.size
    var showAdd by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf("08") }
    var minute by remember { mutableStateOf("00") }

    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp)) {
        Spacer(Modifier.height(20.dp))
        Text("Medicamente", style = MaterialTheme.typography.headlineMedium)
        Text("Urmărește ce ai luat azi", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
        if (total > 0) {
            Spacer(Modifier.height(14.dp))
            LinearProgressIndicator(progress = { takenCount.toFloat() / total }, modifier = Modifier.fillMaxWidth(), color = Teal)
            Text("$takenCount/$total luate azi", fontSize = 11.sp, color = TextSecond,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp, bottom = 8.dp))
        } else Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            if (items.isEmpty()) item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Niciun medicament adăugat", color = Border)
                }
            }
            items(items, key = { it.med.id }) { item ->
                Surface(shape = MaterialTheme.shapes.medium,
                    color = if (item.isTakenToday) MaterialTheme.colorScheme.primaryContainer.copy(alpha = .4f) else SurfaceVar,
                    modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(item.timeDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Teal,
                            modifier = Modifier.width(56.dp))
                        Column(Modifier.weight(1f)) {
                            Text(item.med.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            if (item.med.dose.isNotBlank()) Text(item.med.dose, fontSize = 12.sp, color = TextSecond)
                            if (item.isTakenToday) Text("✓ Luat la ${item.takenAtDisplay}", fontSize = 11.sp, color = Teal)
                        }
                        if (!item.isTakenToday) Button(onClick = { vm.markTaken(item.med.id) },
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)) {
                            Text("Luat", fontSize = 13.sp)
                        }
                        IconButton(onClick = { vm.delete(item.med.id) }) { Text("✕", color = Error) }
                    }
                }
            }
            if (showAdd) item {
                Surface(shape = MaterialTheme.shapes.medium, color = SurfaceVar) {
                    Column(Modifier.padding(16.dp)) {
                        OutlinedTextField(value = name, onValueChange = { name = it },
                            label = { Text("Medicament") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = dose, onValueChange = { dose = it },
                            label = { Text("Doză (opțional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Spacer(Modifier.height(8.dp))
                        Text("Ora de administrare", fontSize = 12.sp, color = TextSecond)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(value = hour, onValueChange = { if (it.length <= 2) hour = it },
                                label = { Text("HH") }, modifier = Modifier.weight(1f), singleLine = true)
                            Text(":", fontSize = 20.sp)
                            OutlinedTextField(value = minute, onValueChange = { if (it.length <= 2) minute = it },
                                label = { Text("MM") }, modifier = Modifier.weight(1f), singleLine = true)
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = {
                                vm.add(name.trim(), dose.trim(), hour.toIntOrNull()?.coerceIn(0,23) ?: 8, minute.toIntOrNull()?.coerceIn(0,59) ?: 0)
                                name=""; dose=""; hour="08"; minute="00"; showAdd=false
                            }, modifier = Modifier.weight(1f), enabled = name.isNotBlank()) { Text("Adaugă") }
                            OutlinedButton(onClick = { showAdd=false }, modifier = Modifier.weight(1f)) { Text("Anulează") }
                        }
                    }
                }
            }
        }
        if (!showAdd) OutlinedButton(onClick = { showAdd = true },
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) { Text("+ Medicament nou") }
    }
}

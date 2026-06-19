package com.asistent.ui.subs

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
fun SubsScreen(padding: PaddingValues, vm: SubsViewModel = hiltViewModel()) {
    val items by vm.items.collectAsState()
    val staleCount = items.count { it.isStale }
    var showAdd by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("RON") }

    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp)) {
        Spacer(Modifier.height(20.dp))
        Text("Abonamente", style = MaterialTheme.typography.headlineMedium)
        Text("Alertă doar dacă n-ai folosit 30+ zile", style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))

        if (staleCount > 0) Surface(shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
            Text("⚠️ $staleCount abonament${if (staleCount > 1) "e neutilizate" else " neutilizat"} de 30+ zile",
                modifier = Modifier.padding(12.dp), color = Warning, fontSize = 13.sp)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            if (items.isEmpty()) item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Niciun abonament adăugat", color = Border)
                }
            }
            items(items, key = { it.sub.id }) { item ->
                Surface(shape = MaterialTheme.shapes.medium,
                    color = if (item.isStale) MaterialTheme.colorScheme.errorContainer else SurfaceVar,
                    modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(item.sub.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            if (item.sub.cost > 0) Text("${item.sub.cost} ${item.sub.currency}/lună", color = Teal, fontSize = 12.sp)
                            Text(if (item.days == 0) "Folosit azi" else "Folosit acum ${item.days} zile",
                                color = if (item.isStale) Warning else TextSecond, fontSize = 11.sp)
                        }
                        IconButton(onClick = { vm.markUsed(item.sub.id) }) { Text("✓", color = Teal) }
                        IconButton(onClick = { vm.delete(item.sub.id) }) { Text("✕", color = Error) }
                    }
                }
            }
            if (showAdd) item {
                Surface(shape = MaterialTheme.shapes.medium, color = SurfaceVar) {
                    Column(Modifier.padding(16.dp)) {
                        OutlinedTextField(value = name, onValueChange = { name = it },
                            label = { Text("Nume (ex: Netflix)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = cost, onValueChange = { cost = it },
                                label = { Text("Cost") }, modifier = Modifier.weight(1f), singleLine = true)
                            var exp by remember { mutableStateOf(false) }
                            Box { OutlinedButton(onClick = { exp = true }) { Text(currency) }
                                DropdownMenu(exp, { exp = false }) {
                                    listOf("RON","EUR","USD").forEach { c ->
                                        DropdownMenuItem(text = { Text(c) }, onClick = { currency = c; exp = false })
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { vm.add(name.trim(), cost.toFloatOrNull() ?: 0f, currency); name=""; cost=""; showAdd=false },
                                modifier = Modifier.weight(1f), enabled = name.isNotBlank()) { Text("Adaugă") }
                            OutlinedButton(onClick = { showAdd = false }, modifier = Modifier.weight(1f)) { Text("Anulează") }
                        }
                    }
                }
            }
        }
        if (!showAdd) OutlinedButton(onClick = { showAdd = true },
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) { Text("+ Abonament nou") }
    }
}

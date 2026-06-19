package com.asistent.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.asistent.ui.theme.*

@Composable
fun SettingsScreen(padding: PaddingValues) {
    var current by remember { mutableStateOf(LanguageManager.current()) }
    LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Spacer(Modifier.height(20.dp)); Text("Setări", style = MaterialTheme.typography.headlineMedium)
               Spacer(Modifier.height(16.dp)); Text("LIMBĂ", style = MaterialTheme.typography.labelSmall); Spacer(Modifier.height(8.dp)) }
        items(LanguageManager.all) { lang ->
            val selected = lang.code == current
            Surface(Modifier.fillMaxWidth().clickable { current = lang.code; LanguageManager.set(lang.code) },
                shape = MaterialTheme.shapes.medium,
                color = if (selected) MaterialTheme.colorScheme.primaryContainer else SurfaceVar,
                tonalElevation = if (selected) 4.dp else 0.dp) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(lang.flag, style = MaterialTheme.typography.headlineSmall)
                    Text(lang.label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                    if (selected) Text("✓", color = Teal)
                }
            }
        }
    }
}

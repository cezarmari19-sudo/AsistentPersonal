package com.asistent.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.asistent.ui.meds.MedsScreen
import com.asistent.ui.settings.SettingsScreen
import com.asistent.ui.subs.SubsScreen

private sealed class Screen(val route: String) {
    object Subs : Screen("subs"); object Meds : Screen("meds"); object Settings : Screen("settings")
}
private data class NavItem(val screen: Screen, val icon: String, val label: String)
private val items = listOf(
    NavItem(Screen.Subs, "💳", "Abonamente"),
    NavItem(Screen.Meds, "💊", "Medicamente"),
    NavItem(Screen.Settings, "⚙️", "Setări"),
)

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val current = entry?.destination
    Scaffold(bottomBar = {
        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = current?.hierarchy?.any { it.route == item.screen.route } == true,
                    icon = { Text(item.icon, style = MaterialTheme.typography.headlineSmall) },
                    label = { Text(item.label) },
                    onClick = {
                        nav.navigate(item.screen.route) {
                            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    })
            }
        }
    }) { padding ->
        NavHost(nav, startDestination = Screen.Subs.route) {
            composable(Screen.Subs.route)     { SubsScreen(padding) }
            composable(Screen.Meds.route)     { MedsScreen(padding) }
            composable(Screen.Settings.route) { SettingsScreen(padding) }
        }
    }
}

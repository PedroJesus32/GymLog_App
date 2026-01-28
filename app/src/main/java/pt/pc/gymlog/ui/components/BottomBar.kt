package pt.pc.gymlog.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import pt.pc.gymlog.ui.navigation.Route

private data class BottomItem(
    val route: Route,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(
        BottomItem(Route.Plan, "Treino", Icons.Default.FitnessCenter),
        BottomItem(Route.Customize, "Criar", Icons.Default.Edit),
        BottomItem(Route.Exercises, "Exercícios", Icons.Default.List),
        BottomItem(Route.Report, "Relatório", Icons.Default.BarChart),
        BottomItem(Route.Me, "Meu", Icons.Default.Person)
    )

    val backStack = navController.currentBackStackEntryAsState()
    val currentRoute = backStack.value?.destination?.route

    NavigationBar {
        items.forEach { item ->

            // ✅ Faz o tab ficar "selecionado" mesmo quando estás numa sub-página desse tab
            val selected = when (item.route) {
                Route.Plan ->
                    currentRoute == Route.Plan.path || currentRoute == Route.Today.path

                Route.Me ->
                    currentRoute == Route.Me.path ||
                            currentRoute == Route.Profile.path ||
                            currentRoute == Route.Settings.path

                else -> currentRoute == item.route.path
            }

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route.path) {
                        // ✅ Isto evita bugs e garante que volta ao ecrã do tab
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

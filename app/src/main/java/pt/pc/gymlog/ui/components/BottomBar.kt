package pt.pc.gymlog.ui.components

import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import pt.pc.gymlog.ui.navigation.Route
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsGymnastics

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
            val selected = currentRoute == item.route.path
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route.path) {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                    }
                }
                ,
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

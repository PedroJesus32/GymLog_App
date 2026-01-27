package pt.pc.gymlog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import pt.pc.gymlog.ui.screens.exercises.ExercisesScreen
import pt.pc.gymlog.ui.screens.history.HistoryScreen
import pt.pc.gymlog.ui.screens.history.WorkoutDetailsScreen
import pt.pc.gymlog.ui.screens.today.TodayScreen
import pt.pc.gymlog.viewmodel.WorkoutViewModel
import pt.pc.gymlog.ui.screens.me.MeScreen
import pt.pc.gymlog.ui.screens.me.ProfileScreen
import pt.pc.gymlog.ui.screens.me.SettingsScreen

@Composable
fun AppNav(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val vm: WorkoutViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Route.Plan.path,
        modifier = modifier
    ) {

        composable(Route.Profile.path) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.Settings.path) {
            SettingsScreen(
                vm = vm,
                onBack = { navController.popBackStack() }
            )
        }



        // TAB: Treino (Plano)
        composable(Route.Plan.path) {
            pt.pc.gymlog.ui.screens.plan.PlanScreen(
                vm = vm,
                onOpenDay = { day ->
                    navController.navigate(Route.Today.create(day)) { launchSingleTop = true }
                }
            )
        }


        // Sub-página: Treino do dia
        composable(Route.Today.path) { backStackEntry ->
            val day = backStackEntry.arguments?.getString("day")?.toIntOrNull() ?: 1

            TodayScreen(
                vm = vm,
                day = day,
                onGoReport = { navController.navigate(Route.Report.path) { launchSingleTop = true } },
                onBackToPlan = { navController.navigate(Route.Plan.path) { launchSingleTop = true } }
            )


        }


        // TAB: Exercícios
        composable(Route.Exercises.path) { ExercisesScreen(vm) }

        // TAB: Personalizar
        composable(Route.Customize.path) { pt.pc.gymlog.ui.screens.customize.CustomizeScreen() }

        // TAB: Meu
        composable(Route.Me.path) {
            MeScreen(
                onOpenProfile = { navController.navigate(Route.Profile.path) { launchSingleTop = true } },
                onOpenSettings = { navController.navigate(Route.Settings.path) { launchSingleTop = true } }
            )
        }

        // TAB: Relatório (inclui Histórico)
        composable(Route.Report.path) {
            pt.pc.gymlog.ui.screens.report.ReportScreen(
                vm = vm,
                onOpenWorkout = { id ->
                    navController.navigate(Route.HistoryDetail.create(id)) { launchSingleTop = true }
                }
            )
        }

        // Detalhe do treino (a partir do Relatório)
        composable(Route.HistoryDetail.path) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("workoutId")?.toLongOrNull() ?: -1L

            WorkoutDetailsScreen(
                vm = vm,
                workoutId = id,
                onBack = { navController.popBackStack() },
                onRepeatToday = {
                    vm.repeatWorkoutFromHistory(historyId = id, targetDay = vm.currentDay)
                    navController.navigate(Route.Today.create(vm.currentDay)) { launchSingleTop = true }

                }
            )
        }
    }

}

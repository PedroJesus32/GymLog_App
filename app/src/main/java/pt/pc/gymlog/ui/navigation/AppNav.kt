package pt.pc.gymlog.ui.navigation

import pt.pc.gymlog.ui.screens.report.WeightScreen
import pt.pc.gymlog.ui.screens.me.OneRmSupinoScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import pt.pc.gymlog.ui.screens.me.GoalScreen
import pt.pc.gymlog.ui.screens.me.FocusScreen
import androidx.navigation.navArgument
@Composable
fun AppNav(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val vm: WorkoutViewModel = viewModel()

    // Observe Reset/Onboarding state
    LaunchedEffect(vm.userSettings.isOnboardingCompleted) {
        if (!vm.userSettings.isOnboardingCompleted) {
            navController.navigate(Route.Onboarding.path) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val startRoute = if (vm.userSettings.isOnboardingCompleted) Route.Plan.path else Route.Onboarding.path

    NavHost(
        navController = navController,
        startDestination = startRoute,
        modifier = modifier
    ) {
        composable(Route.Onboarding.path) {
            pt.pc.gymlog.ui.screens.onboarding.OnboardingScreen(
                vm = vm,
                onFinish = {
                    navController.navigate(Route.Report.path) {
                        popUpTo(Route.Onboarding.path) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Profile.path) {
            ProfileScreen(
                vm = vm,
                onBack = { navController.popBackStack() },
                onOpenGoal = { navController.navigate(Route.Goal.path) },
                onOpenFocus = { navController.navigate(Route.Focus.path) },
                onOpenOneRm = { navController.navigate(Route.OneRmSupino.path) }
            )
        }

        composable(Route.Goal.path) {
            GoalScreen(vm = vm, onBack = { navController.popBackStack() })
        }
        composable(Route.Focus.path) {
            FocusScreen(vm = vm, onBack = { navController.popBackStack() })
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


        // Sub-página: Treino do dia (PREVIEW)
        composable(Route.Today.path) { backStackEntry ->
            val day = backStackEntry.arguments?.getString("day")?.toIntOrNull() ?: 1

            TodayScreen(
                vm = vm,
                day = day,
                onGoReport = { navController.navigate(Route.Report.path) { launchSingleTop = true } },
                onBackToPlan = { navController.navigate(Route.Plan.path) { launchSingleTop = true } },
                onStartSession = {
                    navController.navigate(Route.WorkoutSession.create(day)) { launchSingleTop = true }
                },
                onOpenDetail = { id ->
                    navController.navigate(Route.ExerciseDetail.create(id)) { launchSingleTop = true }
                },
                onEdit = {
                    navController.navigate(Route.DayWorkoutEdit.create(day)) { launchSingleTop = true }
                }
            )
        }
        
        composable(Route.DayWorkoutEdit.path) { backStackEntry ->
            val day = backStackEntry.arguments?.getString("day")?.toIntOrNull() ?: 1
            pt.pc.gymlog.ui.screens.today.EditDayWorkoutScreen(
                vm = vm,
                day = day,
                onBack = { navController.popBackStack() },
                onFinish = {
                    navController.navigate(Route.WorkoutSummary.create(day)) { launchSingleTop = true }
                },
                onAddExercise = {
                    navController.navigate(Route.PickDayExercises.create(day)) { launchSingleTop = true }
                },
                onOpenExerciseDetail = { id ->
                    navController.navigate(Route.ExerciseDetail.create(id)) { launchSingleTop = true }
                },
                onStart = {
                    navController.navigate(Route.WorkoutSession.create(day)) { launchSingleTop = true }
                }
            )
        }

        composable(Route.PickDayExercises.path) { backStackEntry ->
            val day = backStackEntry.arguments?.getString("day")?.toIntOrNull() ?: 1
            pt.pc.gymlog.ui.screens.today.PickDayExercisesScreen(
                vm = vm,
                day = day,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.WorkoutSummary.path) { backStackEntry ->
            val day = backStackEntry.arguments?.getString("day")?.toIntOrNull() ?: 1
            pt.pc.gymlog.ui.screens.today.WorkoutSummaryScreen(
                vm = vm,
                day = day,
                onClose = {
                    val dateLabel = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
                    vm.saveTodayWorkout(day, dateLabel) 
                    // Navigate to Plan and clear edit stack
                    navController.navigate(Route.Plan.path) {
                        popUpTo(Route.Plan.path) { inclusive = true }
                    }
                }
            )
        }
        
        // Active Workout Session
        composable(Route.WorkoutSession.path) { backStackEntry ->
            val day = backStackEntry.arguments?.getString("day")?.toIntOrNull() ?: 1
             pt.pc.gymlog.ui.screens.session.WorkoutSessionScreen(
                 vm = vm,
                 day = day,
                 onBack = { navController.popBackStack() },
                 onFinish = {
                     navController.navigate(Route.WorkoutSummary.create(day)) { launchSingleTop = true }
                 },
                 onOpenExerciseDetail = { id ->
                     navController.navigate(Route.ExerciseDetail.create(id)) { launchSingleTop = true }
                 }
             )
        }


        // TAB: Exercícios
        composable(Route.Exercises.path) { 
            ExercisesScreen(
                vm = vm,
                onOpenDetail = { id -> 
                    navController.navigate(Route.ExerciseDetail.create(id)) { launchSingleTop = true }
                }
            )    
        }
        
        composable(Route.ExerciseDetail.path) { backStackEntry ->
             val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: -1L
             pt.pc.gymlog.ui.screens.exercises.ExerciseDetailScreen(
                 vm = vm,
                 exerciseId = id,
                 onClose = { navController.popBackStack() }
             )
        }

        // TAB: Personalizar
        composable(Route.Customize.path) {
            pt.pc.gymlog.ui.screens.customize.CustomizeScreen(
                vm = vm,
                onOpenWorkout = { id ->
                    navController.navigate(Route.CustomWorkoutDetail.create(id)) { launchSingleTop = true }
                }
            )
        }




        // TAB: Meu
        composable(Route.Me.path) {
            MeScreen(
                onOpenProfile = { navController.navigate(Route.Profile.path) },
                onOpenSettings = { navController.navigate(Route.Settings.path) }
            )
        }


        // TAB: Relatório (inclui Histórico)
        composable(Route.Report.path) {
            pt.pc.gymlog.ui.screens.report.ReportScreen(
                vm = vm,
                onOpenWorkout = { id ->
                    navController.navigate(Route.HistoryDetail.create(id)) { launchSingleTop = true }
                },
                onOpenWeight = {
                    navController.navigate(Route.Weight.path) { launchSingleTop = true }
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

        composable(Route.Goal.path) {
            pt.pc.gymlog.ui.screens.me.GoalScreen(
                vm = vm,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.Focus.path) {
            pt.pc.gymlog.ui.screens.me.FocusScreen(
                vm = vm,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.OneRmSupino.path) {
            OneRmSupinoScreen(vm = vm, onBack = { navController.popBackStack() })
        }

        // Sub-página: Meu peso
        composable(Route.Weight.path) {
            pt.pc.gymlog.ui.screens.report.WeightScreen(
                vm = vm,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Route.CustomWorkoutEdit.path,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: -1L

            pt.pc.gymlog.ui.screens.customize.CustomWorkoutEditScreen(
                vm = vm,
                workoutId = id,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.CustomWorkoutDetail.path) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: -1L

            pt.pc.gymlog.ui.screens.customize.CustomWorkoutEditorScreen(
                vm = vm,
                workoutId = id,
                onBack = { navController.popBackStack() },
                onPickExercises = { wid ->
                    navController.navigate(Route.PickCustomExercises.create(wid)) { launchSingleTop = true }
                },
                onReplaceExercise = { wid, exId ->
                    navController.navigate(Route.PickCustomExercises.create(wid, replaceId = exId)) { launchSingleTop = true }
                },
                onStartWorkout = { wid ->
                    vm.loadCustomWorkoutToDay(wid, vm.currentDay)
                    navController.navigate(Route.WorkoutSession.create(vm.currentDay)) { launchSingleTop = true }
                }
            )
        }

        composable(
            route = "${Route.PickCustomExercises.path}?id={id}&replaceId={replaceId}",
            arguments = listOf(
                navArgument("id") { type = NavType.LongType },
                navArgument("replaceId") { 
                    type = NavType.LongType 
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: -1L
            val replaceId = backStackEntry.arguments?.getLong("replaceId") ?: -1L

            pt.pc.gymlog.ui.screens.customize.PickCustomExercisesScreen(
                vm = vm,
                workoutId = id,
                replaceId = if (replaceId == -1L) null else replaceId,
                onBack = { navController.popBackStack() },
                onOpenDetail = { exerciseId ->
                    navController.navigate(Route.ExerciseDetail.create(exerciseId)) { launchSingleTop = true }
                }
            )
        }

    }

}

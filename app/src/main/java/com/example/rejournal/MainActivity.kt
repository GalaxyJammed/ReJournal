package com.example.rejournal

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rejournal.data.LockPrefs
import com.example.rejournal.data.MoodRepository
import com.example.rejournal.ui.ExtrasScreen
import com.example.rejournal.data.GoalCategory
import com.example.rejournal.ui.GoalCategoryScreen
import com.example.rejournal.ui.GoalDetailScreen
import com.example.rejournal.ui.GoalSuggestionsScreen
import com.example.rejournal.ui.GoalsScreen
import com.example.rejournal.ui.LockScreen
import com.example.rejournal.ui.LogScreen
import com.example.rejournal.ui.PhotoAlbumScreen
import com.example.rejournal.ui.PhotoDetailScreen
import com.example.rejournal.ui.VoiceMemoAlbumScreen
import com.example.rejournal.ui.MainBottomBar
import com.example.rejournal.ui.MoodViewModel
import com.example.rejournal.ui.QuestionnaireScreen
import com.example.rejournal.ui.Screen
import com.example.rejournal.ui.SearchScreen
import com.example.rejournal.ui.SettingsScreen
import com.example.rejournal.ui.StatsScreen
import com.example.rejournal.ui.TrendScreen
import com.example.rejournal.ui.theme.ReJournalTheme
import java.time.LocalDate
import com.example.rejournal.ui.MoodDetailScreen
import com.example.rejournal.ui.ImportantDaysScreen
import com.example.rejournal.ui.FavoriteDaysScreen
import com.example.rejournal.ui.TimeCapsulesScreen
import com.example.rejournal.ui.CreateTimeCapsuleScreen

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        com.example.rejournal.ui.theme.ThemeState.current.value = com.example.rejournal.data.ThemePrefs.getTheme(this)
        com.example.rejournal.ui.theme.ThemeState.darkMode.value = com.example.rejournal.data.ThemePrefs.isDarkMode(this)
        com.example.rejournal.ui.theme.MoodVisualsState.mode.value = com.example.rejournal.data.MoodAppearancePrefs.getMode(this)
        com.example.rejournal.ui.theme.MoodVisualsState.colors.value = com.example.rejournal.data.MoodAppearancePrefs.getActiveColors(this)

        val database = (application as RejournalApplication).database
        val repository = MoodRepository(database.moodDao(), database.importantDayDao(), database.timeCapsuleDao())

        setContent {
            ReJournalTheme {
                AppRoot(repository = repository, activity = this)
            }
        }
    }
}

@Composable
fun AppRoot(repository: MoodRepository, activity: FragmentActivity) {
    val context = LocalContext.current
    val app = context.applicationContext as RejournalApplication
    val lockEnabled = remember { LockPrefs.isEnabled(context) }

    if (lockEnabled && !app.isUnlockedThisSession) {
        LockScreen(activity = activity, onUnlocked = { app.isUnlockedThisSession = true })
    } else {
        AppNavHost(repository = repository)
    }
}

private fun NavController.navigateToBottomDestination(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavHost(repository: MoodRepository) {
    val navController = rememberNavController()
    val appContext = LocalContext.current.applicationContext
    val viewModel: MoodViewModel = viewModel(factory = MoodViewModel.Factory(repository, appContext))

    val animationSpec = tween<androidx.compose.ui.unit.IntOffset>(durationMillis = 300)

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val bottomBarRoutes = setOf(
        Screen.Log.route, Screen.Stats.route, Screen.Trend.route,
        Screen.Extras.route, Screen.Goals.route, Screen.PhotoAlbum.route,
        Screen.VoiceMemoAlbum.route, Screen.ImportantDays.route,
        Screen.FavoriteDays.route, Screen.TimeCapsules.route
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                MainBottomBar(
                    currentRoute = currentRoute,
                    onEntriesClick = { navController.navigateToBottomDestination(Screen.Log.route) },
                    onStatsClick = { navController.navigateToBottomDestination(Screen.Stats.route) },
                    onAddClick = { navController.navigate(Screen.Questionnaire.createRoute(LocalDate.now())) },
                    onTrendClick = { navController.navigateToBottomDestination(Screen.Trend.route) },
                    onExtrasClick = { navController.navigateToBottomDestination(Screen.Extras.route) }
                )
            }
        }
    ) { outerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Log.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(outerPadding),
            enterTransition = {
                slideInHorizontally(animationSpec = animationSpec, initialOffsetX = { fullWidth -> fullWidth })
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = animationSpec, targetOffsetX = { fullWidth -> -fullWidth })
            },
            popEnterTransition = {
                slideInHorizontally(animationSpec = animationSpec, initialOffsetX = { fullWidth -> -fullWidth })
            },
            popExitTransition = {
                slideOutHorizontally(animationSpec = animationSpec, targetOffsetX = { fullWidth -> fullWidth })
            }
        ) {
            composable(Screen.Log.route) {
                LogScreen(
                    viewModel = viewModel,
                    onDayClick = { date -> navController.navigate(Screen.Questionnaire.createRoute(date)) },
                    onSearchClick = { navController.navigate(Screen.Search.route) }
                )
            }
            composable(Screen.Stats.route) {
                StatsScreen(
                    viewModel = viewModel,
                    onMoodClick = { mood -> navController.navigate(Screen.MoodDetail.createRoute(mood)) }
                )
            }
            composable(Screen.Trend.route) {
                TrendScreen(viewModel = viewModel)
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = viewModel,
                    onResultClick = { date -> navController.navigate(Screen.Questionnaire.createRoute(date)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.Questionnaire.route,
                arguments = listOf(navArgument("date") { type = NavType.StringType })
            ) { backStackEntry ->
                val dateString = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
                val date = LocalDate.parse(dateString)
                QuestionnaireScreen(
                    viewModel = viewModel,
                    date = date,
                    onDone = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Extras.route) {
                ExtrasScreen(
                    onGoalsClick = { navController.navigate(Screen.Goals.route) },
                    onPhotoAlbumClick = { navController.navigate(Screen.PhotoAlbum.route) },
                    onVoiceMemoAlbumClick = { navController.navigate(Screen.VoiceMemoAlbum.route) },
                    onImportantDaysClick = { navController.navigate(Screen.ImportantDays.route) },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    onBack = { navController.popBackStack() },
                    onFavoriteDaysClick = { navController.navigate(Screen.FavoriteDays.route) },
                    onTimeCapsulesClick = { navController.navigate(Screen.TimeCapsules.route) },
                )
            }
            composable(Screen.Goals.route) {
                GoalsScreen(
                    viewModel = viewModel,
                    onGoalClick = { id -> navController.navigate(Screen.GoalDetail.createRoute(id)) },
                    onFindGoalClick = { navController.navigate(Screen.GoalCategories.route) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.GoalCategories.route) {
                GoalCategoryScreen(
                    onCategoryClick = { category ->
                        navController.navigate(Screen.GoalSuggestions.createRoute(category.name))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.GoalSuggestions.route,
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val categoryName = backStackEntry.arguments?.getString("category") ?: GoalCategory.HABITS.name
                val category = GoalCategory.entries.find { it.name == categoryName } ?: GoalCategory.HABITS
                GoalSuggestionsScreen(
                    category = category,
                    onGoalSelected = { navController.popBackStack(Screen.Goals.route, inclusive = false) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.GoalDetail.route,
                arguments = listOf(navArgument("goalId") { type = NavType.StringType })
            ) { backStackEntry ->
                val goalId = backStackEntry.arguments?.getString("goalId")!!
                GoalDetailScreen(viewModel = viewModel, goalId = goalId, onBack = { navController.popBackStack() })
            }
            composable(Screen.PhotoAlbum.route) {
                PhotoAlbumScreen(
                    viewModel = viewModel,
                    onPhotoClick = { item ->
                        viewModel.selectedPhoto = item
                        navController.navigate(Screen.PhotoDetail.route)
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.PhotoDetail.route) {
                val item = viewModel.selectedPhoto
                if (item != null) {
                    PhotoDetailScreen(
                        path = item.path,
                        date = item.date,
                        onGoToDay = { date -> navController.navigate(Screen.Questionnaire.createRoute(date)) },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            composable(Screen.VoiceMemoAlbum.route) {
                VoiceMemoAlbumScreen(
                    viewModel = viewModel,
                    onGoToDay = { date -> navController.navigate(Screen.Questionnaire.createRoute(date)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.ImportantDays.route) {
                ImportantDaysScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
            }
            composable(
                route = Screen.MoodDetail.route,
                arguments = listOf(navArgument("moodValue") { type = NavType.IntType })
            ) { backStackEntry ->
                val moodValue = backStackEntry.arguments?.getInt("moodValue") ?: 3
                MoodDetailScreen(viewModel = viewModel, moodValue = moodValue, onBack = { navController.popBackStack() })
            }
            composable(Screen.FavoriteDays.route) {
                FavoriteDaysScreen(
                    viewModel = viewModel,
                    onDayClick = { date -> navController.navigate(Screen.Questionnaire.createRoute(date)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.TimeCapsules.route) {
                TimeCapsulesScreen(
                    viewModel = viewModel,
                    onCreateClick = { navController.navigate(Screen.CreateTimeCapsule.route) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.CreateTimeCapsule.route) {
                CreateTimeCapsuleScreen(
                    viewModel = viewModel,
                    onDone = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
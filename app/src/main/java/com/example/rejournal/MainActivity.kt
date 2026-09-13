package com.example.rejournal

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.example.rejournal.ui.LockScreen
import com.example.rejournal.ui.LogScreen
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
import androidx.compose.foundation.layout.WindowInsets

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = MoodRepository((application as RejournalApplication).database.moodDao())

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
    val bottomBarRoutes = setOf(Screen.Log.route, Screen.Stats.route, Screen.Trend.route, Screen.Settings.route)

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
                    onSettingsClick = { navController.navigateToBottomDestination(Screen.Settings.route) }
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
            composable(Screen.Trend.route) {
                TrendScreen(viewModel = viewModel)
            }
            composable(Screen.Stats.route) {
                StatsScreen(viewModel = viewModel)
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = viewModel,
                    onResultClick = { date -> navController.navigate(Screen.Questionnaire.createRoute(date)) }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = viewModel)
            }
            composable(
                route = Screen.Questionnaire.route,
                arguments = listOf(navArgument("date") { type = NavType.StringType })
            ) { backStackEntry ->
                val dateArg = backStackEntry.arguments?.getString("date")!!
                QuestionnaireScreen(
                    viewModel = viewModel,
                    date = LocalDate.parse(dateArg),
                    onDone = { navController.popBackStack() }
                )
            }
        }
    }
}
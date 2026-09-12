package com.example.rejournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rejournal.data.MoodRepository
import com.example.rejournal.ui.LogScreen
import com.example.rejournal.ui.MoodViewModel
import com.example.rejournal.ui.QuestionnaireScreen
import com.example.rejournal.ui.Screen
import com.example.rejournal.ui.SearchScreen
import com.example.rejournal.ui.SettingsScreen
import com.example.rejournal.ui.StatsScreen
import com.example.rejournal.ui.TrendScreen
import com.example.rejournal.ui.theme.ReJournalTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = MoodRepository((application as RejournalApplication).database.moodDao())

        setContent {
            ReJournalTheme {
                AppNavHost(repository = repository)
            }
        }
    }
}

@Composable
fun AppNavHost(repository: MoodRepository) {
    val navController = rememberNavController()
    val viewModel: MoodViewModel = viewModel(factory = MoodViewModel.Factory(repository))

    val animationSpec = tween<androidx.compose.ui.unit.IntOffset>(durationMillis = 300)

    NavHost(
        navController = navController,
        startDestination = Screen.Log.route,
        modifier = Modifier.fillMaxSize(),
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
                onTrendClick = { navController.navigate(Screen.Trend.route) },
                onStatsClick = { navController.navigate(Screen.Stats.route) },
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
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
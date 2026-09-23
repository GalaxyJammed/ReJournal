package com.example.rejournal

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
import com.example.rejournal.data.MoodAppearancePrefs
import com.example.rejournal.ui.GoalCategoryScreen
import com.example.rejournal.ui.GoalDetailScreen
import com.example.rejournal.ui.GoalSuggestionsScreen
import com.example.rejournal.ui.GoalsScreen
import com.example.rejournal.ui.LockScreen
import com.example.rejournal.ui.LogScreen
import com.example.rejournal.ui.PhotoAlbumScreen
import com.example.rejournal.ui.PhotoDetailScreen
import com.example.rejournal.ui.VoiceMemoAlbumScreen
import com.example.rejournal.ui.EmotionalMixtapeScreen
import com.example.rejournal.ui.MainBottomBar
import com.example.rejournal.ui.MicroWinsScreen
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
import com.example.rejournal.ui.AchievementsScreen
import com.example.rejournal.ui.PositiveMemoryScreen
import com.example.rejournal.ui.ProfileScreen
import com.example.rejournal.ui.SyncScreen
import com.example.rejournal.ui.WhatsNewScreen
import com.example.rejournal.ui.FaqScreen
import com.example.rejournal.ui.AboutScreen
import com.example.rejournal.ui.NotificationTroubleshootScreen
import com.example.rejournal.ui.theme.MoodVisualsState
import com.example.rejournal.ui.SplashGate
import com.example.rejournal.ui.TestsScreen
import com.example.rejournal.ui.MbtiTestScreen
import com.example.rejournal.ui.NpiTestScreen
import com.example.rejournal.ui.DarkTriadTestScreen
import com.example.rejournal.ui.BigFiveTestScreen
import com.example.rejournal.ui.SelfEsteemTestScreen
import com.example.rejournal.ui.ResilienceTestScreen
import com.example.rejournal.data.ProfilePrefs
import com.example.rejournal.data.ThemePrefs
import com.example.rejournal.ui.OnboardingScreen
import com.example.rejournal.ui.theme.ThemeState

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = ThemePrefs
        val appearance = MoodAppearancePrefs
        
        ThemeState.current.value = prefs.getTheme(this)
        ThemeState.darkMode.value = prefs.isDarkMode(this)
        MoodVisualsState.mode.value = appearance.getMode(this)
        MoodVisualsState.colors.value = appearance.getActiveColors(this)
        MoodVisualsState.emojis.value = appearance.getActiveEmojis(this)

        val database = (application as RejournalApplication).database
        val repository = MoodRepository(
            database.moodDao(),
            database.importantDayDao(),
            database.timeCapsuleDao(),
            database.microWinDao()
        )

        setContent {
            ReJournalTheme {
                SplashGate {
                    AppRoot(repository = repository, activity = this)
                }
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

    LaunchedEffect(Unit) {
        viewModel.autoSyncCalendar()
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    
    val bottomBarRoutes = remember {
        setOf(
            Screen.Log.route, Screen.Stats.route, Screen.Trend.route,
            Screen.Extras.route,
        )
    }
    val routeToIndex = remember {
        mapOf(
            Screen.Log.route to 0,
            Screen.Stats.route to 1,
            Screen.Trend.route to 2,
            Screen.Extras.route to 3
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            MainBottomBar(
                currentRoute = currentRoute,
                onEntriesClick = { navController.navigateToBottomDestination(Screen.Log.route) },
                onStatsClick = { navController.navigateToBottomDestination(Screen.Stats.route) },
                onLogTodayClick = { navController.navigate(Screen.Questionnaire.createRoute(LocalDate.now())) },
                onMicroWinClick = { navController.navigate(Screen.MicroWins.route) },
                onTrendClick = { navController.navigateToBottomDestination(Screen.Trend.route) },
                onExtrasClick = { navController.navigateToBottomDestination(Screen.Extras.route) }
            )
        }
    ) { outerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (ProfilePrefs.isOnboarded(appContext)) Screen.Log.route else Screen.Onboarding.route,
            modifier = Modifier.fillMaxSize(),
            enterTransition = {
                val target = targetState.destination.route
                val initial = initialState.destination.route
                val spec = tween<IntOffset>(400, easing = FastOutSlowInEasing)
                
                when {
                    initial == null -> fadeIn(tween(100))
                    target?.startsWith("questionnaire") == true -> 
                        fadeIn(tween(350)) + scaleIn(tween(350), initialScale = 0.92f) + slideInVertically(tween(350), initialOffsetY = { 80 })
                    target in bottomBarRoutes && initial in bottomBarRoutes -> {
                        val tIdx = routeToIndex[target] ?: 0
                        val iIdx = routeToIndex[initial] ?: 0
                        if (tIdx > iIdx) slideInHorizontally(spec, initialOffsetX = { it })
                        else slideInHorizontally(spec, initialOffsetX = { -it })
                    }
                    else -> slideInHorizontally(spec, initialOffsetX = { it })
                }
            },
            exitTransition = {
                val target = targetState.destination.route
                val initial = initialState.destination.route
                val spec = tween<IntOffset>(400, easing = FastOutSlowInEasing)
                when {
                    initial?.startsWith("questionnaire") == true -> 
                        fadeOut(tween(300)) + scaleOut(tween(300), targetScale = 0.92f) + slideOutVertically(tween(300), targetOffsetY = { 80 })
                    target?.startsWith("questionnaire") == true -> fadeOut(tween(300))
                    target in bottomBarRoutes && initial in bottomBarRoutes -> {
                        val tIdx = routeToIndex[target] ?: 0
                        val iIdx = routeToIndex[initial] ?: 0
                        if (tIdx > iIdx) slideOutHorizontally(spec, targetOffsetX = { -it })
                        else slideOutHorizontally(spec, targetOffsetX = { it })
                    }
                    else -> slideOutHorizontally(spec, targetOffsetX = { -it })
                }
            },
            popEnterTransition = {
                val target = targetState.destination.route
                val initial = initialState.destination.route
                val spec = tween<IntOffset>(400, easing = FastOutSlowInEasing)
                when {
                    initial?.startsWith("questionnaire") == true -> fadeIn(tween(300))
                    target?.startsWith("questionnaire") == true -> 
                        fadeIn(tween(350)) + scaleIn(tween(350), initialScale = 0.92f) + slideInVertically(tween(350), initialOffsetY = { 80 })
                    target in bottomBarRoutes && initial in bottomBarRoutes -> {
                        val tIdx = routeToIndex[target] ?: 0
                        val iIdx = routeToIndex[initial] ?: 0
                        if (tIdx > iIdx) slideInHorizontally(spec, initialOffsetX = { it })
                        else slideInHorizontally(spec, initialOffsetX = { -it })
                    }
                    else -> slideInHorizontally(spec, initialOffsetX = { -it })
                }
            },
            popExitTransition = {
                val target = targetState.destination.route
                val initial = initialState.destination.route
                val spec = tween<IntOffset>(400, easing = FastOutSlowInEasing)
                when {
                    initial?.startsWith("questionnaire") == true -> 
                        fadeOut(tween(300)) + scaleOut(tween(300), targetScale = 0.92f) + slideOutVertically(tween(300), targetOffsetY = { 80 })
                    target in bottomBarRoutes && initial in bottomBarRoutes -> {
                        val tIdx = routeToIndex[target] ?: 0
                        val iIdx = routeToIndex[initial] ?: 0
                        if (tIdx > iIdx) slideOutHorizontally(spec, targetOffsetX = { -it })
                        else slideOutHorizontally(spec, targetOffsetX = { it })
                    }
                    else -> slideOutHorizontally(spec, targetOffsetX = { it })
                }
            }
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(onFinish = { navController.navigate(Screen.Log.route) { popUpTo(0) { inclusive = true } } })
            }
            composable(Screen.Log.route) {
                Box(modifier = Modifier.fillMaxSize().padding(outerPadding)) {
                    LogScreen(
                        viewModel = viewModel,
                        onDayClick = { date -> navController.navigate(Screen.Questionnaire.createRoute(date)) },
                        onSearchClick = { navController.navigate(Screen.Search.route) },
                        onVisitPositiveMemory = { entry ->
                            viewModel.selectedPositiveMemory = entry
                            navController.navigate(Screen.PositiveMemory.route)
                        },
                    )
                }
            }
            composable(Screen.Stats.route) {
                Box(modifier = Modifier.fillMaxSize().padding(outerPadding)) {
                    StatsScreen(viewModel = viewModel, onMoodClick = { mood -> navController.navigate(Screen.MoodDetail.createRoute(mood)) })
                }
            }
            composable(Screen.Trend.route) {
                Box(modifier = Modifier.fillMaxSize().padding(outerPadding)) {
                    TrendScreen(viewModel = viewModel)
                }
            }
            composable(Screen.Extras.route) {
                Box(modifier = Modifier.fillMaxSize().padding(outerPadding)) {
                    ExtrasScreen(
                        viewModel = viewModel,
                        onGoalsClick = { navController.navigate(Screen.Goals.route) },
                        onMicroWinsClick = { navController.navigate(Screen.MicroWins.route) },
                        onPhotoAlbumClick = { navController.navigate(Screen.PhotoAlbum.route) },
                        onVoiceMemoAlbumClick = { navController.navigate(Screen.VoiceMemoAlbum.route) },
                        onEmotionalMixtapesClick = { navController.navigate(Screen.EmotionalMixtape.route) },
                        onImportantDaysClick = { navController.navigate(Screen.ImportantDays.route) },
                        onSettingsClick = { navController.navigate(Screen.Settings.route) },
                        onFavoriteDaysClick = { navController.navigate(Screen.FavoriteDays.route) },
                        onTimeCapsulesClick = { navController.navigate(Screen.TimeCapsules.route) },
                        onAchievementsClick = { navController.navigate(Screen.Achievements.route) },
                        onProfileClick = { navController.navigate(Screen.Profile.route) },
                        onSyncClick = { navController.navigate(Screen.Sync.route) },
                        onAboutClick = { navController.navigate(Screen.About.route) },
                        onTestsClick = { navController.navigate(Screen.Tests.route) },
                    )
                }
            }
            composable(Screen.MicroWins.route) {
                Box(Modifier.fillMaxSize()) {
                    MicroWinsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            composable(Screen.WhatsNew.route) { Box(Modifier.fillMaxSize()) { WhatsNewScreen(onBack = { navController.popBackStack() }) } }
            composable(Screen.Faq.route) { Box(Modifier.fillMaxSize()) { FaqScreen(onBack = { navController.popBackStack() }) } }
            composable(Screen.Goals.route) {
                Box(Modifier.fillMaxSize()) { GoalsScreen(viewModel = viewModel, onGoalClick = { id -> navController.navigate(Screen.GoalDetail.createRoute(id)) }, onFindGoalClick = { navController.navigate(Screen.GoalCategories.route) }, onBack = { navController.popBackStack() }) }
            }
            composable(Screen.GoalCategories.route) {
                Box(Modifier.fillMaxSize()) { GoalCategoryScreen(onCategoryClick = { category -> navController.navigate(Screen.GoalSuggestions.createRoute(category.name)) }, onBack = { navController.popBackStack() }) }
            }
            composable(route = Screen.GoalSuggestions.route, arguments = listOf(navArgument("category") { type = NavType.StringType })) { backStackEntry ->
                val catName = backStackEntry.arguments?.getString("category") ?: GoalCategory.HABITS.name
                val cat = GoalCategory.entries.find { it.name == catName } ?: GoalCategory.HABITS
                Box(Modifier.fillMaxSize()) { GoalSuggestionsScreen(viewModel = viewModel, category = cat, onGoalSelected = { navController.popBackStack(Screen.Goals.route, false) }, onBack = { navController.popBackStack() }) }
            }
            composable(route = Screen.GoalDetail.route, arguments = listOf(navArgument("goalId") { type = NavType.StringType })) { backStackEntry ->
                val goalId = backStackEntry.arguments?.getString("goalId")!!
                Box(Modifier.fillMaxSize()) { GoalDetailScreen(viewModel = viewModel, goalId = goalId, onBack = { navController.popBackStack() }) }
            }
            composable(Screen.PhotoAlbum.route) {
                Box(Modifier.fillMaxSize()) { PhotoAlbumScreen(viewModel = viewModel, onPhotoClick = { item -> viewModel.selectedPhoto = item; navController.navigate(Screen.PhotoDetail.route) }, onBack = { navController.popBackStack() }) }
            }
            composable(Screen.PhotoDetail.route) {
                val item = viewModel.selectedPhoto
                if (item != null) Box(Modifier.fillMaxSize()) { PhotoDetailScreen(path = item.path, date = item.date, onGoToDay = { date -> navController.navigate(Screen.Questionnaire.createRoute(date)) }, onBack = { navController.popBackStack() }) }
            }
            composable(Screen.VoiceMemoAlbum.route) {
                Box(Modifier.fillMaxSize()) { VoiceMemoAlbumScreen(viewModel = viewModel, onGoToDay = { date -> navController.navigate(Screen.Questionnaire.createRoute(date)) }, onBack = { navController.popBackStack() }) }
            }
            composable(Screen.EmotionalMixtape.route) {
                Box(Modifier.fillMaxSize()) { EmotionalMixtapeScreen(viewModel = viewModel, onGoToDay = { date -> navController.navigate(Screen.Questionnaire.createRoute(date)) }, onBack = { navController.popBackStack() }) }
            }
            composable(Screen.ImportantDays.route) { Box(Modifier.fillMaxSize()) { ImportantDaysScreen(viewModel = viewModel, onBack = { navController.popBackStack() }) } }
            composable(Screen.Settings.route) { Box(Modifier.fillMaxSize()) { SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() }) } }
            composable(route = Screen.MoodDetail.route, arguments = listOf(navArgument("moodValue") { type = NavType.IntType })) { backStackEntry ->
                val moodValue = backStackEntry.arguments?.getInt("moodValue") ?: 3
                Box(Modifier.fillMaxSize()) { MoodDetailScreen(viewModel = viewModel, moodValue = moodValue, onBack = { navController.popBackStack() }) }
            }
            composable(Screen.FavoriteDays.route) {
                Box(Modifier.fillMaxSize()) { FavoriteDaysScreen(viewModel = viewModel, onDayClick = { date -> navController.navigate(Screen.Questionnaire.createRoute(date)) }, onBack = { navController.popBackStack() }) }
            }
            composable(Screen.TimeCapsules.route) {
                Box(Modifier.fillMaxSize()) { TimeCapsulesScreen(viewModel = viewModel, onCreateClick = { navController.navigate(Screen.CreateTimeCapsule.route) }, onBack = { navController.popBackStack() }) }
            }
            composable(Screen.CreateTimeCapsule.route) { Box(Modifier.fillMaxSize()) { CreateTimeCapsuleScreen(viewModel = viewModel, onDone = { navController.popBackStack() }, onBack = { navController.popBackStack() }) } }
            composable(Screen.Achievements.route) { Box(Modifier.fillMaxSize()) { AchievementsScreen(viewModel = viewModel, onBack = { navController.popBackStack() }) } }
            composable(Screen.PositiveMemory.route) {
                val entry = viewModel.selectedPositiveMemory
                if (entry != null) Box(Modifier.fillMaxSize()) { PositiveMemoryScreen(entry = entry, onGoToDay = { date -> navController.navigate(Screen.Questionnaire.createRoute(date)) }, onBack = { navController.popBackStack() }) }
            }
            composable(Screen.Profile.route) { Box(Modifier.fillMaxSize()) { ProfileScreen(onBack = { navController.popBackStack() }) } }
            composable(Screen.Sync.route) { Box(Modifier.fillMaxSize()) { SyncScreen(viewModel = viewModel, onBack = { navController.popBackStack() }) } }
            composable(Screen.About.route) {
                Box(Modifier.fillMaxSize()) { AboutScreen(onWhatsNewClick = { navController.navigate(Screen.WhatsNew.route) }, onFaqClick = { navController.navigate(Screen.Faq.route) }, onNotificationTroubleshootClick = { navController.navigate(Screen.NotificationTroubleshoot.route) }, onBack = { navController.popBackStack() }) }
            }
            composable(Screen.NotificationTroubleshoot.route) { Box(Modifier.fillMaxSize()) { NotificationTroubleshootScreen(onBack = { navController.popBackStack() }) } }
            composable(Screen.Tests.route) {
                Box(Modifier.fillMaxSize()) { TestsScreen(onMbtiClick = { navController.navigate(Screen.MbtiTest.route) }, onNpiClick = { navController.navigate(Screen.NpiTest.route) }, onDarkTriadClick = { navController.navigate(Screen.DarkTriadTest.route) }, onBigFiveClick = { navController.navigate(Screen.BigFiveTest.route) }, onSelfEsteemClick = { navController.navigate(Screen.SelfEsteemTest.route) }, onResilienceClick = { navController.navigate(Screen.ResilienceTest.route) }, onBack = { navController.popBackStack() }) }
            }
            composable(Screen.MbtiTest.route) { Box(Modifier.fillMaxSize()) { MbtiTestScreen(onBack = { navController.popBackStack() }) } }
            composable(Screen.NpiTest.route) { Box(Modifier.fillMaxSize()) { NpiTestScreen(onBack = { navController.popBackStack() }) } }
            composable(Screen.DarkTriadTest.route) { Box(Modifier.fillMaxSize()) { DarkTriadTestScreen(onBack = { navController.popBackStack() }) } }
            composable(Screen.BigFiveTest.route) { Box(Modifier.fillMaxSize()) { BigFiveTestScreen(onBack = { navController.popBackStack() }) } }
            composable(Screen.SelfEsteemTest.route) { Box(Modifier.fillMaxSize()) { SelfEsteemTestScreen(onBack = { navController.popBackStack() }) } }
            composable(Screen.ResilienceTest.route) { Box(Modifier.fillMaxSize()) { ResilienceTestScreen(onBack = { navController.popBackStack() }) } }
            composable(
                route = Screen.Questionnaire.route,
                arguments = listOf(navArgument("date") { type = NavType.StringType })
            ) { backStackEntry ->
                val dateString = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
                val date = LocalDate.parse(dateString)
                Box(Modifier.fillMaxSize()) {
                    QuestionnaireScreen(
                        viewModel = viewModel,
                        date = date,
                        onDone = { navController.popBackStack() },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

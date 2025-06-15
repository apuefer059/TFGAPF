package puerto.fdez.ies.saladillo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.serialization.Serializable
import puerto.fdez.ies.saladillo.model.BattleShipViewModel
import puerto.fdez.ies.saladillo.model.HangmanViewModel
import puerto.fdez.ies.saladillo.model.TicTacToeViewModel
import puerto.fdez.ies.saladillo.model.WordSoupViewModel
import puerto.fdez.ies.saladillo.screen.BattleshipScreen
import puerto.fdez.ies.saladillo.screen.ExpandableSection
import puerto.fdez.ies.saladillo.screen.HangmanScreen
import puerto.fdez.ies.saladillo.screen.MainScreen
import puerto.fdez.ies.saladillo.screen.TicTacToeScreen
import puerto.fdez.ies.saladillo.screen.WordSoupScreen

@Serializable
object MainScreenRoute

@Serializable
object BattleshipRoute

@Serializable
object HangmanRoute

@Serializable
object WordSearchRoute

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var expandedButton by remember { mutableStateOf<ExpandableSection?>(null) }

    NavHost(
        navController = navController,
        startDestination = MainScreenRoute,
        modifier = Modifier.fillMaxSize()
    ) {
        composable<MainScreenRoute> {
            MainScreen(
                expandedButton = expandedButton,
                onExpandChange = { expandedButton = it },
                onTicTacToePvp = {
                    navController.navigate("TicTacToeRoute?isAi=false") {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onTicTacToeAi = {
                    navController.navigate("TicTacToeRoute?isAi=true") {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBattleship = { navController.navigate(BattleshipRoute) },
                onHangman = { navController.navigate(HangmanRoute) },
                onWordSearch = { navController.navigate(WordSearchRoute) }
            )
        }

        composable(
            route = "TicTacToeRoute?isAi={isAi}",
            arguments = listOf(
                navArgument("isAi") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val isAi = backStackEntry.arguments?.getBoolean("isAi") ?: false
            TicTacToeScreen(
                viewModel = TicTacToeViewModel(initialIsVsAI = isAi),
                onBack = { navController.popBackStack() }
            )
        }

        composable<BattleshipRoute> {
            val viewModel: BattleShipViewModel = hiltViewModel()
            BattleshipScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<HangmanRoute> {
            val viewModel: HangmanViewModel = hiltViewModel()
            HangmanScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable<WordSearchRoute> {
            val viewModel: WordSoupViewModel = hiltViewModel()
            WordSoupScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

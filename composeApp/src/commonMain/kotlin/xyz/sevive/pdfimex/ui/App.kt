package xyz.sevive.pdfimex.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.compose.viewmodel.koinViewModel
import xyz.sevive.pdfimex.MainViewModel
import xyz.sevive.pdfimex.ui.theme.PdfImExTheme

enum class AppNavScreen(
    val title: String,
) {
    Home(title = "Home"),
    Placeholder(title = "Placeholder"),
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val mainVM = koinViewModel<MainViewModel>()

    NavHost(
        navController = navController,
        startDestination = AppNavScreen.Home.name,
        modifier = modifier,
    ) {
        composable(route = AppNavScreen.Home.name) {
            MainScreen(vm = mainVM, Modifier.fillMaxSize().padding(16.dp))
        }

        composable(route = AppNavScreen.Placeholder.name) {
            Box(Modifier.fillMaxSize()) {
                Text(
                    "WOW",
                    Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.displayMedium,
                )
            }
        }
    }
}

@Composable
fun AppNavigationBar(
    currentScreen: AppNavScreen,
    onNavigate: (AppNavScreen) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier) {
        for (screen in AppNavScreen.entries) {
            NavigationBarItem(
                selected = currentScreen.name == screen.name,
                onClick = { onNavigate(screen) },
                icon = { Text(screen.title[0].uppercase()) },
                label = { Text(screen.title) },
            )
        }
    }
}

@Composable
fun App(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen =
        remember(backStackEntry?.destination?.route) {
            AppNavScreen.valueOf(backStackEntry?.destination?.route ?: AppNavScreen.Home.name)
        }

    PdfImExTheme {
        Scaffold(
            modifier,
            bottomBar = {
                AppNavigationBar(
                    currentScreen = currentScreen,
                    onNavigate = { navController.navigate(it.name) },
                    Modifier.fillMaxWidth(),
                )
            },
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding),
            )
        }
    }
}

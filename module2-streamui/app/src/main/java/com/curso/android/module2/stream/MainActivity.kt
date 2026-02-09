package com.curso.android.module2.stream

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.toRoute
import com.curso.android.module2.stream.data.repository.MusicRepository
import com.curso.android.module2.stream.ui.navigation.*
import com.curso.android.module2.stream.ui.screens.*
import com.curso.android.module2.stream.ui.theme.StreamUITheme
import com.curso.android.module2.stream.ui.viewmodel.HomeViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.KClass

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { StreamUITheme { StreamUIApp() } }
    }
}

data class BottomNavItem(
    val route: KClass<*>,
    val label: String,
    val selectedIcon: @Composable () -> ImageVector,
    val unselectedIcon: @Composable () -> ImageVector
)

@Composable
fun getBottomNavItems(): List<BottomNavItem> {
    return listOf(
        BottomNavItem(HomeDestination::class, "Home", { Icons.Filled.Home }, { Icons.Outlined.Home }),
        BottomNavItem(HighlightsDestination::class, "Highlights", { Icons.Filled.Star }, { Icons.Outlined.Star })
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamUIApp() {
    val navController = rememberNavController()
    val viewModel: HomeViewModel = koinViewModel()
    val repository: MusicRepository = koinInject()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomNavItems = getBottomNavItems()

    Scaffold(
        topBar = { TopAppBar(title = { Text("StreamUI", fontWeight = FontWeight.Bold) }) },
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    // Corrección de la lógica de selección para evitar errores de tipo
                    val selected = currentDestination?.hierarchy?.any {
                        it.route?.contains(item.route.simpleName ?: "") == true
                    } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            val dest = if (item.route == HomeDestination::class) HomeDestination else HighlightsDestination
                            navController.navigate(dest) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(if (selected) item.selectedIcon() else item.unselectedIcon(), item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(navController, HomeDestination, Modifier.padding(paddingValues)) {
            composable<HomeDestination> {
                HomeScreen(
                    onSongClick = { song -> navController.navigate(PlayerDestination(songId = song.id)) },
                    viewModel = viewModel
                )
            }
            composable<HighlightsDestination> {
                val favorites by viewModel.favoriteSongs.collectAsState()
                HighlightsScreen(
                    favoriteSongs = favorites,
                    onSongClick = { song -> navController.navigate(PlayerDestination(songId = song.id)) },
                    onFavoriteClick = { id -> viewModel.toggleFavorite(id) }
                )
            }
            composable<PlayerDestination> { backStackEntry ->
                val dest = backStackEntry.toRoute<PlayerDestination>()
                val song = repository.getSongById(dest.songId)
                PlayerScreen(song = song, onBackClick = { navController.popBackStack() })
            }
        }
    }
}
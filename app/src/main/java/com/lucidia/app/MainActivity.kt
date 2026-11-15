package com.lucidia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.lucidia.app.ui.graph.GraphScreen
import com.lucidia.app.ui.graph.GraphViewModel
import com.lucidia.app.ui.home.HomeScreen
import com.lucidia.app.ui.home.HomeViewModel
import com.lucidia.app.ui.journal.JournalScreen
import com.lucidia.app.ui.journal.JournalViewModel
import com.lucidia.app.ui.navigation.Screen
import com.lucidia.app.ui.settings.SettingsScreen
import com.lucidia.app.ui.settings.SettingsViewModel
import com.lucidia.app.ui.theme.CoralCardBackground
import com.lucidia.app.ui.theme.CoralCardDark
import com.lucidia.app.ui.theme.LucidiaTheme
import com.lucidia.app.ui.theme.PureWhite
import com.lucidia.app.ui.theme.TealDarkEdge
import com.lucidia.app.ui.theme.TealDock

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val journalViewModel: JournalViewModel by viewModels()
    private val graphViewModel: GraphViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val prefs by settingsViewModel.preferences.collectAsState()

            LucidiaTheme(
                twilightFilterEnabled = prefs.twilightWarmFilterEnabled,
                twilightIntensity = prefs.twilightWarmthIntensity
            ) {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

                Scaffold(
                    bottomBar = {
                        LucidiaBottomDock(
                            currentScreen = currentScreen,
                            onScreenSelected = { currentScreen = it }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        when (currentScreen) {
                            Screen.Home -> HomeScreen(viewModel = homeViewModel)
                            Screen.Journal -> JournalScreen(viewModel = journalViewModel)
                            Screen.Graph -> GraphScreen(viewModel = graphViewModel)
                            Screen.Settings -> SettingsScreen(viewModel = settingsViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LucidiaBottomDock(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        // Pill shaped dock matching Page 2.png
        Box(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(TealDock)
                .border(2.dp, TealDarkEdge, RoundedCornerShape(32.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DockButton(
                    icon = Icons.Default.AccessTime,
                    isSelected = currentScreen == Screen.Home,
                    onClick = { onScreenSelected(Screen.Home) }
                )
                DockButton(
                    icon = Icons.Default.Description,
                    isSelected = currentScreen == Screen.Journal,
                    onClick = { onScreenSelected(Screen.Journal) }
                )
                DockButton(
                    icon = Icons.Default.Hub,
                    isSelected = currentScreen == Screen.Graph,
                    onClick = { onScreenSelected(Screen.Graph) }
                )
                DockButton(
                    icon = Icons.Default.Settings,
                    isSelected = currentScreen == Screen.Settings,
                    onClick = { onScreenSelected(Screen.Settings) }
                )
            }
        }
    }
}

@Composable
private fun DockButton(
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (isSelected) CoralCardDark else CoralCardBackground)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) PureWhite else PureWhite.copy(alpha = 0.5f),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PureWhite,
            modifier = Modifier.size(24.dp)
        )
    }
}

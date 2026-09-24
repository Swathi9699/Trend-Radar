package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.BattlesScreen
import com.example.ui.screens.LabScreen
import com.example.ui.screens.MemeStudioScreen
import com.example.ui.screens.RadarScreen
import com.example.ui.screens.TrendRadar
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.BorderDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SurfaceDark
import com.example.ui.viewmodel.ViralTrendViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ViralTrendViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

data class NavTabItem(
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun MainAppScreen(viewModel: ViralTrendViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    val navTabs = listOf(
        NavTabItem("Radar", Icons.Default.Whatshot, "nav_radar"),
        NavTabItem("Battles", Icons.Default.CompareArrows, "nav_battles"),
        NavTabItem("Lab", Icons.Default.Science, "nav_lab"),
        NavTabItem("Studio", Icons.Default.Palette, "nav_studio"),
        NavTabItem("Vault", Icons.Default.FolderSpecial, "nav_vault")
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBg),
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                contentColor = Color.White,
                tonalElevation = 8.dp
            ) {
                navTabs.forEachIndexed { index, tab ->
                    val isSelected = currentTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(index) },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = NeonCoral,
                            indicatorColor = NeonCoral,
                            unselectedIconColor = Color(0xFF8896B8),
                            unselectedTextColor = Color(0xFF8896B8)
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> TrendRadar(viewModel = viewModel)
                1 -> BattlesScreen(viewModel = viewModel)
                2 -> LabScreen(viewModel = viewModel)
                3 -> MemeStudioScreen(viewModel = viewModel)
                4 -> VaultScreen(viewModel = viewModel)
            }
        }
    }
}

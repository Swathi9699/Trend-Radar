package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PlatformType
import com.example.data.model.TrendItem
import com.example.data.model.TrendStatus
import com.example.ui.components.LiveTickerBanner
import com.example.ui.components.TrendCard
import com.example.ui.theme.BorderDark
import com.example.ui.theme.FlameGold
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SurfaceDark
import com.example.ui.viewmodel.ViralTrendViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * TrendRadar Composable Screen Component
 *
 * Displays real-time viral topics and algorithmic momentum across social media,
 * powered by Gemini 3.5 Flash for live web signals and prompt-based trend synthesis.
 */
@Composable
fun TrendRadar(
    viewModel: ViralTrendViewModel,
    modifier: Modifier = Modifier
) {
    val liveTrends by viewModel.liveTrendsState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedPlatform by viewModel.selectedPlatform.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()
    val savedTrends by viewModel.savedTrends.collectAsStateWithLifecycle()
    val playingAudioId by viewModel.playingAudioTrackId.collectAsStateWithLifecycle()
    val selectedTrendForDetail by viewModel.selectedTrendForDetail.collectAsStateWithLifecycle()

    val isScanning by viewModel.isGeminiScanning.collectAsStateWithLifecycle()
    val isLiveGeminiFeed by viewModel.isGeminiLiveFeed.collectAsStateWithLifecycle()
    val scanMessage by viewModel.geminiScanMessage.collectAsStateWithLifecycle()
    val lastScanTime by viewModel.lastScanTimestamp.collectAsStateWithLifecycle()
    val selectedCategoryTopic by viewModel.selectedCategoryTopic.collectAsStateWithLifecycle()

    val savedIds = savedTrends.map { it.id }.toSet()

    val filteredTrends = liveTrends.filter { trend ->
        val matchesQuery = searchQuery.isBlank() ||
                trend.title.contains(searchQuery, ignoreCase = true) ||
                trend.oneLiner.contains(searchQuery, ignoreCase = true) ||
                trend.hashtags.any { it.contains(searchQuery, ignoreCase = true) } ||
                trend.tags.any { it.contains(searchQuery, ignoreCase = true) }

        val matchesPlatform = selectedPlatform == null || trend.platform == selectedPlatform
        val matchesStatus = selectedStatus == null || trend.status == selectedStatus

        matchesQuery && matchesPlatform && matchesStatus
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radarRotation"
    )

    val timeFormatted = rememberFormattedTime(lastScanTime)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .testTag("trend_radar_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Live Momentum Ticker
            LiveTickerBanner()

            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WifiTethering,
                                contentDescription = null,
                                tint = NeonCoral,
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(if (isScanning) rotationAngle else 0f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TREND RADAR",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = "Real-time algorithmic virality & breakout topics",
                            fontSize = 12.sp,
                            color = Color(0xFF9EABCA)
                        )
                    }

                    // Gemini Trigger / Refresh Button
                    Button(
                        onClick = { viewModel.refreshTrendsWithGemini(selectedCategoryTopic) },
                        enabled = !isScanning,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLiveGeminiFeed) HyperCyan else NeonCoral,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("gemini_scan_button")
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Scanning...",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Scan Gemini Trends",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI Scan",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Gemini Status & Telemetry Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gemini_status_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, BorderDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isLiveGeminiFeed) HyperCyan else FlameGold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = scanMessage ?: "Gemini 3.5 Flash Radar Active",
                                fontSize = 11.sp,
                                color = Color(0xFFD0D7E8),
                                maxLines = 1
                            )
                        }

                        Text(
                            text = timeFormatted,
                            fontSize = 10.sp,
                            color = Color(0xFF8896B8)
                        )
                    }
                }
            }

            // Scanning indicator progress bar
            AnimatedVisibility(
                visible = isScanning,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = HyperCyan,
                    trackColor = Color(0xFF161B29)
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = {
                    Text("Search breakout trends, viral audio, hashtags...", color = Color(0xFF6B7898), fontSize = 13.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF9EABCA),
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color(0xFF9EABCA),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedBorderColor = NeonCoral,
                    unfocusedBorderColor = BorderDark,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("trend_radar_search")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // AI Topic / Category Quick Pills
            val categoryPills = listOf("All", "Pop Culture", "AI & Tech", "Food & ASMR", "Memes", "Gaming", "Storytime")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoryPills) { category ->
                    val isSelected = selectedCategoryTopic == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color(0xFF261830) else SurfaceDark)
                            .clickable {
                                viewModel.setCategoryTopic(category)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("category_pill_$category")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = FlameGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = category,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) FlameGold else Color(0xFF9EABCA)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Platform Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedPlatform == null,
                        onClick = { viewModel.filterByPlatform(null) },
                        label = { Text("All Feeds", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCoral,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceDark,
                            labelColor = Color(0xFF9EABCA)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedPlatform == null,
                            borderColor = BorderDark,
                            selectedBorderColor = NeonCoral
                        ),
                        modifier = Modifier.testTag("filter_platform_all")
                    )
                }
                items(PlatformType.entries) { platform ->
                    val isSelected = selectedPlatform == platform
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.filterByPlatform(platform) },
                        label = {
                            Text("${platform.iconLabel} ${platform.displayName}", fontSize = 12.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCoral,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceDark,
                            labelColor = Color(0xFF9EABCA)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = BorderDark,
                            selectedBorderColor = NeonCoral
                        ),
                        modifier = Modifier.testTag("filter_platform_${platform.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Status Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TrendStatus.entries) { status ->
                    val isSelected = selectedStatus == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.filterByStatus(status) },
                        label = {
                            Text(status.label, fontSize = 11.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(status.badgeColorHex).copy(alpha = 0.3f),
                            selectedLabelColor = Color(status.badgeColorHex),
                            containerColor = SurfaceDark,
                            labelColor = Color(0xFF8896B8)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = BorderDark,
                            selectedBorderColor = Color(status.badgeColorHex)
                        ),
                        modifier = Modifier.testTag("filter_status_${status.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Trends List
            if (filteredTrends.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📡",
                            fontSize = 44.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Trends Found",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No matching items for this filter. Tap 'AI Scan' to fetch fresh trends with Gemini 3.5 Flash.",
                            fontSize = 13.sp,
                            color = Color(0xFF9EABCA),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.updateSearchQuery("")
                                viewModel.filterByPlatform(null)
                                viewModel.filterByStatus(null)
                                viewModel.refreshTrendsWithGemini("All")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCoral),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset & AI Scan")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("trend_radar_list"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredTrends, key = { it.id }) { trend ->
                        TrendCard(
                            trend = trend,
                            isSaved = savedIds.contains(trend.id),
                            isPlayingAudio = playingAudioId == trend.id,
                            onToggleSave = { viewModel.toggleSaveTrend(trend) },
                            onToggleAudio = { viewModel.toggleAudioPreview(trend.id) },
                            onClickDetail = { viewModel.openTrendDetail(trend) }
                        )
                    }
                }
            }
        }

        // Trend Deep-Dive Dialog
        selectedTrendForDetail?.let { trend ->
            TrendDetailDialog(
                trend = trend,
                isSaved = savedIds.contains(trend.id),
                onToggleSave = { viewModel.toggleSaveTrend(trend) },
                onDismiss = { viewModel.closeTrendDetail() }
            )
        }
    }
}

@Composable
private fun rememberFormattedTime(timestamp: Long): String {
    return androidx.compose.runtime.remember(timestamp) {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        "Updated " + sdf.format(Date(timestamp))
    }
}

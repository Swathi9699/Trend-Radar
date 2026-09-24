package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.HookVariation
import com.example.ui.components.ViralityMeter
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.FlameGold
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.viewmodel.ViralTrendViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LabScreen(
    viewModel: ViralTrendViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedLabTab by remember { mutableIntStateOf(0) } // 0: Predictor, 1: Hook Crafter

    val hookInput by viewModel.hookInputText.collectAsStateWithLifecycle()
    val analysis by viewModel.analysisResult.collectAsStateWithLifecycle()

    val selectedNiche by viewModel.selectedNiche.collectAsStateWithLifecycle()
    val selectedHookStyle by viewModel.selectedHookStyle.collectAsStateWithLifecycle()
    val generatedHooks by viewModel.generatedHooks.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "VIRALITY LAB",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Algorithmic retention scoring & hook engine",
                        fontSize = 12.sp,
                        color = Color(0xFF9EABCA)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF142E28))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "⚡ AI ALGO v4",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HyperCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tab Selector
            TabRow(
                selectedTabIndex = selectedLabTab,
                containerColor = SurfaceDark,
                contentColor = NeonCoral,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedLabTab]),
                        color = NeonCoral
                    )
                }
            ) {
                Tab(
                    selected = selectedLabTab == 0,
                    onClick = { selectedLabTab = 0 },
                    text = {
                        Text(
                            text = "🧪 Retention Predictor",
                            fontWeight = if (selectedLabTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedLabTab == 1,
                    onClick = { selectedLabTab = 1 },
                    text = {
                        Text(
                            text = "⚡ Hook Crafter",
                            fontWeight = if (selectedLabTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        if (selectedLabTab == 0) {
            // Predictor Screen
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    // Input Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, BorderDark),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "TEST YOUR HOOK / TITLE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = HyperCyan,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = hookInput,
                                onValueChange = { viewModel.updateHookInput(it) },
                                placeholder = { Text("e.g. Wait until the end to see this secret recipe...") },
                                minLines = 2,
                                maxLines = 4,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = SurfaceVariantDark,
                                    unfocusedContainerColor = SurfaceVariantDark,
                                    focusedBorderColor = NeonCoral,
                                    unfocusedBorderColor = Color(0xFF2E3856),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("hook_predictor_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Try quick test presets:",
                                fontSize = 11.sp,
                                color = Color(0xFF8896B8)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(
                                    "Wait until the end of this...",
                                    "POV you're just a chill guy...",
                                    "The secret 99% get wrong...",
                                    "Stop making this 1 mistake..."
                                ).forEach { sample ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF222B42))
                                            .clickable { viewModel.updateHookInput(sample) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = sample, fontSize = 11.sp, color = Color(0xFFD4DCF2))
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    // Prediction Score Display
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        border = BorderStroke(1.dp, Color(0xFF342345)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = analysis.ratingLabel,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (analysis.score >= 80) NeonCoral else FlameGold
                                    )
                                    Text(
                                        text = "Estimated 3-sec retention: ${analysis.retentionEstimatePercent}%",
                                        fontSize = 12.sp,
                                        color = Color(0xFF9EABCA)
                                    )
                                }

                                ViralityMeter(
                                    score = analysis.score,
                                    size = 72.dp,
                                    strokeWidth = 6.dp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Sub metrics
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MetricPill(
                                    title = "Curiosity Gap",
                                    value = "${analysis.curiosityScore}/100",
                                    color = HyperCyan,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricPill(
                                    title = "Emotional Hook",
                                    value = "${analysis.emotionalImpactScore}/100",
                                    color = CyberViolet,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricPill(
                                    title = "Pacing Speed",
                                    value = "${analysis.pacingScore}/100",
                                    color = FlameGold,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Strengths & Improvement Tips
                if (analysis.strengthFeedback.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF112620)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "✅ ALGORITHM STRENGTHS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HyperCyan
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                analysis.strengthFeedback.forEach { s ->
                                    Text(
                                        text = "• $s",
                                        fontSize = 12.sp,
                                        color = Color(0xFFC7EAE9),
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                if (analysis.improvementTips.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF261D15)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "💡 ALGORITHM OPTIMIZATION TIPS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FlameGold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                analysis.improvementTips.forEach { tip ->
                                    Text(
                                        text = "• $tip",
                                        fontSize = 12.sp,
                                        color = Color(0xFFF7DEB8),
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // AI Rewritten Variations
                if (analysis.viralVariations.isNotEmpty()) {
                    item {
                        Text(
                            text = "HIGH-RETENTION AI VARIATIONS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    items(analysis.viralVariations) { variation ->
                        HookVariationCard(variation = variation)
                    }
                }
            }
        } else {
            // Hook Crafter Screen
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "1. CHOOSE YOUR NICHE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = HyperCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val niches = listOf(
                            "Food & Treats",
                            "Tech & AI",
                            "Career & Money",
                            "Memes & Humor",
                            "Fitness & Health",
                            "Storytime & Drama"
                        )
                        items(niches) { niche ->
                            FilterChip(
                                selected = selectedNiche == niche,
                                onClick = { viewModel.selectNiche(niche) },
                                label = { Text(niche, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonCoral,
                                    selectedLabelColor = Color.White,
                                    containerColor = SurfaceDark,
                                    labelColor = Color(0xFF9EABCA)
                                )
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "2. CHOOSE HOOK STYLE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = HyperCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val styles = listOf(
                            "Curiosity Gap",
                            "Relatable Micro-Pain",
                            "High Stakes Challenge",
                            "Contrarian Hot Take"
                        )
                        items(styles) { style ->
                            FilterChip(
                                selected = selectedHookStyle == style,
                                onClick = { viewModel.selectHookStyle(style) },
                                label = { Text(style, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberViolet,
                                    selectedLabelColor = Color.White,
                                    containerColor = SurfaceDark,
                                    labelColor = Color(0xFF9EABCA)
                                )
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "READY-TO-SHOOT VIRAL HOOKS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(generatedHooks) { hook ->
                    HookVariationCard(variation = hook)
                }
            }
        }
    }
}

@Composable
fun MetricPill(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = Color(0xFF9EABCA)
            )
        }
    }
}

@Composable
fun HookVariationCard(variation: HookVariation) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, Color(0xFF2C3550)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF22173B))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = variation.style.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberViolet
                    )
                }

                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Viral Hook", variation.title))
                        Toast.makeText(context, "Copied hook to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Hook",
                        tint = FlameGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "\"${variation.title}\"",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Visual direction cue
            Row(verticalAlignment = Alignment.Top) {
                Text(text = "🎬 ", fontSize = 12.sp)
                Text(
                    text = "Visual Cue: ${variation.visualActionCue}",
                    fontSize = 11.sp,
                    color = Color(0xFF9EABCA)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Audio direction cue
            Row(verticalAlignment = Alignment.Top) {
                Text(text = "🎵 ", fontSize = 12.sp)
                Text(
                    text = "Sound Cue: ${variation.soundCue}",
                    fontSize = 11.sp,
                    color = HyperCyan.copy(alpha = 0.8f)
                )
            }
        }
    }
}

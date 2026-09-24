package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChallengeEntity
import com.example.data.model.CreatedMemeEntity
import com.example.data.model.SavedTrendEntity
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

@Composable
fun VaultScreen(
    viewModel: ViralTrendViewModel,
    modifier: Modifier = Modifier
) {
    val savedTrends by viewModel.savedTrends.collectAsStateWithLifecycle()
    val createdMemes by viewModel.createdMemes.collectAsStateWithLifecycle()
    val challenges by viewModel.challenges.collectAsStateWithLifecycle()
    val creatorName by viewModel.creatorDisplayName.collectAsStateWithLifecycle()
    val creatorHandle by viewModel.creatorHandle.collectAsStateWithLifecycle()

    var selectedVaultTab by remember { mutableIntStateOf(0) } // 0: Bookmarks, 1: Memes, 2: Challenge
    var showEditProfileDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CREATOR VAULT",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Saved hits, studio memes & growth challenges",
                        fontSize = 12.sp,
                        color = Color(0xFF9EABCA)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2E1C12))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🏆 LEVEL 4",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FlameGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Creator Identity Card (Tap to Change Name)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { showEditProfileDialog = true }
                    .testTag("creator_profile_card"),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NeonCoral),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = creatorName.take(1).uppercase(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = creatorName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "✨",
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = creatorHandle,
                                fontSize = 12.sp,
                                color = HyperCyan,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF20273B))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Change Name",
                                tint = FlameGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Change Name",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FlameGold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tabs
            TabRow(
                selectedTabIndex = selectedVaultTab,
                containerColor = SurfaceDark,
                contentColor = NeonCoral,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedVaultTab]),
                        color = NeonCoral
                    )
                }
            ) {
                Tab(
                    selected = selectedVaultTab == 0,
                    onClick = { selectedVaultTab = 0 },
                    text = {
                        Text("📌 Saved (${savedTrends.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                )
                Tab(
                    selected = selectedVaultTab == 1,
                    onClick = { selectedVaultTab = 1 },
                    text = {
                        Text("🎨 Memes (${createdMemes.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                )
                Tab(
                    selected = selectedVaultTab == 2,
                    onClick = { selectedVaultTab = 2 },
                    text = {
                        Text("🏆 7-Day Sprint", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                )
            }
        }

        when (selectedVaultTab) {
            0 -> SavedTrendsTab(
                savedTrends = savedTrends,
                onRemove = { viewModel.removeSavedTrend(it) },
                onSelectTrend = { trendId ->
                    val trend = viewModel.liveTrends.firstOrNull { it.id == trendId }
                    if (trend != null) viewModel.openTrendDetail(trend)
                }
            )
            1 -> CreatedMemesTab(
                memes = createdMemes,
                onDelete = { viewModel.deleteMeme(it) }
            )
            else -> ChallengesTab(
                challenges = challenges,
                onToggle = { viewModel.toggleChallenge(it) }
            )
        }

        if (showEditProfileDialog) {
            EditCreatorNameDialog(
                currentName = creatorName,
                currentHandle = creatorHandle,
                onDismiss = { showEditProfileDialog = false },
                onSave = { newName, newHandle ->
                    viewModel.updateCreatorProfile(newName, newHandle)
                    showEditProfileDialog = false
                }
            )
        }
    }
}

@Composable
fun SavedTrendsTab(
    savedTrends: List<SavedTrendEntity>,
    onRemove: (String) -> Unit,
    onSelectTrend: (String) -> Unit
) {
    if (savedTrends.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "📌", fontSize = 42.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No saved trends yet",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tap the bookmark icon on any trend card in Radar to save it here.",
                    fontSize = 13.sp,
                    color = Color(0xFF9EABCA)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(savedTrends, key = { it.id }) { trend ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectTrend(trend.id) },
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, BorderDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ViralityMeter(
                            score = trend.viralityScore,
                            size = 48.dp,
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = trend.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${trend.platform} • ${trend.velocity}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HyperCyan
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = trend.oneLiner,
                                fontSize = 12.sp,
                                color = Color(0xFF9EABCA),
                                maxLines = 1
                            )
                        }

                        IconButton(
                            onClick = { onRemove(trend.id) },
                            modifier = Modifier.testTag("delete_saved_${trend.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = Color(0xFFE57373)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreatedMemesTab(
    memes: List<CreatedMemeEntity>,
    onDelete: (Long) -> Unit
) {
    if (memes.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🎨", fontSize = 42.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No created memes yet",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Head to the Meme & Format Studio tab to design your first viral card.",
                    fontSize = 13.sp,
                    color = Color(0xFF9EABCA)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(memes, key = { it.id }) { meme ->
                val accentColor = try {
                    Color(android.graphics.Color.parseColor(meme.accentHex))
                } catch (_: Exception) {
                    NeonCoral
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.6f))
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
                                    .background(accentColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = meme.stickerBadge,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = accentColor
                                )
                            }

                            IconButton(
                                onClick = { onDelete(meme.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFE57373)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = meme.topText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        if (meme.bottomText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = meme.bottomText,
                                fontSize = 13.sp,
                                color = Color(0xFFD4DCF2)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Format: ${meme.templateStyle} • ${meme.authorTag}",
                            fontSize = 11.sp,
                            color = Color(0xFF71767B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengesTab(
    challenges: List<ChallengeEntity>,
    onToggle: (ChallengeEntity) -> Unit
) {
    val completedCount = challenges.count { it.isCompleted }
    val progress = if (challenges.isNotEmpty()) completedCount.toFloat() / challenges.size else 0f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Sprint Progress Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, Color(0xFF3B291A)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "7-DAY VIRAL SPRINT",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "$completedCount of ${challenges.size} Milestones Completed",
                                fontSize = 12.sp,
                                color = FlameGold
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = FlameGold,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = FlameGold,
                        trackColor = Color(0xFF222B42)
                    )
                }
            }
        }

        items(challenges, key = { it.id }) { challenge ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle(challenge) }
                    .testTag("challenge_item_${challenge.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (challenge.isCompleted) Color(0xFF132A24) else SurfaceDark
                ),
                border = BorderStroke(
                    1.dp,
                    if (challenge.isCompleted) Color(0xFF1C5A48) else BorderDark
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = if (challenge.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Toggle status",
                        tint = if (challenge.isCompleted) Color(0xFF00E676) else Color(0xFF6B7898),
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "DAY ${challenge.dayNumber}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = HyperCyan
                            )
                            if (challenge.isCompleted) {
                                Text(
                                    text = "COMPLETED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00E676)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = challenge.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = challenge.objective,
                            fontSize = 12.sp,
                            color = Color(0xFFD4DCF2),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1A2234))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Pro-Tip: ${challenge.proTip}",
                                fontSize = 11.sp,
                                color = FlameGold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditCreatorNameDialog(
    currentName: String,
    currentHandle: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var nameInput by remember { mutableStateOf(currentName) }
    var handleInput by remember { mutableStateOf(currentHandle.removePrefix("@")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text(
                text = "Change Creator Name & Handle",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Update how your identity appears on created studio memes and creator vault leaderboards.",
                    fontSize = 12.sp,
                    color = Color(0xFF9EABCA)
                )

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonCoral,
                        unfocusedBorderColor = BorderDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_name_input")
                )

                OutlinedTextField(
                    value = handleInput,
                    onValueChange = { handleInput = it },
                    label = { Text("Creator Handle") },
                    prefix = { Text("@", color = HyperCyan) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = HyperCyan,
                        unfocusedBorderColor = BorderDark
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_handle_input")
                )

                Text(
                    text = "Quick Creator Aliases:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FlameGold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Trend Spotter", "Culture Hacker", "Meme King").forEach { preset ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF222B42))
                                .clickable {
                                    nameInput = preset
                                    handleInput = preset.lowercase().replace(" ", "_")
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = preset, fontSize = 10.sp, color = Color(0xFFD4DCF2))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameInput.isNotBlank()) {
                        onSave(nameInput, handleInput)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCoral),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_creator_name_btn")
            ) {
                Text("Save Name", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF9EABCA))
            }
        }
    )
}


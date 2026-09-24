package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BattleItem
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.FlameGold
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.viewmodel.ViralTrendViewModel

@Composable
fun BattlesScreen(
    viewModel: ViralTrendViewModel,
    modifier: Modifier = Modifier
) {
    val battles by viewModel.battles.collectAsStateWithLifecycle()
    var cheerCount by remember { mutableIntStateOf(1420) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "THE HYPE ARENA",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Vote on internet culture's biggest showdowns",
                        fontSize = 12.sp,
                        color = Color(0xFF9EABCA)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF281335))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "⚔️ LIVE SHOWDOWN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberViolet
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Live Hype Reactor
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, Color(0xFF2E3856)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "COMMUNITY HYPE METER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = FlameGold
                        )
                        Text(
                            text = "$cheerCount Crowd Cheers",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("🔥", "🤯", "💀", "👑").forEach { emoji ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF252C42))
                                    .clickable { cheerCount += 1 },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }

        // Battle Cards
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(battles, key = { it.id }) { battle ->
                BattleCard(
                    battle = battle,
                    onVote = { option -> viewModel.voteInBattle(battle.id, option) }
                )
            }
        }
    }
}

@Composable
fun BattleCard(
    battle: BattleItem,
    onVote: (Int) -> Unit
) {
    val totalVotes = (battle.option1Votes + battle.option2Votes).coerceAtLeast(1)
    val pct1 = (battle.option1Votes.toFloat() / totalVotes * 100).toInt()
    val pct2 = 100 - pct1

    val animatedPct1 by animateFloatAsState(
        targetValue = pct1.toFloat(),
        animationSpec = tween(600),
        label = "animPct1"
    )

    val hasVoted = battle.userVote != 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("battle_card_${battle.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = BorderStroke(1.dp, Color(0xFF2A334D))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Category and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1B2338))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = battle.category.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = HyperCyan
                    )
                }
                Text(
                    text = "${totalVotes / 1000}k Votes",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF8896B8)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = battle.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Two battle sides
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Option 1
                BattleOptionItem(
                    title = battle.option1Title,
                    subtitle = battle.option1Subtitle,
                    emoji = battle.option1Emoji,
                    percentage = pct1,
                    isSelected = battle.userVote == 1,
                    isWinning = pct1 >= pct2,
                    hasVoted = hasVoted,
                    color = NeonCoral,
                    onClick = { onVote(1) },
                    modifier = Modifier.weight(1f)
                )

                // Option 2
                BattleOptionItem(
                    title = battle.option2Title,
                    subtitle = battle.option2Subtitle,
                    emoji = battle.option2Emoji,
                    percentage = pct2,
                    isSelected = battle.userVote == 2,
                    isWinning = pct2 > pct1,
                    hasVoted = hasVoted,
                    color = CyberViolet,
                    onClick = { onVote(2) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dynamic Voting Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFF1B2133))
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedPct1 / 100f)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(NeonCoral, Color(0xFFFF5252))
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF8E24AA), CyberViolet)
                                )
                            )
                    )
                }
            }

            if (!hasVoted) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap a side above to cast your vote",
                    fontSize = 11.sp,
                    color = Color(0xFF7A87A5),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun BattleOptionItem(
    title: String,
    subtitle: String,
    emoji: String,
    percentage: Int,
    isSelected: Boolean,
    isWinning: Boolean,
    hasVoted: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.2f) else SurfaceVariantDark
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) color else Color(0xFF2E3856)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF28314B)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF9EABCA),
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (hasVoted) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$percentage%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = color
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Your vote",
                            tint = color,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(color.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "VOTE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = color
                    )
                }
            }
        }
    }
}

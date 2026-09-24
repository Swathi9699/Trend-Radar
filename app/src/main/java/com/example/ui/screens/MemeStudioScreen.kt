package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
fun MemeStudioScreen(
    viewModel: ViralTrendViewModel,
    modifier: Modifier = Modifier
) {
    val template by viewModel.memeTemplate.collectAsStateWithLifecycle()
    val topText by viewModel.memeTopText.collectAsStateWithLifecycle()
    val bottomText by viewModel.memeBottomText.collectAsStateWithLifecycle()
    val author by viewModel.memeAuthor.collectAsStateWithLifecycle()
    val sticker by viewModel.memeSticker.collectAsStateWithLifecycle()
    val accentHex by viewModel.memeAccent.collectAsStateWithLifecycle()
    val savedMessage by viewModel.memeSavedMessage.collectAsStateWithLifecycle()

    val accentColor = try {
        Color(android.graphics.Color.parseColor(accentHex))
    } catch (_: Exception) {
        NeonCoral
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MEME & FORMAT STUDIO",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Create viral cards, memes & breaking news",
                        fontSize = 12.sp,
                        color = Color(0xFF9EABCA)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2B163B))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🎨 STUDIO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberViolet
                    )
                }
            }
        }

        item {
            // LIVE CANVAS PREVIEW
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("meme_canvas_preview"),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, accentColor),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F121C)),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    when (template) {
                        "DARK_TWEET" -> DarkTweetTemplate(
                            topText = topText,
                            bottomText = bottomText,
                            author = author,
                            sticker = sticker,
                            accentColor = accentColor
                        )
                        "BREAKING_NEWS" -> BreakingNewsTemplate(
                            topText = topText,
                            bottomText = bottomText,
                            sticker = sticker,
                            accentColor = accentColor
                        )
                        "IMPACT" -> ImpactMemeTemplate(
                            topText = topText,
                            bottomText = bottomText,
                            sticker = sticker,
                            accentColor = accentColor
                        )
                        else -> MinimalPovTemplate(
                            topText = topText,
                            bottomText = bottomText,
                            author = author,
                            sticker = sticker,
                            accentColor = accentColor
                        )
                    }
                }
            }
        }

        // Save confirmation toast/banner
        if (savedMessage != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF13362A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = savedMessage ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB9F6CA)
                        )
                    }
                }
            }
        }

        // Save button
        item {
            Button(
                onClick = { viewModel.saveCurrentMeme() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_meme_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkAdd,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save to My Creator Vault",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // Template Selector
        item {
            Text(
                text = "FORMAT TEMPLATE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = HyperCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val templates = listOf(
                    "DARK_TWEET" to "🐦 Dark Post",
                    "BREAKING_NEWS" to "📰 Breaking News",
                    "IMPACT" to "💬 Bold Meme",
                    "MINIMAL_POV" to "👁️ Minimal POV"
                )
                items(templates) { (key, label) ->
                    FilterChip(
                        selected = template == key,
                        onClick = { viewModel.updateMemeTemplate(key) },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentColor,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceDark,
                            labelColor = Color(0xFF9EABCA)
                        )
                    )
                }
            }
        }

        // Text Fields
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = BorderStroke(1.dp, BorderDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "CUSTOMIZE TEXT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = FlameGold,
                        letterSpacing = 1.sp
                    )

                    OutlinedTextField(
                        value = topText,
                        onValueChange = { viewModel.updateMemeTopText(it) },
                        label = { Text("Top / Headline Text") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = BorderDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("meme_top_text_input")
                    )

                    OutlinedTextField(
                        value = bottomText,
                        onValueChange = { viewModel.updateMemeBottomText(it) },
                        label = { Text("Bottom / Punchline Text") },
                        minLines = 2,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = BorderDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("meme_bottom_text_input")
                    )

                    OutlinedTextField(
                        value = author,
                        onValueChange = { viewModel.updateMemeAuthor(it) },
                        label = { Text("Creator Handle") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = BorderDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("meme_author_input")
                    )
                }
            }
        }

        // Sticker Badges
        item {
            Text(
                text = "STICKER BADGE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = HyperCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val stickers = listOf(
                    "🔥 10M VIEWS",
                    "💀 BRAINROT",
                    "⚡ PEAK MOMENT",
                    "💯 VALID",
                    "🎯 ACCURATE",
                    "👑 UNBOTHERED"
                )
                stickers.forEach { s ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (sticker == s) accentColor else Color(0xFF1E2538))
                            .clickable { viewModel.updateMemeSticker(s) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = s,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (sticker == s) Color.White else Color(0xFFD4DCF2)
                        )
                    }
                }
            }
        }

        // Accent Color
        item {
            Text(
                text = "ACCENT NEON GLOW",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = HyperCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val colors = listOf(
                    "#FF2E63" to "Neon Coral",
                    "#A259FF" to "Cyber Violet",
                    "#08D9D6" to "Hyper Cyan",
                    "#FF9F1C" to "Flame Gold",
                    "#00E676" to "Viral Green"
                )
                colors.forEach { (hex, _) ->
                    val c = Color(android.graphics.Color.parseColor(hex))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(c)
                            .clickable { viewModel.updateMemeAccent(hex) }
                            .then(
                                if (accentHex == hex) Modifier.border(3.dp, Color.White, CircleShape)
                                else Modifier
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun DarkTweetTemplate(
    topText: String,
    bottomText: String,
    author: String,
    sticker: String,
    accentColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(accentColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = author.take(2).uppercase().replace("@", "V"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Viral Trendmaker",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = Color(0xFF1D9BF0),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(text = author, fontSize = 11.sp, color = Color(0xFF71767B))
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(accentColor.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = sticker,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = accentColor
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = topText,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            lineHeight = 22.sp
        )

        if (bottomText.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = bottomText,
                fontSize = 14.sp,
                color = Color(0xFFD4DCF2),
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Engagement bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "💬 24.8K", fontSize = 11.sp, color = Color(0xFF71767B))
            Text(text = "🔁 89.2K", fontSize = 11.sp, color = Color(0xFF71767B))
            Text(text = "❤️ 412K", fontSize = 11.sp, color = Color(0xFF71767B))
            Text(text = "📊 14.2M", fontSize = 11.sp, color = Color(0xFF71767B))
        }
    }
}

@Composable
fun BreakingNewsTemplate(
    topText: String,
    bottomText: String,
    sticker: String,
    accentColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Red breaking bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE50914))
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🚨 BREAKING ALGORITHM ALERT 🚨",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = topText.uppercase(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = bottomText,
            fontSize = 13.sp,
            color = Color(0xFFFFD54F),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF1E2638))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = sticker,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}

@Composable
fun ImpactMemeTemplate(
    topText: String,
    bottomText: String,
    sticker: String,
    accentColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = topText.uppercase(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif,
            lineHeight = 26.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(accentColor.copy(alpha = 0.5f), Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🔥", fontSize = 32.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = bottomText.uppercase(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif,
            lineHeight = 26.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = sticker,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
    }
}

@Composable
fun MinimalPovTemplate(
    topText: String,
    bottomText: String,
    author: String,
    sticker: String,
    accentColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "POV • REEL",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = accentColor,
                letterSpacing = 1.sp
            )
            Text(
                text = sticker,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4DCF2)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = topText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = bottomText,
            fontSize = 13.sp,
            color = Color(0xFF9EABCA)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "by $author",
            fontSize = 10.sp,
            color = Color(0xFF6B7898)
        )
    }
}

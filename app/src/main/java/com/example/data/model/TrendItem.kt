package com.example.data.model

data class TrendItem(
    val id: String,
    val title: String,
    val platform: PlatformType,
    val viralityScore: Int, // 0 to 100
    val velocity: String, // e.g. "+620%/hr"
    val status: TrendStatus,
    val oneLiner: String,
    val originStory: String,
    val whyItWorks: String,
    val howToRemix: String,
    val audioTrack: String,
    val audioBpm: Int,
    val cuePoint: String,
    val hashtags: List<String>,
    val bestTimeToPost: String,
    val exampleHook: String,
    val tags: List<String>
)

enum class PlatformType(val displayName: String, val iconLabel: String) {
    TIKTOK("TikTok", "🎵"),
    REELS("Reels", "📸"),
    TWITTER_X("X", "🐦"),
    YOUTUBE("Shorts", "▶️"),
    MEMES("Memes", "🎭"),
    AI("AI & Tech", "✨")
}

enum class TrendStatus(val label: String, val badgeColorHex: Long) {
    BLOWING_UP("🔥 Blowing Up", 0xFFFF2E63),
    PEAKING("⚡ Peak Velocity", 0xFFFF9F1C),
    EMERGING("🚀 Rising Fast", 0xFF08D9D6),
    EVERGREEN("💎 Golden Viral", 0xFFA259FF)
}

data class BattleItem(
    val id: String,
    val title: String,
    val category: String,
    val option1Title: String,
    val option1Subtitle: String,
    val option1Emoji: String,
    val option1Votes: Int,
    val option2Title: String,
    val option2Subtitle: String,
    val option2Emoji: String,
    val option2Votes: Int,
    val userVote: Int = 0 // 0 = not voted, 1 = option1, 2 = option2
)

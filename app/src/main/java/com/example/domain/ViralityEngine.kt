package com.example.domain

data class ViralityAnalysisResult(
    val score: Int, // 0 - 100
    val ratingLabel: String,
    val retentionEstimatePercent: Int,
    val curiosityScore: Int,
    val emotionalImpactScore: Int,
    val pacingScore: Int,
    val strengthFeedback: List<String>,
    val improvementTips: List<String>,
    val viralVariations: List<HookVariation>
)

data class HookVariation(
    val title: String,
    val style: String,
    val visualActionCue: String,
    val soundCue: String
)

object ViralityEngine {

    fun analyzeText(input: String): ViralityAnalysisResult {
        val text = input.trim()
        if (text.isEmpty()) {
            return ViralityAnalysisResult(
                score = 0,
                ratingLabel = "Enter a hook to test",
                retentionEstimatePercent = 0,
                curiosityScore = 0,
                emotionalImpactScore = 0,
                pacingScore = 0,
                strengthFeedback = emptyList(),
                improvementTips = listOf("Type your video opening line, caption, or idea above."),
                viralVariations = emptyList()
            )
        }

        val lower = text.lowercase()
        var baseScore = 50

        // Curiosity trigger check
        val curiosityWords = listOf("secret", "nobody", "hidden", "why", "truth", "mistake", "banned", "illegal", "hiding", "actually")
        val foundCuriosity = curiosityWords.count { lower.contains(it) }
        baseScore += (foundCuriosity * 8).coerceAtMost(24)

        // Retention trigger check
        val retentionWords = listOf("wait until", "don't swipe", "stop doing", "watch till", "ending", "part 2", "first 3", "before you", "never")
        val foundRetention = retentionWords.count { lower.contains(it) }
        baseScore += (foundRetention * 9).coerceAtMost(25)

        // POV & Relatable trigger check
        val relatableWords = listOf("pov", "literally", "me when", "why is nobody", "feels like", "everyone", "every single time", "you ever")
        val foundRelatable = relatableWords.count { lower.contains(it) }
        baseScore += (foundRelatable * 7).coerceAtMost(20)

        // Numbers & specificity
        val hasNumbers = lower.any { it.isDigit() }
        if (hasNumbers) baseScore += 8

        // Length optimization: 6 to 18 words is sweet spot for 3-second hook
        val wordCount = text.split("\\s+".toRegex()).size
        val pacingScore = when (wordCount) {
            in 6..14 -> { baseScore += 10; 95 }
            in 3..5 -> { baseScore += 5; 82 }
            in 15..22 -> { baseScore += 2; 75 }
            else -> { baseScore -= 8; 55 }
        }

        val finalScore = baseScore.coerceIn(28, 99)
        val curiosityScore = (50 + foundCuriosity * 15 + if (lower.contains("?")) 12 else 0).coerceIn(35, 98)
        val emotionalScore = (55 + foundRelatable * 14 + if (lower.contains("!")) 8 else 0).coerceIn(40, 96)
        val retentionEstimate = (finalScore * 0.85).toInt().coerceIn(30, 94)

        val ratingLabel = when {
            finalScore >= 90 -> "🔥 Algorithmic Rocket"
            finalScore >= 80 -> "⚡ High Viral Potential"
            finalScore >= 65 -> "👍 Solid Engagement"
            else -> "⚠️ Low Hook Retention"
        }

        val strengths = mutableListOf<String>()
        val tips = mutableListOf<String>()

        if (foundCuriosity > 0) strengths.add("Strong curiosity gap sparks immediate viewer questions.")
        if (foundRetention > 0) strengths.add("Direct pattern interrupt challenges viewer to stay.")
        if (foundRelatable > 0) strengths.add("Relatable POV format drives quote-shares and saves.")
        if (hasNumbers) strengths.add("Specific numbers anchor clarity and credibility.")
        if (wordCount in 6..14) strengths.add("Perfect word economy: readable within 1.5 seconds.")

        if (foundCuriosity == 0) tips.add("Add a curiosity trigger word ('The truth about...', 'Nobody tells you...')")
        if (wordCount > 16) tips.add("Trim length down to under 12 words to fit before the viewer scrolls.")
        if (!hasNumbers) tips.add("Add a concrete number (e.g. '3 reasons', 'in under 60 seconds').")
        if (foundRetention == 0) tips.add("Start with visual motion or a direct warning ('Stop scrolling if you...')")

        val variations = listOf(
            HookVariation(
                title = "Wait until the end before you try $text...",
                style = "Retention Trap",
                visualActionCue = "Drop phone to desk or zoom fast onto screen in 0.5s",
                soundCue = "Record-scratch audio followed by upbeat bass drop"
            ),
            HookVariation(
                title = "POV: You finally realized why $text works so well.",
                style = "Relatable POV",
                visualActionCue = "Slow eye contact with camera and knowing half-smile",
                soundCue = "Lo-fi synth piano crescendo"
            ),
            HookVariation(
                title = "99% of people get this wrong: $text",
                style = "The Contrarian Gap",
                visualActionCue = "Bold red X buzzer graphic over screen",
                soundCue = "Loud dramatic thud sound effect"
            )
        )

        return ViralityAnalysisResult(
            score = finalScore,
            ratingLabel = ratingLabel,
            retentionEstimatePercent = retentionEstimate,
            curiosityScore = curiosityScore,
            emotionalImpactScore = emotionalScore,
            pacingScore = pacingScore,
            strengthFeedback = strengths,
            improvementTips = tips,
            viralVariations = variations
        )
    }

    fun generateHooks(niche: String, style: String): List<HookVariation> {
        return when (style) {
            "Curiosity Gap" -> listOf(
                HookVariation(
                    title = "The one $niche hack nobody will admit actually works...",
                    style = "Curiosity Gap",
                    visualActionCue = "Whispering close-up to the camera with screen brightness lowered",
                    soundCue = "Subtle clock ticking speeding up to sudden silence"
                ),
                HookVariation(
                    title = "I was strictly told never to reveal this about $niche...",
                    style = "Forbidden Secret",
                    visualActionCue = "Quick glance over shoulder before leaning in",
                    soundCue = "Dark cello bass hit"
                ),
                HookVariation(
                    title = "Why does every top $niche expert secretly do this one thing?",
                    style = "Authority Mystery",
                    visualActionCue = "Pointing to blurred screenshot or hidden document",
                    soundCue = "High-pitch mystery chime"
                )
            )
            "Relatable Micro-Pain" -> listOf(
                HookVariation(
                    title = "Tell me I'm not the only person who struggles with $niche like this...",
                    style = "Relatable Vulnerability",
                    visualActionCue = "Exasperated sigh while holding coffee or phone",
                    soundCue = "Muted violin or soft acoustic guitar loop"
                ),
                HookVariation(
                    title = "POV: It's 2 AM and your $niche obsession is officially out of hand.",
                    style = "POV Insomnia",
                    visualActionCue = "Phone glow illuminating your face in dark room",
                    soundCue = "Ambient night reverb beat"
                ),
                HookVariation(
                    title = "That specific emotional damage when $niche goes completely sideways.",
                    style = "Shared Trauma Humor",
                    visualActionCue = "Staring blankly into distance as dramatic text appears",
                    soundCue = "Slowed down classical piano"
                )
            )
            "High Stakes Challenge" -> listOf(
                HookVariation(
                    title = "Can you master $niche in under 60 seconds? Watch this.",
                    style = "Speed Challenge",
                    visualActionCue = "Countdown timer graphic running on corner of screen",
                    soundCue = "Fast electronic kick drum with rising riser"
                ),
                HookVariation(
                    title = "I spent 30 days testing the most viral $niche routine so you don't have to.",
                    style = "Mythbuster Test",
                    visualActionCue = "Fast montage of calendar dates and progress shots",
                    soundCue = "Energetic drum roll"
                ),
                HookVariation(
                    title = "If you can make it past the first 5 seconds of this $niche test...",
                    style = "Attention Gate",
                    visualActionCue = "Intense eye tracking with neon graphic countdown",
                    soundCue = "Heavy heartbeat thump"
                )
            )
            else -> listOf(
                HookVariation(
                    title = "Stop scrolling: The most controversial truth about $niche.",
                    style = "Hot Take",
                    visualActionCue = "Direct hand gesture stopping the camera lens",
                    soundCue = "Glass shatter or dramatic bass drop"
                ),
                HookVariation(
                    title = "Everything you were taught about $niche is completely backwards.",
                    style = "Paradigm Shift",
                    visualActionCue = "Inverting the camera angle or flipping the item",
                    soundCue = "Deep subwoofer sweep"
                ),
                HookVariation(
                    title = "Why everyone is suddenly obsessed with $niche in 2026...",
                    style = "Trend Velocity",
                    visualActionCue = "Rapid screen scroll showing viral headlines",
                    soundCue = "Fast camera shutter clicks"
                )
            )
        }
    }
}

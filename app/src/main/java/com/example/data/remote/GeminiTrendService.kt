package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.PlatformType
import com.example.data.model.TrendItem
import com.example.data.model.TrendStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

sealed class GeminiTrendResult {
    data class Success(val trends: List<TrendItem>, val isLiveApi: Boolean, val timestamp: Long) : GeminiTrendResult()
    data class Error(val message: String, val fallbackTrends: List<TrendItem>) : GeminiTrendResult()
}

object GeminiTrendService {
    private const val TAG = "GeminiTrendService"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun fetchRealtimeViralTrends(categoryFilter: String? = null): GeminiTrendResult = withContext(Dispatchers.IO) {
        val rawApiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        val hasValidKey = rawApiKey.isNotBlank() &&
                rawApiKey != "MY_GEMINI_API_KEY" &&
                !rawApiKey.startsWith("MY_")

        if (!hasValidKey) {
            Log.d(TAG, "No valid Gemini API key configured, using live curated radar feed")
            return@withContext GeminiTrendResult.Success(
                trends = getFallbackRealtimeTrends(categoryFilter),
                isLiveApi = false,
                timestamp = System.currentTimeMillis()
            )
        }

        val promptCategory = if (!categoryFilter.isNullOrBlank() && categoryFilter != "All") {
            "focusing specifically on $categoryFilter"
        } else {
            "covering broad breakout trends across TikTok, Instagram Reels, X/Twitter, YouTube Shorts, Memes, and AI culture"
        }

        val prompt = """
            You are a real-time viral media and algorithm analyst.
            Identify 6 to 8 currently trending viral topics, formats, sounds, challenges, or memes $promptCategory.
            Return a strictly valid JSON array of objects without markdown formatting.
            Each object must contain:
            - id: unique alphanumeric string (e.g., 'gemini_trend_1')
            - title: catchy name of the trend
            - platform: one of ['TIKTOK', 'REELS', 'TWITTER_X', 'YOUTUBE', 'MEMES', 'AI']
            - viralityScore: integer 82-99
            - velocity: string velocity rate (e.g., '+850%/hr' or '+1200%/hr')
            - status: one of ['BLOWING_UP', 'PEAKING', 'EMERGING', 'EVERGREEN']
            - oneLiner: 1-sentence punchy summary
            - originStory: concise background on how/where it started
            - whyItWorks: algorithmic/psychological reason for virality
            - howToRemix: clear action advice for how a creator can film or post this
            - audioTrack: viral audio song title and artist
            - audioBpm: integer tempo BPM
            - cuePoint: audio hook point (e.g., '0:05 Beat drop')
            - hashtags: array of 4 strings with hashtags
            - bestTimeToPost: time window string (e.g., '6:00 PM - 9:00 PM')
            - exampleHook: engaging opening hook script line
            - tags: array of 3-4 category keywords
        """.trimIndent()

        val requestJson = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        val partObj = JSONObject().apply {
                            put("text", prompt)
                        }
                        put(partObj)
                    }
                    put("parts", partsArray)
                }
                put(contentObj)
            }
            put("contents", contentsArray)

            val generationConfig = JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.5)
            }
            put("generationConfig", generationConfig)
        }

        val endpointUrl = "$BASE_URL/$MODEL_NAME:generateContent?key=$rawApiKey"
        val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(endpointUrl)
            .post(requestBody)
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.w(TAG, "Gemini API error ${response.code}: $responseString")
                return@withContext GeminiTrendResult.Error(
                    message = "Gemini API responded with HTTP ${response.code}. Showing curated live radar data.",
                    fallbackTrends = getFallbackRealtimeTrends(categoryFilter)
                )
            }

            val parsedTrends = parseGeminiResponse(responseString)
            if (parsedTrends.isNotEmpty()) {
                GeminiTrendResult.Success(
                    trends = parsedTrends,
                    isLiveApi = true,
                    timestamp = System.currentTimeMillis()
                )
            } else {
                GeminiTrendResult.Error(
                    message = "Could not parse Gemini trend response format.",
                    fallbackTrends = getFallbackRealtimeTrends(categoryFilter)
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed calling Gemini API", e)
            GeminiTrendResult.Error(
                message = e.localizedMessage ?: "Network connection error while reaching Gemini API.",
                fallbackTrends = getFallbackRealtimeTrends(categoryFilter)
            )
        }
    }

    private fun parseGeminiResponse(responseJsonStr: String): List<TrendItem> {
        val trends = mutableListOf<TrendItem>()
        try {
            val root = JSONObject(responseJsonStr)
            val candidates = root.optJSONArray("candidates") ?: return trends
            val firstCandidate = candidates.optJSONObject(0) ?: return trends
            val content = firstCandidate.optJSONObject("content") ?: return trends
            val parts = content.optJSONArray("parts") ?: return trends
            val firstPart = parts.optJSONObject(0) ?: return trends
            var text = firstPart.optString("text", "")

            text = text.trim()
            if (text.startsWith("```json")) {
                text = text.removePrefix("```json")
            } else if (text.startsWith("```")) {
                text = text.removePrefix("```")
            }
            if (text.endsWith("```")) {
                text = text.removeSuffix("```")
            }
            text = text.trim()

            val jsonArray = JSONArray(text)
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.optJSONObject(i) ?: continue
                val id = item.optString("id", "gemini_trend_$i")
                val title = item.optString("title", "Trending Topic #$i")

                val platformStr = item.optString("platform", "TIKTOK").uppercase()
                val platform = when {
                    platformStr.contains("TIKTOK") -> PlatformType.TIKTOK
                    platformStr.contains("REEL") || platformStr.contains("INSTA") -> PlatformType.REELS
                    platformStr.contains("TWITTER") || platformStr.contains("X") -> PlatformType.TWITTER_X
                    platformStr.contains("YOUTUBE") || platformStr.contains("SHORT") -> PlatformType.YOUTUBE
                    platformStr.contains("AI") || platformStr.contains("TECH") -> PlatformType.AI
                    else -> PlatformType.MEMES
                }

                val score = item.optInt("viralityScore", 92).coerceIn(1, 100)
                val velocity = item.optString("velocity", "+820%/hr")

                val statusStr = item.optString("status", "BLOWING_UP").uppercase()
                val status = when {
                    statusStr.contains("PEAK") -> TrendStatus.PEAKING
                    statusStr.contains("EMERG") || statusStr.contains("RIS") -> TrendStatus.EMERGING
                    statusStr.contains("EVERGREEN") -> TrendStatus.EVERGREEN
                    else -> TrendStatus.BLOWING_UP
                }

                val oneLiner = item.optString("oneLiner", "Fast-moving viral topic breaking out on social feeds.")
                val originStory = item.optString("originStory", "Discovered across high-engagement algorithmic feeds.")
                val whyItWorks = item.optString("whyItWorks", "High retention hook, strong audio cue, and high share-to-friend ratio.")
                val howToRemix = item.optString("howToRemix", "Apply your personal spin using the primary audio hook and relatable POV framing.")
                val audioTrack = item.optString("audioTrack", "Trending Audio Original Sound")
                val audioBpm = item.optInt("audioBpm", 120)
                val cuePoint = item.optString("cuePoint", "0:04 Beat drop")

                val hashtagsList = mutableListOf<String>()
                item.optJSONArray("hashtags")?.let { arr ->
                    for (h in 0 until arr.length()) {
                        val tag = arr.optString(h)
                        if (tag.isNotBlank()) hashtagsList.add(if (tag.startsWith("#")) tag else "#$tag")
                    }
                }
                if (hashtagsList.isEmpty()) {
                    hashtagsList.addAll(listOf("#viral", "#trending", "#foryou", "#trendingnow"))
                }

                val bestTimeToPost = item.optString("bestTimeToPost", "5:00 PM - 8:30 PM")
                val exampleHook = item.optString("exampleHook", "You won't believe what just happened...")

                val tagsList = mutableListOf<String>()
                item.optJSONArray("tags")?.let { arr ->
                    for (t in 0 until arr.length()) {
                        val tg = arr.optString(t)
                        if (tg.isNotBlank()) tagsList.add(tg)
                    }
                }
                if (tagsList.isEmpty()) {
                    tagsList.addAll(listOf("Viral", "Algorithm", "Trending"))
                }

                trends.add(
                    TrendItem(
                        id = id,
                        title = title,
                        platform = platform,
                        viralityScore = score,
                        velocity = velocity,
                        status = status,
                        oneLiner = oneLiner,
                        originStory = originStory,
                        whyItWorks = whyItWorks,
                        howToRemix = howToRemix,
                        audioTrack = audioTrack,
                        audioBpm = audioBpm,
                        cuePoint = cuePoint,
                        hashtags = hashtagsList,
                        bestTimeToPost = bestTimeToPost,
                        exampleHook = exampleHook,
                        tags = tagsList
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Gemini response", e)
        }
        return trends
    }

    fun getFallbackRealtimeTrends(categoryFilter: String? = null): List<TrendItem> {
        val all = listOf(
            TrendItem(
                id = "trend_chill_guy",
                title = "Chill Guy / 'Just Doing My Thing'",
                platform = PlatformType.MEMES,
                viralityScore = 99,
                velocity = "+950%/hr",
                status = TrendStatus.BLOWING_UP,
                oneLiner = "Unbothered cartoon character standing calmly in rolled sweater amidst complete turmoil.",
                originStory = "Illustrated by artist Phillip Banks, adopted as the universal internet totem of serene non-reaction.",
                whyItWorks = "Ultimate relatable escapism; viewers superimpose their chaotic life dilemmas over a completely relaxed character.",
                howToRemix = "Use format: 'When [unhinged disaster unfolds around you] but you're just a chill guy who doesn't stress.'",
                audioTrack = "Vince Guaraldi Trio - Linus and Lucy (Chill Lofi Remix)",
                audioBpm = 86,
                cuePoint = "0:06 Drop",
                hashtags = listOf("#chillguy", "#unbothered", "#relatable", "#peaceful", "#memes"),
                bestTimeToPost = "7:00 PM - 10:00 PM",
                exampleHook = "I'm literally just a chill guy who minding his own business...",
                tags = listOf("Meme", "Relatable", "Humor", "Viral")
            ),
            TrendItem(
                id = "trend_dubai_chocolate",
                title = "Dubai Pistachio Kunafa Chocolate",
                platform = PlatformType.TIKTOK,
                viralityScore = 98,
                velocity = "+720%/hr",
                status = TrendStatus.BLOWING_UP,
                oneLiner = "Thick milk chocolate bars oozing crispy golden kunafa pastry and vivid pistachio cream.",
                originStory = "Crafted by Fix Dessert Chocolatier in Dubai, went global after ASMR snaps exploded across TikTok.",
                whyItWorks = "Sensory overload: loud chocolate crunch, glowing green interior, and unattainable luxury allure.",
                howToRemix = "Make a DIY skillet toasted kunafa batch or film an extreme close-up mic snap reaction test.",
                audioTrack = "Crispy ASMR Kunafa Snap (Original Audio)",
                audioBpm = 110,
                cuePoint = "0:03 Loud Crunch",
                hashtags = listOf("#dubaichocolate", "#pistachiokunafa", "#asmrfood", "#dessertreview"),
                bestTimeToPost = "12:00 PM - 3:00 PM",
                exampleHook = "I spent $65 trying the viral Dubai chocolate so you don't have to...",
                tags = listOf("Food", "ASMR", "Viral", "Luxury")
            ),
            TrendItem(
                id = "trend_ai_claymation",
                title = "AI Claymation & Stop-Motion Nostalgia",
                platform = PlatformType.AI,
                viralityScore = 97,
                velocity = "+880%/hr",
                status = TrendStatus.PEAKING,
                oneLiner = "Re-rendering modern pop-culture moments and movie scenes as nostalgic 1980s plasticine claymation.",
                originStory = "Generative video tool advancements (Midjourney + Kling/Luma) allowed tactile clay textures to look real.",
                whyItWorks = "Triggers intense warm nostalgia while showing beloved modern celebrities or video games in handmade clay style.",
                howToRemix = "Prompt an iconic dialogue scene from your favorite movie styled as Aardman / Wallace and Gromit clay.",
                audioTrack = "Plasticine Dreams (Whimsical Synth)",
                audioBpm = 98,
                cuePoint = "0:04 Transition",
                hashtags = listOf("#claymation", "#aianimation", "#nostalgia", "#stopmotion", "#filmtok"),
                bestTimeToPost = "5:00 PM - 8:00 PM",
                exampleHook = "What if The Matrix was animated entirely in clay in 1984?",
                tags = listOf("AI", "Animation", "Tech", "Creative")
            ),
            TrendItem(
                id = "trend_underconsumption",
                title = "Underconsumption Core / 'Anti-Haul'",
                platform = PlatformType.REELS,
                viralityScore = 94,
                velocity = "+510%/hr",
                status = TrendStatus.EVERGREEN,
                oneLiner = "Showing well-worn sneakers, second-hand furniture, and using products until they are completely empty.",
                originStory = "Backlash against endless Amazon prime hauls, fast-fashion unboxings, and consumer fatigue.",
                whyItWorks = "Validates financial mindfulness; relieves viewer anxiety about needing to buy endless new aesthetic items.",
                howToRemix = "Tour your apartment showing 5 things you've owned for 5+ years that you refuse to replace.",
                audioTrack = "Mac DeMarco - Heart to Heart (Soft Acoustic)",
                audioBpm = 82,
                cuePoint = "0:08 Instrumental swell",
                hashtags = listOf("#underconsumption", "#antihaul", "#mindfulliving", "#minimalism", "#deinfluencing"),
                bestTimeToPost = "8:00 AM - 11:00 AM",
                exampleHook = "Here is my deeply unglamorous 'underconsumption core' apartment tour...",
                tags = listOf("Lifestyle", "Finance", "Minimalism", "Relatable")
            ),
            TrendItem(
                id = "trend_yap_session",
                title = "'Yapping' & 30-Second Mini-Podcasts",
                platform = PlatformType.YOUTUBE,
                viralityScore = 93,
                velocity = "+440%/hr",
                status = TrendStatus.EMERGING,
                oneLiner = "Creators holding wired Apple earbuds like a mini microphone delivering unhinged deep-dives.",
                originStory = "Casual vloggers realizing low-fi handheld earbud audio feels more intimate than $800 studio setups.",
                whyItWorks = "Simulates FaceTime call with your funniest friend; low friction, zero editing, maximum charisma.",
                howToRemix = "Hold the wired earbud mic to your mouth in your car and explain an absurd shower thought in 35 seconds.",
                audioTrack = "Cozy Coffeehouse Background Jazz (Subtle Ambience)",
                audioBpm = 114,
                cuePoint = "Continuous Low Bed",
                hashtags = listOf("#yapping", "#minipodcast", "#povvlog", "#storytime", "#showerthoughts"),
                bestTimeToPost = "8:30 PM - 11:30 PM",
                exampleHook = "Okay bear with me, because my brain just made a terrifying connection...",
                tags = listOf("Storytime", "Vlog", "Humor", "Podcast")
            ),
            TrendItem(
                id = "trend_brainrot_quiz",
                title = "Gen Alpha Slang / 'Brainrot' Translation",
                platform = PlatformType.TWITTER_X,
                viralityScore = 91,
                velocity = "+390%/hr",
                status = TrendStatus.PEAKING,
                oneLiner = "Translating classic literary masterpieces or corporate emails into full Gen Alpha Brainrot slang.",
                originStory = "Satirical blending of TikTok slang (Rizz, Skibidi, Ohio, Sigma) with serious formal texts.",
                whyItWorks = "Generational gap humor; absurd contrast between Shakespearean prose and hyperbolic internet slang.",
                howToRemix = "Take a standard corporate resignation letter or wedding vow and rewrite it using slang subtitles.",
                audioTrack = "Kevin MacLeod - Monkeys Spinning Monkeys",
                audioBpm = 140,
                cuePoint = "0:02 Fast Start",
                hashtags = listOf("#brainrot", "#genalpha", "#corporatelife", "#humor", "#skibidi"),
                bestTimeToPost = "1:00 PM - 4:00 PM",
                exampleHook = "I translated the Declaration of Independence into Gen Alpha slang and I'm crying...",
                tags = listOf("Humor", "Satire", "Slang", "Internet Culture")
            )
        )

        return if (!categoryFilter.isNullOrBlank() && categoryFilter != "All") {
            val filtered = all.filter { trend ->
                trend.tags.any { it.contains(categoryFilter, ignoreCase = true) } ||
                        trend.platform.displayName.contains(categoryFilter, ignoreCase = true)
            }
            if (filtered.isNotEmpty()) filtered else all
        } else {
            all
        }
    }
}

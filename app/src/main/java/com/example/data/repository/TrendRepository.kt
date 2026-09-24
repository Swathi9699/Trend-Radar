package com.example.data.repository

import com.example.data.local.TrendDao
import com.example.data.model.BattleItem
import com.example.data.model.ChallengeEntity
import com.example.data.model.CreatedMemeEntity
import com.example.data.model.PlatformType
import com.example.data.model.SavedTrendEntity
import com.example.data.model.TrendItem
import com.example.data.model.TrendStatus
import com.example.data.model.TrendVoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class TrendRepository(private val dao: TrendDao) {

    val savedTrends: Flow<List<SavedTrendEntity>> = dao.getAllSavedTrends()
    val createdMemes: Flow<List<CreatedMemeEntity>> = dao.getAllCreatedMemes()
    val challenges: Flow<List<ChallengeEntity>> = dao.getChallenges()

    suspend fun saveTrend(trend: TrendItem, notes: String = "") {
        val entity = SavedTrendEntity(
            id = trend.id,
            title = trend.title,
            platform = trend.platform.name,
            viralityScore = trend.viralityScore,
            velocity = trend.velocity,
            oneLiner = trend.oneLiner,
            audioTrack = trend.audioTrack,
            hashtags = trend.hashtags.joinToString(" "),
            userNotes = notes
        )
        dao.saveTrend(entity)
    }

    suspend fun removeSavedTrend(trendId: String) {
        dao.removeSavedTrend(trendId)
    }

    fun isTrendSaved(trendId: String): Flow<Boolean> = dao.isTrendSaved(trendId)

    suspend fun saveMeme(meme: CreatedMemeEntity): Long {
        return dao.insertCreatedMeme(meme)
    }

    suspend fun deleteMeme(memeId: Long) {
        dao.deleteCreatedMeme(memeId)
    }

    suspend fun voteInBattle(battleId: String, option: Int) {
        dao.recordVote(TrendVoteEntity(battleId, option))
    }

    suspend fun toggleChallenge(challenge: ChallengeEntity) {
        val updated = challenge.copy(
            isCompleted = !challenge.isCompleted,
            completedAt = if (!challenge.isCompleted) System.currentTimeMillis() else 0L
        )
        dao.updateChallenge(updated)
    }

    suspend fun initializeChallenges() {
        val defaultChallenges = listOf(
            ChallengeEntity(
                id = "day_1",
                dayNumber = 1,
                title = "The 1.5s Visual Pattern Interrupt",
                objective = "Film an opening shot with unexpected movement or text overlay within the first 1.5 seconds.",
                proTip = "Stop the scroll immediately before viewers register who is speaking."
            ),
            ChallengeEntity(
                id = "day_2",
                dayNumber = 2,
                title = "The Curiosity Gap Headline",
                objective = "Post a video or meme posing a mystery that is only answered at the very end.",
                proTip = "Use phrases like 'The one mistake 90% of people make without knowing...'."
            ),
            ChallengeEntity(
                id = "day_3",
                dayNumber = 3,
                title = "Audio Drop & Sound Sync",
                objective = "Edit your video's main punchline or reveal exactly on the beat drop of a trending sound.",
                proTip = "Look for audio tracks in the '🔥 Blowing Up' section with >130 BPM."
            ),
            ChallengeEntity(
                id = "day_4",
                dayNumber = 4,
                title = "The Relatable Pain Point POV",
                objective = "Create a POV post focusing on an everyday micro-frustration everyone experiences.",
                proTip = "Hyper-specific relatable situations generate 3x more comments."
            ),
            ChallengeEntity(
                id = "day_5",
                dayNumber = 5,
                title = "The Infinite Seamless Loop",
                objective = "Craft the last word of your video to grammatically flow directly into your opening sentence.",
                proTip = "Users replay it 1.5x on average, supercharging the algorithm score."
            ),
            ChallengeEntity(
                id = "day_6",
                dayNumber = 6,
                title = "The Contrarian Hot Take",
                objective = "Politely challenge a common internet consensus with evidence or personal experience.",
                proTip = "Sparks healthy debate in comments, multiplying distribution."
            ),
            ChallengeEntity(
                id = "day_7",
                dayNumber = 7,
                title = "The Save-Worthy Resource Pack",
                objective = "Share a concise checklist, recipe, or cheat-sheet explicitly prompting: 'Bookmark this for later'.",
                proTip = "Saves are weighted 2x higher than likes by modern recommendation engines."
            )
        )
        dao.seedChallenges(defaultChallenges)
    }

    // Combine static battles with user votes from Room
    fun getBattlesWithVotes(): Flow<List<BattleItem>> {
        return dao.getAllVotes().combine(
            kotlinx.coroutines.flow.flowOf(initialBattles)
        ) { votes, battles ->
            val voteMap = votes.associate { it.battleId to it.chosenOption }
            battles.map { battle ->
                val userChoice = voteMap[battle.id] ?: 0
                val adjustedOp1 = if (userChoice == 1) battle.option1Votes + 1 else battle.option1Votes
                val adjustedOp2 = if (userChoice == 2) battle.option2Votes + 1 else battle.option2Votes
                battle.copy(
                    option1Votes = adjustedOp1,
                    option2Votes = adjustedOp2,
                    userVote = userChoice
                )
            }
        }
    }

    val liveTrends: List<TrendItem> = listOf(
        TrendItem(
            id = "trend_chill_guy",
            title = "Chill Guy / 'Just Doing My Thing'",
            platform = PlatformType.MEMES,
            viralityScore = 99,
            velocity = "+920%/hr",
            status = TrendStatus.BLOWING_UP,
            oneLiner = "Unbothered character in rolled-up sweater standing calmly amidst extreme chaos.",
            originStory = "Created by artist Phillip Banks, adopted as the universal symbol of zen non-reaction to stressful modern life.",
            whyItWorks = "Ultimate relatable escapism; users overlay their most stressful personal dilemmas over his relaxed stance.",
            howToRemix = "Use template: 'When [terrible catastrophe happens] but you're just a chill guy who doesn't let things bother him'.",
            audioTrack = "Vince Guaraldi Trio - Linus and Lucy (Chill Lofi Remix)",
            audioBpm = 86,
            cuePoint = "0:06 Drop",
            hashtags = listOf("#chillguy", "#unbothered", "#relatable", "#memes", "#peaceful"),
            bestTimeToPost = "7:00 PM - 10:00 PM",
            exampleHook = "I'm literally just a chill guy who avoids drama at all costs...",
            tags = listOf("Meme", "Relatable", "Cartoon", "Humor")
        ),
        TrendItem(
            id = "trend_dubai_chocolate",
            title = "Dubai Pistachio Kunafa Chocolate",
            platform = PlatformType.TIKTOK,
            viralityScore = 98,
            velocity = "+580%/hr",
            status = TrendStatus.BLOWING_UP,
            oneLiner = "Thick chocolate bars packed with toasted crispy kunafa and creamy green pistachio tahini.",
            originStory = "Originating from Fix Dessert Chocolatier in Dubai, skyrocketed after ASMR unboxing and snap sounds went wild.",
            whyItWorks = "Triple sensory combo: Loud crack ASMR, bright neon pistachio aesthetic, and extreme decadence.",
            howToRemix = "Do a DIY home attempt or an exaggerated taste test reaction focusing on the mic-close crunch.",
            audioTrack = "ASMR Crispy Bar Break (Original Audio)",
            audioBpm = 110,
            cuePoint = "0:03 Snapping sound",
            hashtags = listOf("#dubaichocolate", "#pistachiokunafa", "#asmrfood", "#dessertreview", "#crunch"),
            bestTimeToPost = "12:00 PM - 3:00 PM",
            exampleHook = "I spent $65 trying the viral Dubai chocolate so you don't have to...",
            tags = listOf("Food", "ASMR", "Viral Food", "Luxury")
        ),
        TrendItem(
            id = "trend_ai_claymation",
            title = "AI Claymation & Plasticine Worlds",
            platform = PlatformType.AI,
            viralityScore = 96,
            velocity = "+740%/hr",
            status = TrendStatus.BLOWING_UP,
            oneLiner = "Real life movie scenes and street videos transformed into tactile Wallace & Gromit style clay models.",
            originStory = "Diffusion models tuned with stop-motion clay style prompting went viral across X and TikTok reels.",
            whyItWorks = "Nostalgic tactile texture that tricks the brain into feeling warm handmade stop-motion cinema.",
            howToRemix = "Take a high-energy pop music video or iconic dialogue scene and render it with fingerprinted clay texture.",
            audioTrack = "French Cafe Accordion (Whimsical Stopmotion Cut)",
            audioBpm = 120,
            cuePoint = "0:08 Beat switch",
            hashtags = listOf("#aianimation", "#claymation", "#stopmotion", "#creativeai", "#filmmaking"),
            bestTimeToPost = "4:00 PM - 7:00 PM",
            exampleHook = "What if The Matrix was animated entirely out of Play-Doh?",
            tags = listOf("AI", "Animation", "Tech", "Cinema")
        ),
        TrendItem(
            id = "trend_corporate_speak",
            title = "Corporate Speak Honest Translation",
            platform = PlatformType.TWITTER_X,
            viralityScore = 95,
            velocity = "+450%/hr",
            status = TrendStatus.PEAKING,
            oneLiner = "Bitingly funny subtitles translating corporate passive-aggressive emails into raw human truth.",
            originStory = "Remote workers venting humorously about 'Per my last note' and 'circling back to this'.",
            whyItWorks = "Universal workplace catharsis; viewers quote-tweet with their own company horror stories.",
            howToRemix = "Format as split screen or bulleted list: 'What I wrote: As discussed. What I meant: Why didn't you listen the first 3 times?'",
            audioTrack = "Elevator Bossa Nova (Lo-Fi Office Beat)",
            audioBpm = 95,
            cuePoint = "0:05 Chime",
            hashtags = listOf("#corporatelife", "#wfh", "#workhumor", "#officememes", "#corporatespeak"),
            bestTimeToPost = "8:30 AM - 11:30 AM",
            exampleHook = "Translating corporate emails so you don't get fired (Part 4)...",
            tags = listOf("Humor", "Work", "X Post", "Relatable")
        ),
        TrendItem(
            id = "trend_loop_trick",
            title = "The 3-Second Retention Infinite Loop",
            platform = PlatformType.YOUTUBE,
            viralityScore = 94,
            velocity = "+390%/hr",
            status = TrendStatus.PEAKING,
            oneLiner = "Video scripting technique where the final spoken word perfectly completes the opening sentence.",
            originStory = "Shorts creators hacking the YouTube Shorts algorithm to achieve 130%+ average watch duration.",
            whyItWorks = "Algorithms reward high completion rate; viewers don't realize it started over until 2 seconds in.",
            howToRemix = "End with: '...and that is why you should always start with...' -> beginning: '...the hardest challenge first!'",
            audioTrack = "Subtle Ambient Future Bass Pulse",
            audioBpm = 128,
            cuePoint = "0:00 Seamless loop",
            hashtags = listOf("#creators", "#youtubeshorts", "#retentionhack", "#videotips", "#viralgrowth"),
            bestTimeToPost = "2:00 PM - 5:00 PM",
            exampleHook = "The biggest secret top YouTubers use to double their watch time...",
            tags = listOf("Growth", "Editing", "Hack", "Strategy")
        ),
        TrendItem(
            id = "trend_moo_deng",
            title = "Moo Deng Chaos Hippo Energy",
            platform = PlatformType.MEMES,
            viralityScore = 97,
            velocity = "+510%/hr",
            status = TrendStatus.PEAKING,
            oneLiner = "Baby pygmy hippo with glossy pink cheeks experiencing unprompted bursts of dramatic ferocity.",
            originStory = "Khao Kheow Open Zoo in Thailand shared footage of baby hippo Moo Deng being sprayed with water.",
            whyItWorks = "Pure serotonin booster; chaotic screaming cute animal creates instant meme templates for overwhelm.",
            howToRemix = "Pair clips of Moo Deng screaming with relatable everyday overwhelm moments.",
            audioTrack = "Dramatic Opera Screaming Choir (Remix)",
            audioBpm = 140,
            cuePoint = "0:04 Chaos scream",
            hashtags = listOf("#moodeng", "#pygmyhippo", "#chaosenergy", "#cutememes", "#mood"),
            bestTimeToPost = "11:00 AM - 2:00 PM",
            exampleHook = "Me trying to handle adult responsibilities on a Monday morning...",
            tags = listOf("Animal", "Wholesome", "Chaos", "Viral")
        ),
        TrendItem(
            id = "trend_05x_fisheye",
            title = "0.5x Ultra-Wide Candid Drop",
            platform = PlatformType.REELS,
            viralityScore = 91,
            velocity = "+270%/hr",
            status = TrendStatus.EMERGING,
            oneLiner = "Extreme overhead angle photos with exaggerated forehead and distorted footwear, high-flash.",
            originStory = "Gen Z rejection of curated perfection, embracing goofy candid wide-lens distortion.",
            whyItWorks = "Playful anti-aesthetic, feels authentic and funny without trying too hard.",
            howToRemix = "Hold phone at arms length tilted 45 degrees down with flash on in a mundane spot like grocery aisles.",
            audioTrack = "Brat Charli XCX - 360 (Club Edit)",
            audioBpm = 132,
            cuePoint = "0:07 Drop",
            hashtags = listOf("#05photo", "#wideangle", "#aesthetic", "#candid", "#genzphotography"),
            bestTimeToPost = "6:00 PM - 9:00 PM",
            exampleHook = "Proof that 0.5x lens makes any mundane Tuesday look iconic...",
            tags = listOf("Photography", "Aesthetic", "Fashion", "Vibe")
        ),
        TrendItem(
            id = "trend_silent_review",
            title = "Silent Review & Nail Tap ASMR",
            platform = PlatformType.TIKTOK,
            viralityScore = 90,
            velocity = "+320%/hr",
            status = TrendStatus.EMERGING,
            oneLiner = "Reviewing skincare, books, or gadgets with zero spoken words—only expressive side-eyes and thumbs.",
            originStory = "Began in beauty community as an antidote to noisy sponsored pitches.",
            whyItWorks = "Fast-paced visual grading, high retention because viewers eagerly wait for the face reaction.",
            howToRemix = "Line up 5 items you use daily. Show product, tap it, make a deadpan smile or disgust face, score 1-10.",
            audioTrack = "Satisfying Tap Tap Click (Pure Audio)",
            audioBpm = 100,
            cuePoint = "0:02 Tap sound",
            hashtags = listOf("#silentreviews", "#asmrtapping", "#honestreview", "#nobs", "#beautyreview"),
            bestTimeToPost = "1:00 PM - 4:00 PM",
            exampleHook = "[Silent text on screen]: Rating everything in my bag honestly without saying a word...",
            tags = listOf("Review", "ASMR", "Minimal", "Format")
        ),
        TrendItem(
            id = "trend_brainrot_opera",
            title = "Symphonic Opera Covers of Slang",
            platform = PlatformType.TIKTOK,
            viralityScore = 93,
            velocity = "+610%/hr",
            status = TrendStatus.BLOWING_UP,
            oneLiner = "Grand orchestra, operatic tenors, and string quartets performing absurd modern internet slang.",
            originStory = "Classical vocalists performing dramatic arias with lyrics like 'skibidi rizzler sigma'.",
            whyItWorks = "Extreme highbrow/lowbrow irony; beautiful orchestral music juxtaposed with dumb meme vocabulary.",
            howToRemix = "Lip sync dramatically in a ballroom or tuxedo to operatic brainrot tracks with serious facial acting.",
            audioTrack = "Verdi - Requiem (Dies Irae) Brainrot Soprano",
            audioBpm = 145,
            cuePoint = "0:09 Crescendo",
            hashtags = listOf("#brainrot", "#operacover", "#classicalmusic", "#slang", "#irony"),
            bestTimeToPost = "5:00 PM - 8:00 PM",
            exampleHook = "When the orchestra takes internet slang way too seriously...",
            tags = listOf("Music", "Parody", "Humor", "Audio")
        ),
        TrendItem(
            id = "trend_nostalgia_2014",
            title = "POV: It's Autumn 2014",
            platform = PlatformType.REELS,
            viralityScore = 89,
            velocity = "+180%/hr",
            status = TrendStatus.EVERGREEN,
            oneLiner = "Reliving the Tumblr era, Flappy Bird, Vine clips, and early iPhone camera roll nostalgia.",
            originStory = "10-year anniversary of 2014 sparking massive wave of longing for pre-algorithm social media.",
            whyItWorks = "Deep nostalgic emotional trigger for Gen Z & Millennials yearning for simpler digital eras.",
            howToRemix = "Compile 5-second rapid photo flashes from 2014 paired with Lorde or The 1975.",
            audioTrack = "The 1975 - Chocolate (Vintage Slowed)",
            audioBpm = 98,
            cuePoint = "0:12 Chorus",
            hashtags = listOf("#2014nostalgia", "#tumblrera", "#throwback", "#nostalgic", "#goldenyears"),
            bestTimeToPost = "8:00 PM - 11:00 PM",
            exampleHook = "POV: You just woke up on a Saturday morning in October 2014...",
            tags = listOf("Nostalgia", "Vibe", "Music", "Aesthetic")
        )
    )

    private val initialBattles = listOf(
        BattleItem(
            id = "battle_dubai_vs_crumbl",
            title = "The Viral Treat Showdown",
            category = "Food Craze",
            option1Title = "Dubai Kunafa Bar",
            option1Subtitle = "Pistachio crunch & melted chocolate",
            option1Emoji = "🍫",
            option1Votes = 14200,
            option2Title = "Crumbl Cookie Craze",
            option2Subtitle = "Weekly rotating frosted towers",
            option2Emoji = "🍪",
            option2Votes = 11840
        ),
        BattleItem(
            id = "battle_chillguy_vs_moodeng",
            title = "Meme Mascot of the Year",
            category = "Pop Culture",
            option1Title = "Chill Guy",
            option1Subtitle = "Unbothered, zen, hands in pockets",
            option1Emoji = "🚶‍♂️",
            option1Votes = 18600,
            option2Title = "Moo Deng",
            option2Subtitle = "Screaming chaotic pink baby hippo",
            option2Emoji = "🦛",
            option2Votes = 19450
        ),
        BattleItem(
            id = "battle_05x_vs_vintage",
            title = "Gen-Z Camera Aesthetic",
            category = "Style & Photo",
            option1Title = "0.5x Ultra-Wide Fisheye",
            option1Subtitle = "Distorted candid top-down flash",
            option1Emoji = "📸",
            option1Votes = 9400,
            option2Title = "Vintage CCD Digicam",
            option2Subtitle = "2008 blurry washed-out warmth",
            option2Emoji = "🎞️",
            option2Votes = 10250
        ),
        BattleItem(
            id = "battle_phonk_vs_lofi",
            title = "Ultimate Focus Audio",
            category = "Soundtracks",
            option1Title = "Drift Phonk & Bass",
            option1Subtitle = "Cowbell rhythm & 140 BPM hyperdrive",
            option1Emoji = "🏎️",
            option1Votes = 8120,
            option2Title = "Rainy Lo-Fi Chill",
            option2Subtitle = "Muted piano & warm vinyl crackle",
            option2Emoji = "☕",
            option2Votes = 12790
        )
    )
}

package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.BattleItem
import com.example.data.model.ChallengeEntity
import com.example.data.model.CreatedMemeEntity
import com.example.data.model.PlatformType
import com.example.data.model.SavedTrendEntity
import com.example.data.model.TrendItem
import com.example.data.model.TrendStatus
import com.example.data.remote.GeminiTrendResult
import com.example.data.remote.GeminiTrendService
import com.example.data.repository.TrendRepository
import com.example.domain.HookVariation
import com.example.domain.ViralityAnalysisResult
import com.example.domain.ViralityEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ViralTrendViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TrendRepository
    private val prefs = application.getSharedPreferences("creator_profile_prefs", Context.MODE_PRIVATE)

    // Creator Profile Info
    private val _creatorDisplayName = MutableStateFlow(
        prefs.getString("creator_name", "Viral Trendmaker") ?: "Viral Trendmaker"
    )
    val creatorDisplayName: StateFlow<String> = _creatorDisplayName.asStateFlow()

    private val _creatorHandle = MutableStateFlow(
        prefs.getString("creator_handle", "@trendpulse_creator") ?: "@trendpulse_creator"
    )
    val creatorHandle: StateFlow<String> = _creatorHandle.asStateFlow()

    fun updateCreatorProfile(name: String, handle: String) {
        val cleanName = name.trim().ifBlank { "Viral Trendmaker" }
        val rawHandle = handle.trim().removePrefix("@").ifBlank { "creator" }
        val cleanHandle = "@$rawHandle"

        _creatorDisplayName.value = cleanName
        _creatorHandle.value = cleanHandle
        _memeAuthor.value = cleanHandle

        prefs.edit()
            .putString("creator_name", cleanName)
            .putString("creator_handle", cleanHandle)
            .apply()
    }

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TrendRepository(db.trendDao())
        viewModelScope.launch {
            repository.initializeChallenges()
        }
    }

    // Navigation
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    fun selectTab(tabIndex: Int) {
        _currentTab.value = tabIndex
    }

    // Radar Screen State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedPlatform = MutableStateFlow<PlatformType?>(null)
    val selectedPlatform: StateFlow<PlatformType?> = _selectedPlatform.asStateFlow()

    private val _selectedStatus = MutableStateFlow<TrendStatus?>(null)
    val selectedStatus: StateFlow<TrendStatus?> = _selectedStatus.asStateFlow()

    private val _selectedTrendForDetail = MutableStateFlow<TrendItem?>(null)
    val selectedTrendForDetail: StateFlow<TrendItem?> = _selectedTrendForDetail.asStateFlow()

    // Simulated Audio Playing Track ID
    private val _playingAudioTrackId = MutableStateFlow<String?>(null)
    val playingAudioTrackId: StateFlow<String?> = _playingAudioTrackId.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun filterByPlatform(platform: PlatformType?) {
        _selectedPlatform.value = if (_selectedPlatform.value == platform) null else platform
    }

    fun filterByStatus(status: TrendStatus?) {
        _selectedStatus.value = if (_selectedStatus.value == status) null else status
    }

    fun openTrendDetail(trend: TrendItem) {
        _selectedTrendForDetail.value = trend
    }

    fun closeTrendDetail() {
        _selectedTrendForDetail.value = null
    }

    fun toggleAudioPreview(trendId: String) {
        if (_playingAudioTrackId.value == trendId) {
            _playingAudioTrackId.value = null
        } else {
            _playingAudioTrackId.value = trendId
        }
    }

    // Gemini AI Real-Time Trend Scanner State
    private val _liveTrendsState = MutableStateFlow<List<TrendItem>>(repository.liveTrends)
    val liveTrendsState: StateFlow<List<TrendItem>> = _liveTrendsState.asStateFlow()
    val liveTrends: List<TrendItem>
        get() = _liveTrendsState.value

    private val _isGeminiScanning = MutableStateFlow(false)
    val isGeminiScanning: StateFlow<Boolean> = _isGeminiScanning.asStateFlow()

    private val _isGeminiLiveFeed = MutableStateFlow(false)
    val isGeminiLiveFeed: StateFlow<Boolean> = _isGeminiLiveFeed.asStateFlow()

    private val _geminiScanMessage = MutableStateFlow<String?>("Gemini 3.5 Flash Radar Active")
    val geminiScanMessage: StateFlow<String?> = _geminiScanMessage.asStateFlow()

    private val _lastScanTimestamp = MutableStateFlow<Long>(System.currentTimeMillis())
    val lastScanTimestamp: StateFlow<Long> = _lastScanTimestamp.asStateFlow()

    private val _selectedCategoryTopic = MutableStateFlow("All")
    val selectedCategoryTopic: StateFlow<String> = _selectedCategoryTopic.asStateFlow()

    fun setCategoryTopic(category: String) {
        _selectedCategoryTopic.value = category
        refreshTrendsWithGemini(category)
    }

    fun refreshTrendsWithGemini(category: String? = _selectedCategoryTopic.value) {
        viewModelScope.launch {
            _isGeminiScanning.value = true
            _geminiScanMessage.value = "Scanning viral web signals with Gemini 3.5 Flash..."
            try {
                when (val result = GeminiTrendService.fetchRealtimeViralTrends(category)) {
                    is GeminiTrendResult.Success -> {
                        _liveTrendsState.value = result.trends
                        _isGeminiLiveFeed.value = result.isLiveApi
                        _lastScanTimestamp.value = result.timestamp
                        _geminiScanMessage.value = if (result.isLiveApi) {
                            "✨ Live viral trends synthesized via Gemini 3.5 Flash"
                        } else {
                            "⚡ Real-time radar stream active (Configure Secrets for custom prompts)"
                        }
                    }
                    is GeminiTrendResult.Error -> {
                        _liveTrendsState.value = result.fallbackTrends
                        _isGeminiLiveFeed.value = false
                        _geminiScanMessage.value = result.message
                    }
                }
            } catch (e: Exception) {
                _geminiScanMessage.value = "Error updating trends: ${e.localizedMessage}"
            } finally {
                _isGeminiScanning.value = false
            }
        }
    }

    val savedTrends: StateFlow<List<SavedTrendEntity>> = repository.savedTrends
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val createdMemes: StateFlow<List<CreatedMemeEntity>> = repository.createdMemes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val challenges: StateFlow<List<ChallengeEntity>> = repository.challenges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val battles: StateFlow<List<BattleItem>> = repository.getBattlesWithVotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleSaveTrend(trend: TrendItem) {
        viewModelScope.launch {
            val isAlreadySaved = savedTrends.value.any { it.id == trend.id }
            if (isAlreadySaved) {
                repository.removeSavedTrend(trend.id)
            } else {
                repository.saveTrend(trend)
            }
        }
    }

    fun removeSavedTrend(trendId: String) {
        viewModelScope.launch {
            repository.removeSavedTrend(trendId)
        }
    }

    // Battles
    fun voteInBattle(battleId: String, option: Int) {
        viewModelScope.launch {
            repository.voteInBattle(battleId, option)
        }
    }

    // Challenges
    fun toggleChallenge(challenge: ChallengeEntity) {
        viewModelScope.launch {
            repository.toggleChallenge(challenge)
        }
    }

    // Virality Lab (Predictor)
    private val _hookInputText = MutableStateFlow("Wait until the end to see what happened to this secret recipe...")
    val hookInputText: StateFlow<String> = _hookInputText.asStateFlow()

    private val _analysisResult = MutableStateFlow<ViralityAnalysisResult>(
        ViralityEngine.analyzeText("Wait until the end to see what happened to this secret recipe...")
    )
    val analysisResult: StateFlow<ViralityAnalysisResult> = _analysisResult.asStateFlow()

    fun updateHookInput(text: String) {
        _hookInputText.value = text
        _analysisResult.value = ViralityEngine.analyzeText(text)
    }

    // Hook Generator
    private val _selectedNiche = MutableStateFlow("Food & Treats")
    val selectedNiche: StateFlow<String> = _selectedNiche.asStateFlow()

    private val _selectedHookStyle = MutableStateFlow("Curiosity Gap")
    val selectedHookStyle: StateFlow<String> = _selectedHookStyle.asStateFlow()

    private val _generatedHooks = MutableStateFlow<List<HookVariation>>(
        ViralityEngine.generateHooks("Food & Treats", "Curiosity Gap")
    )
    val generatedHooks: StateFlow<List<HookVariation>> = _generatedHooks.asStateFlow()

    fun selectNiche(niche: String) {
        _selectedNiche.value = niche
        generateHooks()
    }

    fun selectHookStyle(style: String) {
        _selectedHookStyle.value = style
        generateHooks()
    }

    private fun generateHooks() {
        _generatedHooks.value = ViralityEngine.generateHooks(_selectedNiche.value, _selectedHookStyle.value)
    }

    // Meme Studio State
    private val _memeTemplate = MutableStateFlow("DARK_TWEET") // DARK_TWEET, IMPACT, BREAKING_NEWS, MINIMAL_POV
    val memeTemplate: StateFlow<String> = _memeTemplate.asStateFlow()

    private val _memeTopText = MutableStateFlow("POV: You're just a chill guy")
    val memeTopText: StateFlow<String> = _memeTopText.asStateFlow()

    private val _memeBottomText = MutableStateFlow("Watching everyone stress over things that don't matter")
    val memeBottomText: StateFlow<String> = _memeBottomText.asStateFlow()

    private val _memeAuthor = MutableStateFlow(
        prefs.getString("creator_handle", "@trendpulse_creator") ?: "@trendpulse_creator"
    )
    val memeAuthor: StateFlow<String> = _memeAuthor.asStateFlow()

    private val _memeSticker = MutableStateFlow("🔥 10M VIEWS")
    val memeSticker: StateFlow<String> = _memeSticker.asStateFlow()

    private val _memeAccent = MutableStateFlow("#FF2E63")
    val memeAccent: StateFlow<String> = _memeAccent.asStateFlow()

    private val _memeSavedMessage = MutableStateFlow<String?>(null)
    val memeSavedMessage: StateFlow<String?> = _memeSavedMessage.asStateFlow()

    fun updateMemeTemplate(template: String) {
        _memeTemplate.value = template
    }

    fun updateMemeTopText(text: String) {
        _memeTopText.value = text
    }

    fun updateMemeBottomText(text: String) {
        _memeBottomText.value = text
    }

    fun updateMemeAuthor(author: String) {
        _memeAuthor.value = author
    }

    fun updateMemeSticker(sticker: String) {
        _memeSticker.value = sticker
    }

    fun updateMemeAccent(colorHex: String) {
        _memeAccent.value = colorHex
    }

    fun saveCurrentMeme() {
        viewModelScope.launch {
            val meme = CreatedMemeEntity(
                templateStyle = _memeTemplate.value,
                topText = _memeTopText.value,
                bottomText = _memeBottomText.value,
                authorTag = _memeAuthor.value,
                stickerBadge = _memeSticker.value,
                accentHex = _memeAccent.value
            )
            repository.saveMeme(meme)
            _memeSavedMessage.value = "Meme saved to Creator Vault!"
            delay(3000)
            _memeSavedMessage.value = null
        }
    }

    fun deleteMeme(id: Long) {
        viewModelScope.launch {
            repository.deleteMeme(id)
        }
    }
}

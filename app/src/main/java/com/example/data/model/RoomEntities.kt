package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_trends")
data class SavedTrendEntity(
    @PrimaryKey val id: String,
    val title: String,
    val platform: String,
    val viralityScore: Int,
    val velocity: String,
    val oneLiner: String,
    val audioTrack: String,
    val hashtags: String,
    val userNotes: String = "",
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "created_memes")
data class CreatedMemeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val templateStyle: String,
    val topText: String,
    val bottomText: String,
    val authorTag: String,
    val stickerBadge: String,
    val accentHex: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "trend_votes")
data class TrendVoteEntity(
    @PrimaryKey val battleId: String,
    val chosenOption: Int, // 1 or 2
    val votedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "creator_challenges")
data class ChallengeEntity(
    @PrimaryKey val id: String,
    val dayNumber: Int,
    val title: String,
    val objective: String,
    val proTip: String,
    val isCompleted: Boolean = false,
    val completedAt: Long = 0L
)

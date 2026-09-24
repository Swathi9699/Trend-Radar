package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChallengeEntity
import com.example.data.model.CreatedMemeEntity
import com.example.data.model.SavedTrendEntity
import com.example.data.model.TrendVoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrendDao {
    // Saved Trends
    @Query("SELECT * FROM saved_trends ORDER BY savedAt DESC")
    fun getAllSavedTrends(): Flow<List<SavedTrendEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTrend(trend: SavedTrendEntity)

    @Query("DELETE FROM saved_trends WHERE id = :trendId")
    suspend fun removeSavedTrend(trendId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_trends WHERE id = :trendId)")
    fun isTrendSaved(trendId: String): Flow<Boolean>

    // Created Memes
    @Query("SELECT * FROM created_memes ORDER BY createdAt DESC")
    fun getAllCreatedMemes(): Flow<List<CreatedMemeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreatedMeme(meme: CreatedMemeEntity): Long

    @Query("DELETE FROM created_memes WHERE id = :memeId")
    suspend fun deleteCreatedMeme(memeId: Long)

    // Trend Battles Votes
    @Query("SELECT * FROM trend_votes")
    fun getAllVotes(): Flow<List<TrendVoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordVote(vote: TrendVoteEntity)

    // Creator Challenges
    @Query("SELECT * FROM creator_challenges ORDER BY dayNumber ASC")
    fun getChallenges(): Flow<List<ChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun seedChallenges(challenges: List<ChallengeEntity>)

    @Update
    suspend fun updateChallenge(challenge: ChallengeEntity)
}

package com.vi17.assistant.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vi17.assistant.core.database.entity.ActivityLogEntity
import com.vi17.assistant.core.database.entity.MemoryEntity
import com.vi17.assistant.core.database.entity.MemoryType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Memory entities.
 *
 * Handles CRUD operations and vector similarity search.
 */
@Dao
interface MemoryDao {

    /**
     * Insert a new memory.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    /**
     * Update an existing memory.
     */
    @Update
    suspend fun updateMemory(memory: MemoryEntity)

    /**
     * Delete a memory by ID.
     */
    @Delete
    suspend fun deleteMemory(memory: MemoryEntity)

    /**
     * Delete memory by ID.
     */
    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemoryById(id: String)

    /**
     * Get memory by ID.
     */
    @Query("SELECT * FROM memories WHERE id = :id")
    suspend fun getMemoryById(id: String): MemoryEntity?

    /**
     * Get all memories of a specific type.
     */
    @Query("SELECT * FROM memories WHERE type = :type AND isArchived = 0 ORDER BY timestamp DESC")
    suspend fun getByType(type: MemoryType): List<MemoryEntity>

    /**
     * Get recent memories (not archived).
     */
    @Query("SELECT * FROM memories WHERE isArchived = 0 ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMemories(limit: Int = 50): List<MemoryEntity>

    /**
     * Get memories by tag.
     */
    @Query("SELECT * FROM memories WHERE tags LIKE :tag AND isArchived = 0 ORDER BY timestamp DESC")
    suspend fun getByTag(tag: String): List<MemoryEntity>

    /**
     * Get memories from a specific app.
     */
    @Query("SELECT * FROM memories WHERE sourceApp = :appPackage AND isArchived = 0 ORDER BY timestamp DESC")
    suspend fun getBySourceApp(appPackage: String): List<MemoryEntity>

    /**
     * Archive old memories with low importance.
     * Memories older than 90 days with importance < 0.3 are archived.
     */
    @Query("""
        UPDATE memories 
        SET isArchived = 1 
        WHERE timestamp < :cutoffTime AND importance < 0.3 AND isArchived = 0
    """)
    suspend fun archiveOldMemories(cutoffTime: Long)

    /**
     * Get all memories (for backup/export).
     */
    @Query("SELECT * FROM memories ORDER BY timestamp DESC")
    suspend fun getAllMemories(): List<MemoryEntity>

    /**
     * Get all memories as Flow for reactive updates.
     */
    @Query("SELECT * FROM memories WHERE isArchived = 0 ORDER BY timestamp DESC")
    fun getAllMemoriesFlow(): Flow<List<MemoryEntity>>

    /**
     * Count total memories.
     */
    @Query("SELECT COUNT(*) FROM memories WHERE isArchived = 0")
    suspend fun getMemoryCount(): Int

    /**
     * Delete all memories (for reset/logout).
     */
    @Query("DELETE FROM memories")
    suspend fun deleteAllMemories()

    /**
     * Get memories created in the last N hours.
     */
    @Query("""
        SELECT * FROM memories 
        WHERE timestamp > :sinceTime AND isArchived = 0 
        ORDER BY timestamp DESC
    """)
    suspend fun getMemoriesSince(sinceTime: Long): List<MemoryEntity>

    /**
     * Update importance score of a memory.
     */
    @Query("UPDATE memories SET importance = :importance WHERE id = :id")
    suspend fun updateImportance(id: String, importance: Float)
}

/**
 * Data Access Object for Activity Log entities.
 *
 * Handles CRUD operations for audit trail.
 */
@Dao
interface ActivityLogDao {

    /**
     * Insert a new activity log entry.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActivityLogEntity): Long

    /**
     * Update an existing log entry.
     */
    @Update
    suspend fun updateLog(log: ActivityLogEntity)

    /**
     * Delete a log entry.
     */
    @Delete
    suspend fun deleteLog(log: ActivityLogEntity)

    /**
     * Delete log by ID.
     */
    @Query("DELETE FROM activity_log WHERE id = :id")
    suspend fun deleteLogById(id: String)

    /**
     * Get log entry by ID.
     */
    @Query("SELECT * FROM activity_log WHERE id = :id")
    suspend fun getLogById(id: String): ActivityLogEntity?

    /**
     * Get all activity logs.
     */
    @Query("SELECT * FROM activity_log ORDER BY timestamp DESC")
    suspend fun getAllLogs(): List<ActivityLogEntity>

    /**
     * Get all activity logs as Flow for reactive updates.
     */
    @Query("SELECT * FROM activity_log ORDER BY timestamp DESC")
    fun getAllLogsFlow(): Flow<List<ActivityLogEntity>>

    /**
     * Get logs by action type.
     */
    @Query("SELECT * FROM activity_log WHERE action = :action ORDER BY timestamp DESC")
    suspend fun getLogsByAction(action: String): List<ActivityLogEntity>

    /**
     * Get logs from a specific app.
     */
    @Query("SELECT * FROM activity_log WHERE sourceApp = :appPackage ORDER BY timestamp DESC")
    suspend fun getLogsByApp(appPackage: String): List<ActivityLogEntity>

    /**
     * Get recent logs (last N entries).
     */
    @Query("SELECT * FROM activity_log ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentLogs(limit: Int = 100): List<ActivityLogEntity>

    /**
     * Get logs within a time range.
     */
    @Query("""
        SELECT * FROM activity_log 
        WHERE timestamp BETWEEN :startTime AND :endTime 
        ORDER BY timestamp DESC
    """)
    suspend fun getLogsBetween(startTime: Long, endTime: Long): List<ActivityLogEntity>

    /**
     * Mark log as reviewed.
     */
    @Query("UPDATE activity_log SET isReviewed = 1 WHERE id = :id")
    suspend fun markAsReviewed(id: String)

    /**
     * Count total logs.
     */
    @Query("SELECT COUNT(*) FROM activity_log")
    suspend fun getLogCount(): Int

    /**
     * Delete all logs (for reset).
     */
    @Query("DELETE FROM activity_log")
    suspend fun deleteAllLogs()

    /**
     * Delete logs older than a certain date.
     */
    @Query("DELETE FROM activity_log WHERE timestamp < :cutoffTime")
    suspend fun deleteOldLogs(cutoffTime: Long)

    /**
     * Get unreviewed logs count.
     */
    @Query("SELECT COUNT(*) FROM activity_log WHERE isReviewed = 0")
    suspend fun getUnreviewedCount(): Int
}

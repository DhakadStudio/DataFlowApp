package com.vi17.assistant.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Memory types for Vi-17.
 */
enum class MemoryType {
    EPISODIC,      // Conversation history, recent interactions
    SEMANTIC,      // Facts about user, preferences
    CONTEXTUAL     // What was on screen, app context, time/location
}

/**
 * Room entity for encrypted memory storage.
 *
 * All text content is encrypted before storage using CryptoManager.
 * Vector embeddings are stored as ByteArray for efficient similarity search.
 */
@Entity(
    tableName = "memories",
    indices = [
        Index("type"),
        Index("sourceApp"),
        Index("timestamp"),
        Index("importance"),
        Index("isArchived")
    ]
)
data class MemoryEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    // Encrypted content (plaintext encrypted with AES-256-GCM)
    val content: String,

    // MiniLM-L6 embedding as ByteArray (for vector similarity search)
    val contentVector: ByteArray,

    // Memory type: episodic, semantic, or contextual
    val type: MemoryType,

    // Source app (e.g., "com.whatsapp", "com.google.android.gms")
    val sourceApp: String? = null,

    // Timestamp in milliseconds
    val timestamp: Long = System.currentTimeMillis(),

    // Importance score 0.0-1.0 (affects retrieval priority and archival)
    val importance: Float = 0.5f,

    // JSON array of tags for categorization
    val tags: String = "[]",

    // Whether memory is archived (not retrieved in normal searches)
    val isArchived: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MemoryEntity) return false
        if (id != other.id) return false
        if (content != other.content) return false
        if (!contentVector.contentEquals(other.contentVector)) return false
        if (type != other.type) return false
        if (sourceApp != other.sourceApp) return false
        if (timestamp != other.timestamp) return false
        if (importance != other.importance) return false
        if (tags != other.tags) return false
        if (isArchived != other.isArchived) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + contentVector.contentHashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (sourceApp?.hashCode() ?: 0)
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + importance.hashCode()
        result = 31 * result + tags.hashCode()
        result = 31 * result + isArchived.hashCode()
        return result
    }
}

/**
 * Activity log entry for audit trail.
 *
 * Logs every action Vi takes:
 * - What it read from screen
 * - What it said/responded
 * - What app it accessed
 * - Permissions used
 *
 * Users can view and delete entries from Settings → Activity Log.
 */
@Entity(
    tableName = "activity_log",
    indices = [
        Index("timestamp"),
        Index("action"),
        Index("sourceApp")
    ]
)
data class ActivityLogEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    // Action type: "read_screen", "voice_response", "app_access", "permission_used"
    val action: String,

    // Description of the action
    val description: String,

    // Source app (e.g., "com.whatsapp")
    val sourceApp: String? = null,

    // Data read (encrypted)
    val dataRead: String? = null,

    // Response given (encrypted)
    val response: String? = null,

    // Timestamp in milliseconds
    val timestamp: Long = System.currentTimeMillis(),

    // Whether user has reviewed this entry
    val isReviewed: Boolean = false
)

/**
 * Type converter for ByteArray to/from database.
 */
object ByteArrayConverter {
    @androidx.room.TypeConverter
    fun fromByteArray(value: ByteArray): String {
        return value.joinToString(",") { it.toString() }
    }

    @androidx.room.TypeConverter
    fun toByteArray(value: String): ByteArray {
        return if (value.isEmpty()) {
            byteArrayOf()
        } else {
            value.split(",").map { it.toByte() }.toByteArray()
        }
    }
}

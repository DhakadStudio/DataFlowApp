package com.vi17.assistant.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vi17.assistant.core.database.converter.ByteArrayConverter
import com.vi17.assistant.core.database.dao.ActivityLogDao
import com.vi17.assistant.core.database.dao.MemoryDao
import com.vi17.assistant.core.database.entity.ActivityLogEntity
import com.vi17.assistant.core.database.entity.MemoryEntity
import com.vi17.assistant.core.security.CryptoManager
import net.zetetic.database.sqlcipher.SQLiteDatabase
import timber.log.Timber

/**
 * Room Database for Vi-17 with SQLCipher encryption.
 *
 * Stores:
 * - Memory (episodic, semantic, contextual)
 * - Activity logs
 * - User preferences
 *
 * All data is encrypted with AES-256-GCM using Android Keystore.
 */
@Database(
    entities = [
        MemoryEntity::class,
        ActivityLogEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(ByteArrayConverter::class)
abstract class ViDatabase : RoomDatabase() {

    abstract fun memoryDao(): MemoryDao
    abstract fun activityLogDao(): ActivityLogDao

    companion object {
        private const val DATABASE_NAME = "vi_database"
        private const val DATABASE_PASSPHRASE_LENGTH = 32

        @Volatile
        private var INSTANCE: ViDatabase? = null

        /**
         * Gets or creates the database instance with SQLCipher encryption.
         *
         * @param context Application context
         * @param cryptoManager CryptoManager for key derivation
         * @return ViDatabase instance
         */
        fun getInstance(
            context: Context,
            cryptoManager: CryptoManager
        ): ViDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: createDatabase(context, cryptoManager).also {
                    INSTANCE = it
                }
            }
        }

        /**
         * Creates a new database instance with SQLCipher encryption.
         */
        private fun createDatabase(
            context: Context,
            cryptoManager: CryptoManager
        ): ViDatabase {
            return try {
                // Generate a deterministic passphrase from the master key
                val passphrase = generateDatabasePassphrase(cryptoManager)

                Room.databaseBuilder(
                    context.applicationContext,
                    ViDatabase::class.java,
                    DATABASE_NAME
                )
                    .openHelperFactory { configuration ->
                        ViSQLiteOpenHelperFactory(passphrase)
                    }
                    .addCallback(ViDatabaseCallback())
                    .build()
                    .also {
                        Timber.d("Database created with SQLCipher encryption")
                    }
            } catch (e: Exception) {
                Timber.e(e, "Failed to create database")
                throw DatabaseException("Failed to initialize encrypted database", e)
            }
        }

        /**
         * Generates a deterministic database passphrase from the master key.
         * Uses a fixed encryption to ensure the same passphrase is generated each time.
         */
        private fun generateDatabasePassphrase(cryptoManager: CryptoManager): ByteArray {
            return try {
                // Use a fixed string to generate a consistent passphrase
                val passphraseString = "vi_database_passphrase_seed"
                val encrypted = cryptoManager.encrypt(passphraseString)

                // Use first 32 bytes of ciphertext as passphrase
                encrypted.ciphertext.take(DATABASE_PASSPHRASE_LENGTH).toByteArray()
            } catch (e: Exception) {
                Timber.e(e, "Failed to generate database passphrase")
                throw DatabaseException("Failed to generate database passphrase", e)
            }
        }

        /**
         * Resets the database (for testing or user request).
         */
        fun reset() {
            INSTANCE = null
        }
    }
}

/**
 * Custom SQLite Open Helper Factory for SQLCipher.
 */
class ViSQLiteOpenHelperFactory(private val passphrase: ByteArray) :
    androidx.room.support.SupportSQLiteOpenHelper.Factory {

    override fun create(
        configuration: androidx.room.support.SupportSQLiteOpenHelper.Configuration
    ): androidx.room.support.SupportSQLiteOpenHelper {
        return ViSQLiteOpenHelper(configuration, passphrase)
    }
}

/**
 * Custom SQLite Open Helper for SQLCipher.
 */
class ViSQLiteOpenHelper(
    private val configuration: androidx.room.support.SupportSQLiteOpenHelper.Configuration,
    private val passphrase: ByteArray
) : androidx.room.support.SupportSQLiteOpenHelper {

    private var delegate: net.zetetic.database.sqlcipher.SQLiteOpenHelper? = null

    override fun getDatabaseName(): String = configuration.name ?: "database"

    override fun setWriteAheadLoggingEnabled(enabled: Boolean) {
        // SQLCipher doesn't support WAL in the same way
    }

    override fun getWritableDatabase(): androidx.room.support.SupportSQLiteDatabase {
        return getDelegate().writableDatabase.let {
            ViSupportSQLiteDatabase(it)
        }
    }

    override fun getReadableDatabase(): androidx.room.support.SupportSQLiteDatabase {
        return getDelegate().readableDatabase.let {
            ViSupportSQLiteDatabase(it)
        }
    }

    override fun close() {
        delegate?.close()
    }

    private fun getDelegate(): net.zetetic.database.sqlcipher.SQLiteOpenHelper {
        return delegate ?: object : net.zetetic.database.sqlcipher.SQLiteOpenHelper(
            configuration.context,
            configuration.name,
            null,
            1
        ) {
            override fun onCreate(db: SQLiteDatabase) {
                db.execSQL("PRAGMA cipher_page_size = 4096")
                db.execSQL("PRAGMA kdf_iter = 256000")
                configuration.callback?.onCreate(ViSupportSQLiteDatabase(db))
            }

            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
                configuration.callback?.onUpgrade(ViSupportSQLiteDatabase(db), oldVersion, newVersion)
            }

            override fun onOpen(db: SQLiteDatabase) {
                db.rawExecSQL("PRAGMA key = \"x'${passphrase.toHexString()}'\"")
                configuration.callback?.onOpen(ViSupportSQLiteDatabase(db))
            }
        }.also {
            delegate = it
        }
    }
}

/**
 * Wrapper for SQLCipher SQLiteDatabase to implement SupportSQLiteDatabase.
 */
class ViSupportSQLiteDatabase(private val delegate: SQLiteDatabase) :
    androidx.room.support.SupportSQLiteDatabase {

    override fun compileStatement(sql: String): androidx.room.support.SupportSQLiteStatement {
        return ViSupportSQLiteStatement(delegate.compileStatement(sql))
    }

    override fun beginTransaction() {
        delegate.beginTransaction()
    }

    override fun endTransaction() {
        delegate.endTransaction()
    }

    override fun setTransactionSuccessful() {
        delegate.setTransactionSuccessful()
    }

    override fun inTransaction(): Boolean = delegate.inTransaction()

    override fun isReadOnly(): Boolean = delegate.isReadOnly

    override fun isOpen(): Boolean = delegate.isOpen

    override fun execSQL(sql: String) {
        delegate.execSQL(sql)
    }

    override fun execSQL(sql: String, bindArgs: Array<out Any?>) {
        delegate.execSQL(sql, bindArgs)
    }

    override fun query(sql: String): androidx.room.support.SupportSQLiteDatabase.Cursor {
        return ViSupportSQLiteCursor(delegate.rawQuery(sql, null))
    }

    override fun query(
        sql: String,
        bindArgs: Array<out Any?>
    ): androidx.room.support.SupportSQLiteDatabase.Cursor {
        return ViSupportSQLiteCursor(delegate.rawQuery(sql, bindArgs.map { it.toString() }.toTypedArray()))
    }

    override fun close() {
        delegate.close()
    }

    override fun setLocale(locale: java.util.Locale) {
        // Not supported by SQLCipher
    }

    override fun disableWriteAheadLogging() {
        // Not supported by SQLCipher
    }

    override fun enableWriteAheadLogging(): Boolean {
        return false
    }

    override fun getAttachDatabases(): MutableList<androidx.room.support.SupportSQLiteDatabase.Pair> {
        return mutableListOf()
    }

    override fun getPath(): String? = null

    override fun setForeignKeyConstraintsEnabled(enabled: Boolean) {
        delegate.setForeignKeyConstraintsEnabled(enabled)
    }

    override fun supportsBindArgumentCount(): Boolean = true
}

/**
 * Wrapper for SQLCipher Cursor.
 */
class ViSupportSQLiteCursor(private val delegate: SQLiteDatabase.Cursor) :
    androidx.room.support.SupportSQLiteDatabase.Cursor {

    override fun getCount(): Int = delegate.count
    override fun getPosition(): Int = delegate.position
    override fun move(offset: Int): Boolean = delegate.move(offset)
    override fun moveToPosition(position: Int): Boolean = delegate.moveToPosition(position)
    override fun moveToFirst(): Boolean = delegate.moveToFirst()
    override fun moveToLast(): Boolean = delegate.moveToLast()
    override fun moveToNext(): Boolean = delegate.moveToNext()
    override fun moveToPrevious(): Boolean = delegate.moveToPrevious()
    override fun isFirst(): Boolean = delegate.isFirst
    override fun isLast(): Boolean = delegate.isLast
    override fun isBeforeFirst(): Boolean = delegate.isBeforeFirst
    override fun isAfterLast(): Boolean = delegate.isAfterLast
    override fun getColumnIndex(columnName: String): Int = delegate.getColumnIndex(columnName)
    override fun getColumnIndexOrThrow(columnName: String): Int = delegate.getColumnIndexOrThrow(columnName)
    override fun getColumnName(columnIndex: Int): String = delegate.getColumnName(columnIndex)
    override fun getColumnNames(): Array<String> = delegate.columnNames
    override fun getColumnCount(): Int = delegate.columnCount
    override fun getBlob(columnIndex: Int): ByteArray = delegate.getBlob(columnIndex)
    override fun getString(columnIndex: Int): String = delegate.getString(columnIndex)
    override fun getShort(columnIndex: Int): Short = delegate.getShort(columnIndex)
    override fun getInt(columnIndex: Int): Int = delegate.getInt(columnIndex)
    override fun getLong(columnIndex: Int): Long = delegate.getLong(columnIndex)
    override fun getFloat(columnIndex: Int): Float = delegate.getFloat(columnIndex)
    override fun getDouble(columnIndex: Int): Double = delegate.getDouble(columnIndex)
    override fun getType(columnIndex: Int): Int = delegate.getType(columnIndex)
    override fun isNull(columnIndex: Int): Boolean = delegate.isNull(columnIndex)
    override fun close() = delegate.close()
    override fun isClosed(): Boolean = delegate.isClosed
}

/**
 * Wrapper for SQLCipher Statement.
 */
class ViSupportSQLiteStatement(private val delegate: SQLiteDatabase.Statement) :
    androidx.room.support.SupportSQLiteStatement {

    override fun execute() = delegate.execute()
    override fun executeUpdateDelete(): Int = delegate.executeUpdateDelete()
    override fun executeInsert(): Long = delegate.executeInsert()
    override fun simpleQueryForLong(): Long = delegate.simpleQueryForLong()
    override fun simpleQueryForString(): String = delegate.simpleQueryForString()
    override fun bindNull(index: Int) = delegate.bindNull(index)
    override fun bindLong(index: Int, value: Long) = delegate.bindLong(index, value)
    override fun bindDouble(index: Int, value: Double) = delegate.bindDouble(index, value)
    override fun bindString(index: Int, value: String) = delegate.bindString(index, value)
    override fun bindBlob(index: Int, value: ByteArray) = delegate.bindBlob(index, value)
    override fun clearBindings() = delegate.clearBindings()
    override fun close() = delegate.close()
}

/**
 * Extension to convert ByteArray to hex string.
 */
private fun ByteArray.toHexString(): String {
    return joinToString("") { "%02x".format(it) }
}

/**
 * Database callback for initialization.
 */
class ViDatabaseCallback : RoomDatabase.Callback() {
    override fun onCreate(db: androidx.room.support.SupportSQLiteDatabase) {
        super.onCreate(db)
        Timber.d("Database created")
    }

    override fun onOpen(db: androidx.room.support.SupportSQLiteDatabase) {
        super.onOpen(db)
        Timber.d("Database opened")
    }
}

/**
 * Exception thrown when database operations fail.
 */
class DatabaseException(message: String, cause: Throwable? = null) :
    Exception(message, cause)

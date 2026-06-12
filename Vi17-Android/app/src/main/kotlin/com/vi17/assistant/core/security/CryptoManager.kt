package com.vi17.assistant.core.security

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.io.IOException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import timber.log.Timber

/**
 * Manages encryption/decryption using Android Keystore with AES-256-GCM.
 * All sensitive data (memory, preferences, etc.) is encrypted using this manager.
 *
 * Features:
 * - Hardware-backed encryption (if available)
 * - AES-256-GCM (authenticated encryption)
 * - Automatic key generation and management
 * - Secure random IV generation
 */
class CryptoManager {

    private val keyStore: KeyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
    private val cipher: Cipher = Cipher.getInstance(CIPHER_ALGORITHM)

    init {
        keyStore.load(null)
        ensureKeyExists()
    }

    /**
     * Encrypts plaintext using AES-256-GCM.
     *
     * @param plaintext The data to encrypt
     * @return EncryptedData containing ciphertext and IV
     */
    fun encrypt(plaintext: String): EncryptedData {
        return try {
            val key = getOrCreateKey()
            cipher.init(Cipher.ENCRYPT_MODE, key)

            val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
            val iv = cipher.iv

            EncryptedData(
                ciphertext = ciphertext,
                iv = iv
            )
        } catch (e: Exception) {
            Timber.e(e, "Encryption failed")
            throw EncryptionException("Failed to encrypt data", e)
        }
    }

    /**
     * Decrypts ciphertext using AES-256-GCM.
     *
     * @param encryptedData The encrypted data with IV
     * @return Decrypted plaintext string
     */
    fun decrypt(encryptedData: EncryptedData): String {
        return try {
            val key = getOrCreateKey()
            val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, encryptedData.iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)

            val plaintext = cipher.doFinal(encryptedData.ciphertext)
            String(plaintext, Charsets.UTF_8)
        } catch (e: Exception) {
            Timber.e(e, "Decryption failed")
            throw EncryptionException("Failed to decrypt data", e)
        }
    }

    /**
     * Generates a new encryption key or retrieves existing one.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun getOrCreateKey(): SecretKey {
        val existingKey = keyStore.getKey(KEY_ALIAS, null)
        return if (existingKey != null) {
            existingKey as SecretKey
        } else {
            generateKey()
        }
    }

    /**
     * Generates a new AES-256 key in Android Keystore.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun generateKey(): SecretKey {
        return try {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_PROVIDER
            )

            val keySpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).apply {
                setKeySize(KEY_SIZE_BITS)
                setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                setRandomizedEncryptionRequired(true)

                // Hardware-backed if available
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    setIsStrongBoxBacked(true)
                }
            }.build()

            keyGenerator.init(keySpec)
            keyGenerator.generateKey()
        } catch (e: Exception) {
            Timber.e(e, "Key generation failed")
            throw EncryptionException("Failed to generate encryption key", e)
        }
    }

    /**
     * Ensures encryption key exists, creates if not.
     */
    private fun ensureKeyExists() {
        try {
            if (keyStore.getKey(KEY_ALIAS, null) == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    generateKey()
                    Timber.d("Encryption key created")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to ensure key exists")
        }
    }

    /**
     * Data class for encrypted data with IV.
     */
    data class EncryptedData(
        val ciphertext: ByteArray,
        val iv: ByteArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is EncryptedData) return false
            if (!ciphertext.contentEquals(other.ciphertext)) return false
            if (!iv.contentEquals(other.iv)) return false
            return true
        }

        override fun hashCode(): Int {
            var result = ciphertext.contentHashCode()
            result = 31 * result + iv.contentHashCode()
            return result
        }
    }

    /**
     * Exception thrown when encryption/decryption fails.
     */
    class EncryptionException(message: String, cause: Throwable? = null) :
        Exception(message, cause)

    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "vi_master_key"
        private const val CIPHER_ALGORITHM = "AES/GCM/NoPadding"
        private const val KEY_SIZE_BITS = 256
        private const val GCM_TAG_LENGTH_BITS = 128
    }
}

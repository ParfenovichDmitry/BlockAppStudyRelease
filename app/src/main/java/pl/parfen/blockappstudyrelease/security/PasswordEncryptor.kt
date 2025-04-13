package pl.parfen.blockappstudyrelease.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object PasswordEncryptor {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val AES_KEY_ALIAS = "BlockAppKey"
    private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    private const val IV_SIZE = 16

    fun createSecretKeyIfNeeded() {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        if (keyStore.getKey(AES_KEY_ALIAS, null) == null) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                AES_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        val existingKey = keyStore.getKey(AES_KEY_ALIAS, null)
            ?: throw IllegalStateException("Error getting secret key")

        return existingKey as SecretKey
    }

    fun encrypt(password: String): String? {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(password.toByteArray(StandardCharsets.UTF_8))

            val combined = iv + encryptedBytes
            Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (e: Exception) {

            null
        }
    }

    fun decrypt(encryptedData: String): String? {
        return try {
            val combined = Base64.decode(encryptedData, Base64.DEFAULT)

            val iv = combined.copyOfRange(0, IV_SIZE)
            val encryptedBytes = combined.copyOfRange(IV_SIZE, combined.size)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), IvParameterSpec(iv))

            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {

            null
        }
    }
}

package pl.parfen.blockappstudyrelease.data

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object PasswordRepository {
    private const val PREFS_NAME = "secure_user_prefs"
    private const val ENCRYPTED_PASSWORD_KEY = "encrypted_password"
    private const val SECRET_QUESTION_KEY = "secret_question"
    private const val SECRET_ANSWER_KEY = "secret_answer"

    private fun getEncryptedPrefs(context: Context) =
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )


    fun saveEncryptedPassword(context: Context, encryptedPassword: String) {
        getEncryptedPrefs(context).edit {
            putString(ENCRYPTED_PASSWORD_KEY, encryptedPassword)
        }
    }


    fun getEncryptedPassword(context: Context): String? {
        return getEncryptedPrefs(context).getString(ENCRYPTED_PASSWORD_KEY, null)
    }


    fun saveSecretQuestion(context: Context, question: String) {
        getEncryptedPrefs(context).edit {
            putString(SECRET_QUESTION_KEY, question)
        }
    }


    fun getSecretQuestion(context: Context): String? {
        return getEncryptedPrefs(context).getString(SECRET_QUESTION_KEY, null)
    }

    fun saveSecretAnswer(context: Context, answer: String) {
        getEncryptedPrefs(context).edit {
            putString(SECRET_ANSWER_KEY, answer)
        }
    }

    fun getSecretAnswer(context: Context): String? {
        return getEncryptedPrefs(context).getString(SECRET_ANSWER_KEY, null)
    }
}

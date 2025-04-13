package pl.parfen.blockappstudyrelease.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class ProfilePreferences(context: Context, profileId: Int) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "CreateProfilePrefs_$profileId", Context.MODE_PRIVATE
    )

    var aiNetwork: String
        get() = prefs.getString(KEY_AI_NETWORK, "ChatGPT") ?: "ChatGPT"
        set(value) = prefs.edit { putString(KEY_AI_NETWORK, value) }

    var aiTopics: List<String>
        get() = prefs.getStringSet(KEY_AI_TOPICS, emptySet())?.toList() ?: emptyList()
        set(value) = prefs.edit { putStringSet(KEY_AI_TOPICS, value.toSet()) }

    var aiLanguage: String
        get() = prefs.getString(KEY_AI_LANGUAGE, "English") ?: "English"
        set(value) = prefs.edit { putString(KEY_AI_LANGUAGE, value) }

    var additionalLanguage: String?
        get() = prefs.getString(KEY_ADDITIONAL_LANGUAGE, null)
        set(value) = prefs.edit { putString(KEY_ADDITIONAL_LANGUAGE, value) }

    var selectedTopics: List<String>
        get() = prefs.getStringSet(KEY_SELECTED_TOPICS, emptySet())?.toList() ?: emptyList()
        set(value) = prefs.edit { putStringSet(KEY_SELECTED_TOPICS, value.toSet()) }

    fun clear() {
        prefs.edit {
            remove(KEY_AI_NETWORK)
            remove(KEY_AI_TOPICS)
            remove(KEY_AI_LANGUAGE)
            remove(KEY_ADDITIONAL_LANGUAGE)
            remove(KEY_SELECTED_TOPICS)
        }
    }

    companion object {
        private const val KEY_AI_NETWORK = "aiNetwork"
        private const val KEY_AI_TOPICS = "aiTopics"
        private const val KEY_AI_LANGUAGE = "aiLanguage"
        private const val KEY_ADDITIONAL_LANGUAGE = "additionalLanguage"
        private const val KEY_SELECTED_TOPICS = "selectedTopics"
    }
}

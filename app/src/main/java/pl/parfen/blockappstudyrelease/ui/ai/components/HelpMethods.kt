package pl.parfen.blockappstudyrelease.util

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import pl.parfen.blockappstudyrelease.R
import java.util.Calendar
import java.util.Locale

object HelpMethods {

    fun getSystemLanguage(): String {
        return Locale.getDefault().language
    }

    fun updateContextLocale(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun getWordCountRangeForAge(age: String): Pair<Int, Int> {
        val ageInt = age.toIntOrNull() ?: 6
        return when (ageInt.coerceIn(6, 15)) {
            6 -> 50 to 80
            7 -> 80 to 120
            8 -> 120 to 180
            9 -> 180 to 250
            10 -> 250 to 350
            11 -> 350 to 450
            12 -> 450 to 600
            13 -> 600 to 750
            14 -> 750 to 850
            15 -> 850 to 1000
            else -> 100 to 200
        }
    }

    private fun normalizeTopic(topic: String): String {
        return topic.trim().lowercase().replace("\\s+".toRegex(), " ")
    }

    private fun createTopicMapping(
        context: Context,
        languageCodes: List<String>
    ): Map<String, String> {
        val mapping = mutableMapOf<String, String>()
        val englishTopics = context.resources.getStringArray(R.array.default_topics_en).toList()
        languageCodes.forEach { langCode ->
            val locale = Locale(langCode)
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            val localizedContext = context.createConfigurationContext(config)
            val localizedTopics = localizedContext.resources.getStringArray(R.array.default_topics).toList()
            localizedTopics.forEachIndexed { index, localizedTopic ->
                mapping[normalizeTopic(localizedTopic)] = englishTopics[index]
            }
        }
        return mapping
    }

    fun getTopicsForLanguage(context: Context, langCode: String): List<String> {
        val config = Configuration(context.resources.configuration)
        config.setLocale(Locale(langCode))
        val localizedContext = context.createConfigurationContext(config)
        return localizedContext.resources.getStringArray(R.array.default_topics).toList()
    }

    fun mapTopicToEnglish(context: Context, topic: String, languageCodes: List<String>): String {
        val mapping = createTopicMapping(context, languageCodes)
        val normalizedTopic = normalizeTopic(topic)
        return mapping[normalizedTopic] ?: topic
    }

    fun createPrompt(
        context: Context,
        age: String,
        topics: List<String>,
        languageCode: String,
        languageCodes: List<String>,
        breakIntoSyllables: Boolean = false
    ): String {
        val (minWords, maxWords) = getWordCountRangeForAge(age)
        val ageInt = age.toIntOrNull() ?: 6
        val topic = topics.randomOrNull() ?: "Edukacja"

        val languageName = when (languageCode.lowercase()) {
            "pl" -> "Polish"
            "en" -> "English"
            "ru" -> "Russian"
            "de" -> "German"
            "fr" -> "French"
            else -> "English"
        }

        val exampleStart = when (languageCode.lowercase()) {
            "pl" -> "np. \"Dawno, dawno temu...\""
            "en" -> "e.g. \"Once upon a time...\""
            "ru" -> "например \"Жили-были...\""
            "de" -> "z.B. \"Es war einmal...\""
            "fr" -> "ex. \"Il était une fois...\""
            else -> "e.g. \"Once upon a time...\""
        }

        val syllableInstruction = if (breakIntoSyllables) {
            """
            Separate every word into syllables using hyphens (e.g., "ku-ba", "ma-gicz-na").
            Return ONLY the syllable-separated version of the story.
            Do NOT include the original version without syllables.
            """.trimIndent()
        } else ""

        return if (ageInt <= 8) {
            """
            Write a fairy-tale style educational story in $languageName on the topic \"$topic\".
            The story must include real and age-appropriate factual information, but be told in a magical or imaginative way suitable for a $ageInt-year-old child.
            Start with $exampleStart
            Use simple vocabulary, friendly tone, and characters (like animals, children, or magical guides) to introduce real facts.
            The story must contain between $minWords and $maxWords words.
            $syllableInstruction
            Avoid violence, fear, adult content. Make it safe, fun, and informative.
            """.trimIndent()
        } else {
            """
            Write an informative and engaging text in $languageName on the topic \"$topic\".
            Present real, age-appropriate facts in a way that sparks curiosity and imagination in a $ageInt-year-old child.
            Use clear and simple language, examples, and comparisons suitable for this age group.
            The text should contain between $minWords and $maxWords words.
            $syllableInstruction
            Avoid complex terminology, technical jargon, or any content that may be inappropriate.
            Encourage learning by making the information feel exciting and relevant to the child's world.
            """.trimIndent()
        }
    }

    fun calculateOneMonthLater(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        return java.text.SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(calendar.time)
    }
}

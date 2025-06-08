package pl.parfen.blockappstudyrelease.utils.blockapp

object ComplianceCalculator {

    /**
     * Считает процент совпадения между произнесённым и оригинальным текстом.
     * Используется Левенштейн + пофразное сравнение.
     */
    fun calculateCompliance(spokenText: String, originalText: String): Float {
        if (originalText.isBlank() || spokenText.isBlank()) return 0f

        val cleanedSpoken = cleanText(spokenText)
        val cleanedOriginal = cleanText(originalText)

        if (cleanedSpoken == cleanedOriginal) return 100f

        val spokenWords = cleanedSpoken.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val originalWords = cleanedOriginal.split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (originalWords.isEmpty()) return 0f

        var matchingWordsCount = 0
        val usedSpokenIndices = mutableSetOf<Int>()

        for (i in originalWords.indices) {
            val originalWord = originalWords[i]
            var bestMatchIndex = -1
            var bestMatchDistance = Int.MAX_VALUE

            for (j in spokenWords.indices) {
                if (j in usedSpokenIndices) continue
                val spokenWord = spokenWords[j]
                val distance = levenshteinDistance(originalWord, spokenWord)
                if (distance <= 2 && distance < bestMatchDistance) {
                    bestMatchDistance = distance
                    bestMatchIndex = j
                }
            }
            if (bestMatchIndex != -1) {
                matchingWordsCount++
                usedSpokenIndices.add(bestMatchIndex)
            }
        }

        return (matchingWordsCount.toFloat() / originalWords.size) * 100f
    }

    /**
     * Очищает текст для сравнения (убирает спецсимволы, приводит к нижнему регистру)
     */
    private fun cleanText(text: String): String {
        return text.lowercase()
            .trim()
            .replace("[^a-zа-яёąćęłńóśźżüöäüß\\s-]".toRegex(), "")
    }

    /**
     * Левенштейн (расстояние редактирования между двумя словами)
     */
    fun levenshteinDistance(s1: String, s2: String): Int {
        if (s1.isEmpty()) return s2.length
        if (s2.isEmpty()) return s1.length

        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                dp[i][j] = if (s1[i - 1] == s2[j - 1]) {
                    dp[i - 1][j - 1]
                } else {
                    1 + minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
                }
            }
        }
        return dp[s1.length][s2.length]
    }
}

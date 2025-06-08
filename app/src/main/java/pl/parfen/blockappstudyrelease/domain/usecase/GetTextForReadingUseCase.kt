package pl.parfen.blockappstudyrelease.domain.usecase

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import pl.parfen.blockappstudyrelease.data.model.Profile
import pl.parfen.blockappstudyrelease.data.repository.BookRepositoryImpl
import pl.parfen.blockappstudyrelease.data.repository.blockapp.AiTextRepositoryImpl
import pl.parfen.blockappstudyrelease.data.repository.blockapp.ProfileRepository
import pl.parfen.blockappstudyrelease.domain.GetNextBookUseCase
import pl.parfen.blockappstudyrelease.util.HelpMethods
import java.util.*
import kotlin.coroutines.resume

class GetTextForReadingUseCase(
    private val bookRepository: BookRepositoryImpl,
    private val profileRepository: ProfileRepository,
    private val aiTextRepository: AiTextRepositoryImpl,
    private val getNextBookUseCase: GetNextBookUseCase,
    private val checkSubscriptionUseCase: CheckSubscriptionUseCase
) {
    suspend operator fun invoke(
        context: Context,
        profileId: Int,
        selectedBookTitle: String? = null
    ): String = withContext(Dispatchers.IO) {
        val profile = profileRepository.getProfileById(profileId)
            ?: return@withContext "Ошибка: профиль не найден"

        val profileLang = profile.profileLanguage.ifBlank { Locale.getDefault().language }

        Log.d("GetText", "PROFILE: $profile")

        // Проверка: используем ли AI и есть ли подписка
        val useAI = profile.selectedResource == "ai" && checkSubscriptionUseCase()
        if (useAI) {
            val langCode = profileLang.lowercase()
            val allTopics = HelpMethods.getTopicsForLanguage(context, langCode)
            val topic = if (profile.aiTopics.isNotEmpty()) {
                profile.aiTopics.random()
            } else {
                allTopics.random()
            }

            val breakIntoSyllables = profile.age <= 8
            val prompt = HelpMethods.createPrompt(
                context = context,
                age = profile.age.toString(),
                topics = listOf(topic),
                languageCode = langCode,
                languageCodes = listOf(profileLang.lowercase()),
                breakIntoSyllables = breakIntoSyllables
            )

            return@withContext suspendCancellableCoroutine { cont ->
                aiTextRepository.getText(prompt) { aiResult ->
                    cont.resume(aiResult)
                }
            }
        }

        // Если источник - книга
        if (profile.selectedResource == "book") {
            val allBooks = bookRepository.getSystemBooks(profileId) + bookRepository.getUserBooks(profileId)
            if (allBooks.isEmpty()) return@withContext "Нет доступных книг"

            val actualTitle = selectedBookTitle
                ?: profile.activeBook.takeIf { it.isNotBlank() }
                ?: allBooks.first().title.also {
                    profileRepository.updateProfile(profile.copy(activeBook = it))
                }

            Log.d("GetText", "Читаем книгу: $actualTitle")
            val book = allBooks.find { it.title.equals(actualTitle, ignoreCase = true) }
                ?: return@withContext "Книга не найдена"

            val linesToRead = 15
            val totalLines = bookRepository.countBookLines(book)
            val startLine = ((book.progress / 100f) * totalLines).toInt().coerceAtMost(totalLines - 1)
            val textLines = bookRepository.loadBookLines(book, startLine, linesToRead)

            return@withContext when {
                textLines.isNotEmpty() -> textLines.joinToString("\n")
                else -> {
                    val next = getNextBookUseCase.getNextBook(book, profileId, book)
                        ?: return@withContext "Нет следующей книги"
                    bookRepository.loadBookLines(next, 0, linesToRead).joinToString("\n")
                }
            }
        }

        return@withContext "Не выбран источник текста"
    }
}

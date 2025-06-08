package pl.parfen.blockappstudyrelease.data.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

class SpeechRecognizerManager {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    fun startListening(
        context: Context,
        languageCode: String,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rms: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListening = false
            }
            override fun onError(error: Int) {
                isListening = false
                onError(errorToString(error))
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.firstOrNull()?.let { onResult(it) }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        try {
            speechRecognizer?.startListening(intent)
            isListening = true
        } catch (e: Exception) {
            Log.e("SpeechRecognizerManager", "Ошибка запуска: ${e.message}", e)
            isListening = false
            onError("Ошибка запуска: ${e.message}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        isListening = false
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        isListening = false
    }

    private fun errorToString(error: Int): String {
        return when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Ошибка аудио"
            SpeechRecognizer.ERROR_CLIENT -> "Ошибка клиента"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Нет разрешения"
            SpeechRecognizer.ERROR_NETWORK -> "Ошибка сети"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Тайм-аут сети"
            SpeechRecognizer.ERROR_NO_MATCH -> "Нет совпадений"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Распознаватель занят"
            SpeechRecognizer.ERROR_SERVER -> "Ошибка сервера"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Тайм-аут речи"
            else -> "Неизвестная ошибка: $error"
        }
    }
}

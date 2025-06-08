package pl.parfen.blockappstudyrelease.utils.blockapp

import android.content.Context
import pl.parfen.blockappstudyrelease.data.speech.SpeechRecognizerManager

class SpeechRecognitionUseCase(
    private val speechRecognizerManager: SpeechRecognizerManager
) {
    fun startRecognition(
        context: Context,
        language: String,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        speechRecognizerManager.startListening(context, language, onResult, onError)
    }

    fun stopRecognition() {
        speechRecognizerManager.stopListening()
    }

    fun destroy() {
        speechRecognizerManager.destroy()
    }
}

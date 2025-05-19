package pl.parfen.blockappstudyrelease.data.model.ai

import android.os.Handler
import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.parfen.blockappstudyrelease.BuildConfig
import pl.parfen.blockappstudyrelease.data.remote.ChatGPTService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class ChatGPTManager(
    private val handler: Handler
) {
    companion object {
        private const val TAG = "ChatGPTManager"
        private const val BASE_URL = "https://api.openai.com/"
    }

    private val apiKey: String = BuildConfig.CHAT_GPT_API_KEY
    private val chatGPTService: ChatGPTService
    private var readTimeoutMillis: Long = 30000
    private var connectTimeoutMillis: Long = 30000
    private val isRequestInProgress = AtomicBoolean(false)
    private var retryCount = 0
    private val maxRetries = 3
    private var retryDelayMillis: Long = 2000

    init {
        val gson = GsonBuilder().create()
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .readTimeout(readTimeoutMillis, TimeUnit.MILLISECONDS)
            .connectTimeout(connectTimeoutMillis, TimeUnit.MILLISECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        chatGPTService = retrofit.create(ChatGPTService::class.java)
    }

    fun getChatGPTResponse(
        prompt: String,
        model: String,
        store: Boolean,
        callback: (String) -> Unit
    ) {
        if (isRequestInProgress.get()) return
        isRequestInProgress.set(true)

        val messages = listOf(Message("user", prompt))
        val request = ChatGPTRequest(messages, model, store)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = chatGPTService.createCompletion("Bearer $apiKey", request)
                isRequestInProgress.set(false)
                if (response.isSuccessful && response.body() != null) {
                    val content = response.body()?.choices?.firstOrNull()?.message?.content ?: "Пустой ответ"
                    callback(content)
                    retryCount = 0
                } else if (response.code() == 429 && retryCount < maxRetries) {
                    retryWithBackoff(prompt, model, store, callback)
                } else {
                    Log.e(TAG, "Ошибка ${response.code()}: ${response.errorBody()?.string()}")
                    callback("Ошибка запроса: ${response.code()}")
                    retryCount = 0
                }
            } catch (e: Exception) {
                isRequestInProgress.set(false)
                Log.e(TAG, "Ошибка: ${e.message}", e)
                if (retryCount < maxRetries) {
                    retryWithBackoff(prompt, model, store, callback)
                } else {
                    callback("Ошибка подключения: ${e.message}")
                    retryCount = 0
                }
            }
        }
    }

    private fun retryWithBackoff(
        prompt: String,
        model: String,
        store: Boolean,
        callback: (String) -> Unit
    ) {
        val delay = retryDelayMillis * Math.pow(2.0, retryCount.toDouble()).toLong()
        retryCount++
        Log.w(TAG, "Повтор запроса #$retryCount через ${delay}мс")
        handler.postDelayed({ getChatGPTResponse(prompt, model, store, callback) }, delay)
    }
}

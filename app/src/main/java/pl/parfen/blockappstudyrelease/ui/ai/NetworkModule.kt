package pl.parfen.blockappstudyrelease.ui.ai

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.parfen.blockappstudyrelease.data.remote.ChatGPTRepository
import pl.parfen.blockappstudyrelease.data.remote.ChatGPTService
import pl.parfen.blockappstudyrelease.data.remote.GetChatGPTResponseUseCase
import pl.parfen.blockappstudyrelease.data.repository.ChatGPTRepositoryImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private const val BASE_URL = "https://api.openai.com/"

    fun provideChatGPTService(): ChatGPTService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatGPTService::class.java)
    }

    fun provideChatGPTRepository(apiKey: String): ChatGPTRepository {
        return ChatGPTRepositoryImpl(provideChatGPTService(), apiKey)
    }

    fun provideGetChatGPTResponseUseCase(apiKey: String): GetChatGPTResponseUseCase {
        return GetChatGPTResponseUseCase(provideChatGPTRepository(apiKey))
    }
}

package pl.parfen.blockappstudyrelease.data.model.ai

import com.google.gson.annotations.SerializedName


data class ChatGPTResponse(
    @SerializedName("id") val id: String,
    @SerializedName("object") val objectName: String,
    @SerializedName("created") val created: Long,
    @SerializedName("model") val model: String,
    @SerializedName("choices") val choices: List<Choice>,
    @SerializedName("usage") val usage: Usage
) {
    data class Usage(
        @SerializedName("prompt_tokens") val promptTokens: Int,
        @SerializedName("completion_tokens") val completionTokens: Int,
        @SerializedName("total_tokens") val totalTokens: Int
    )
}

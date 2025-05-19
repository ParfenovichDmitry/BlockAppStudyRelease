package pl.parfen.blockappstudyrelease.data.model.ai

import com.google.gson.annotations.SerializedName

data class ChatGPTRequest(
    @SerializedName("messages") val messages: List<Message>,
    @SerializedName("model") val model: String,
    @SerializedName("store") val store: Boolean
)

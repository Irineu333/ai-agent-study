package com.neoutils.agent.feature.chat.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatOutput(
    val model: String,
    val message: ChatOutputMessage = ChatOutputMessage(),
    val done: Boolean = false,
)

@Serializable
data class ChatOutputMessage(
    val role: String = "",
    val content: String = "",
    val thinking: String = "",
    @SerialName("tool_calls") val toolCalls: List<ChatToolCall>? = null,
)

@Serializable
data class ChatToolCall(
    val function: ChatToolCallFunction,
)

@Serializable
data class ChatToolCallFunction(
    val name: String,
    val arguments: Map<String, String> = emptyMap(),
)

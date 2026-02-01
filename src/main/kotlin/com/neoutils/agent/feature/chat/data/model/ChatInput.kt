package com.neoutils.agent.feature.chat.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class ChatInput(
    val model: String,
    val messages: List<ChatInputMessage>,
    val stream: Boolean = true,
    val tools: List<ChatTool>? = null,
)

@Serializable
data class ChatInputMessage(
    val role: String,
    val content: String = "",
    @SerialName("tool_calls") val toolCalls: List<ChatToolCall>? = null,
)

@Serializable
data class ChatTool(
    val type: String = "function",
    val function: ChatToolFunction,
)

@Serializable
data class ChatToolFunction(
    val name: String,
    val description: String,
    val parameters: ChatToolParameters,
)

@Serializable
data class ChatToolParameters(
    val type: String = "object",
    val properties: Map<String, ChatToolProperty>,
    val required: List<String> = emptyList(),
)

@Serializable
data class ChatToolProperty(
    val type: String,
    val description: String,
)

fun Map<String, Any>.toJsonObject(): JsonObject {
    return JsonObject(
        mapValues { (_, value) -> JsonPrimitive(value.toString()) }
    )
}

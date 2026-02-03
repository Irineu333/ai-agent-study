package com.neoutils.agent.feature.chat.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class ChatOutput(
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
    val arguments: JsonObject = JsonObject(emptyMap()),
) {
    fun argumentsAsMap(): Map<String, Any> {
        return arguments.mapValues { (_, value) ->
            when (value) {
                is JsonPrimitive -> value.content
                is JsonArray -> value.map { it.jsonPrimitive.content }
                is JsonObject -> value.toString()
            }
        }
    }
}

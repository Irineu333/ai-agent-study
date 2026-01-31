package com.neoutils.agent.feature.chat.data.model

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
)

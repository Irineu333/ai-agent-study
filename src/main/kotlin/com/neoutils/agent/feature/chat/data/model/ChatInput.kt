package com.neoutils.agent.feature.chat.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatInput(
    val model: String,
    val messages: List<ChatInputMessage>,
    val stream: Boolean = true,
)

@Serializable
data class ChatInputMessage(
    val role: String,
    val content: String,
)

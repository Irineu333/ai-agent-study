package com.neoutils.agent.feature.chat.domain.model

data class ChatMessage(
    val role: Role,
    val content: String,
) {
    enum class Role { System, User, Assistant }
}

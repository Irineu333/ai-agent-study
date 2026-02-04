package com.neoutils.agent.feature.chat.domain.model

import com.neoutils.agent.core.domain.model.ToolCall

data class ChatMessage(
    val role: Role,
    val content: String = "",
    val toolCalls: List<ToolCall>? = null,
) {
    enum class Role { System, User, Assistant, Tool }
}

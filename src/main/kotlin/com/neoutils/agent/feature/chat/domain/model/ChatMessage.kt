package com.neoutils.agent.feature.chat.domain.model

data class ChatMessage(
    val role: Role,
    val content: String = "",
    val toolCalls: List<ToolCallInfo>? = null,
) {
    enum class Role { System, User, Assistant, Tool }
}

data class ToolCallInfo(
    val name: String,
    val arguments: Map<String, Any>
)

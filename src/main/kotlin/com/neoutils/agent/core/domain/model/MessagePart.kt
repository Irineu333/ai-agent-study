package com.neoutils.agent.core.domain.model

sealed class MessagePart {
    data class Thinking(
        val content: String
    ) : MessagePart()

    data class Response(
        val content: String
    ) : MessagePart()

    data class Tooling(
        val name: String,
        val arguments: Map<String, Any>,
    ) : MessagePart()
}

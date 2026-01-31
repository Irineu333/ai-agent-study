package com.neoutils.agent.domain.model

sealed class MessagePart {
    data class Thinking(val content: String) : MessagePart()
    data class Response(val content: String) : MessagePart()
}

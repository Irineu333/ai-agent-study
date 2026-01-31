package com.neoutils.agent.domain.model

sealed class ChatMessagePart {
    data class Thinking(val content: String) : ChatMessagePart()
    data class Response(val content: String) : ChatMessagePart()
}

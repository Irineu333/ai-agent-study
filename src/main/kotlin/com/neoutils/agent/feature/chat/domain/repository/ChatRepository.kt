package com.neoutils.agent.feature.chat.domain.repository

import com.neoutils.agent.domain.model.MessagePart
import com.neoutils.agent.domain.model.ToolAction
import com.neoutils.agent.feature.chat.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun chat(
        messages: List<ChatMessage>, model: String,
        tools: List<ToolAction> = emptyList()
    ): Flow<MessagePart>
}

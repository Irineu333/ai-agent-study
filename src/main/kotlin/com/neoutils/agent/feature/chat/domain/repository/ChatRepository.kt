package com.neoutils.agent.feature.chat.domain.repository

import com.neoutils.agent.core.domain.model.MessagePart
import com.neoutils.agent.core.domain.tool.ToolDefinition
import com.neoutils.agent.feature.chat.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun chat(
        messages: List<ChatMessage>, model: String,
        toolDefinitions: List<ToolDefinition> = emptyList()
    ): Flow<MessagePart>
}

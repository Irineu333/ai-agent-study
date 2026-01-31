package com.neoutils.agent.domain.repository

import com.neoutils.agent.domain.model.ChatMessagePart
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun generate(prompt: String, model: String): Flow<ChatMessagePart>
}

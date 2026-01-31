package com.neoutils.agent.data.repository

import com.neoutils.agent.data.client.OllamaClient
import com.neoutils.agent.domain.model.ChatMessagePart
import com.neoutils.agent.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion

class ChatRepositoryImpl(
    private val client: OllamaClient
) : ChatRepository {

    override fun generate(prompt: String, model: String): Flow<ChatMessagePart> {
        return client
            .generate(prompt, model)
            .mapNotNull { output ->
                when {
                    output.thinking.isNotEmpty() -> ChatMessagePart.Thinking(output.thinking)
                    output.response.isNotEmpty() -> ChatMessagePart.Response(output.response)
                    else -> null
                }
            }
            .onCompletion { client.close() }
    }
}

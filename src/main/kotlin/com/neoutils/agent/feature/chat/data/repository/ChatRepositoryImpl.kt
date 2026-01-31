package com.neoutils.agent.feature.chat.data.repository

import com.neoutils.agent.data.client.OllamaClient
import com.neoutils.agent.domain.model.MessagePart
import com.neoutils.agent.feature.chat.data.model.ChatInputMessage
import com.neoutils.agent.feature.chat.domain.model.ChatMessage
import com.neoutils.agent.feature.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class ChatRepositoryImpl(
    private val client: OllamaClient
) : ChatRepository {

    override fun chat(
        messages: List<ChatMessage>,
        model: String
    ): Flow<MessagePart> {
        val inputMessages = messages.map { msg ->
            ChatInputMessage(
                role = when (msg.role) {
                    ChatMessage.Role.System -> "system"
                    ChatMessage.Role.User -> "user"
                    ChatMessage.Role.Assistant -> "assistant"
                },
                content = msg.content,
            )
        }

        return client
            .chat(model, inputMessages)
            .mapNotNull { output ->
                when {
                    output.message.thinking.isNotEmpty() -> MessagePart.Thinking(output.message.thinking)
                    output.message.content.isNotEmpty() -> MessagePart.Response(output.message.content)
                    else -> null
                }
            }
    }
}

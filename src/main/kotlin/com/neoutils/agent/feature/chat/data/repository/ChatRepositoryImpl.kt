package com.neoutils.agent.feature.chat.data.repository

import com.neoutils.agent.data.client.OllamaClient
import com.neoutils.agent.domain.model.MessagePart
import com.neoutils.agent.domain.model.ToolAction
import com.neoutils.agent.feature.chat.data.model.*
import com.neoutils.agent.feature.chat.domain.model.ChatMessage
import com.neoutils.agent.feature.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion

class ChatRepositoryImpl(
    private val client: OllamaClient
) : ChatRepository {

    override fun chat(
        messages: List<ChatMessage>,
        model: String,
        tools: List<ToolAction>,
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

        val chatTools = tools.map { action ->
            ChatTool(
                function = ChatToolFunction(
                    name = action.name,
                    description = action.description,
                    parameters = ChatToolParameters(
                        properties = action.parameters.mapValues { (_, info) ->
                            ChatToolProperty(
                                type = info.type,
                                description = info.description,
                            )
                        },
                        required = action.parameters.filter { it.value.required }.keys.toList(),
                    ),
                ),
            )
        }

        return client
            .chat(model, inputMessages, chatTools.ifEmpty { null })
            .mapNotNull { output ->
                val toolCalls = output.message.toolCalls
                when {
                    !toolCalls.isNullOrEmpty() -> {
                        val call = toolCalls.first()
                        MessagePart.ToolCall(call.function.name, call.function.arguments)
                    }
                    output.message.thinking.isNotEmpty() -> MessagePart.Thinking(output.message.thinking)
                    output.message.content.isNotEmpty() -> MessagePart.Response(output.message.content)
                    else -> null
                }
            }
    }
}

package com.neoutils.agent.feature.chat.data.repository

import com.neoutils.agent.core.data.client.OllamaClient
import com.neoutils.agent.core.domain.model.MessagePart
import com.neoutils.agent.core.domain.tool.ToolDefinition
import com.neoutils.agent.feature.chat.data.model.*
import com.neoutils.agent.feature.chat.domain.model.ChatMessage
import com.neoutils.agent.feature.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class ChatRepositoryImpl(
    private val client: OllamaClient
) : ChatRepository {

    override fun chat(
        messages: List<ChatMessage>,
        model: String,
        toolDefinitions: List<ToolDefinition>,
    ): Flow<MessagePart> {
        val inputMessages = messages.map { msg ->
            ChatInputMessage(
                role = when (msg.role) {
                    ChatMessage.Role.System -> "system"
                    ChatMessage.Role.User -> "user"
                    ChatMessage.Role.Assistant -> "assistant"
                    ChatMessage.Role.Tool -> "tool"
                },
                content = msg.content,
                toolCalls = msg.toolCalls?.map { tc ->
                    ChatToolCall(
                        function = ChatToolCallFunction(
                            name = tc.name,
                            arguments = tc.arguments.toJsonObject(),
                        )
                    )
                },
            )
        }

        val chatTools = toolDefinitions.map { definition ->
            ChatTool(
                function = ChatToolFunction(
                    name = definition.name,
                    description = definition.description,
                    parameters = ChatToolParameters(
                        properties = definition.parameters.associate { param ->
                            param.name to ChatToolProperty(
                                type = param.type,
                                description = param.description,
                            )
                        },
                        required = definition.parameters
                            .filter { it.required }
                            .map { it.name },
                    ),
                ),
            )
        }

        return client
            .chat(model, inputMessages, chatTools.ifEmpty { null })
            .mapNotNull { output ->

                val toolCall = output.message.toolCalls?.firstOrNull()

                when {
                    toolCall != null -> {
                        MessagePart.ToolCall(
                            toolCall.function.name,
                            toolCall.function.argumentsAsMap()
                        )
                    }

                    output.message.thinking.isNotEmpty() -> {
                        MessagePart.Thinking(output.message.thinking)
                    }

                    output.message.content.isNotEmpty() -> {
                        MessagePart.Response(output.message.content)
                    }

                    else -> null
                }
            }
    }
}

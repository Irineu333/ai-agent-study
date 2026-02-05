package com.neoutils.agent.feature.chat.presentation

import com.neoutils.agent.core.domain.extension.flatMap
import com.neoutils.agent.core.domain.model.MessagePart
import com.neoutils.agent.core.domain.model.ToolCall
import com.neoutils.agent.core.domain.service.ToolService
import com.neoutils.agent.core.presentation.TerminalUI
import com.neoutils.agent.core.presentation.TextColors
import com.neoutils.agent.feature.chat.domain.SYSTEM_PROMPT
import com.neoutils.agent.feature.chat.domain.model.ChatMessage
import com.neoutils.agent.feature.chat.domain.model.ChatMessage.Role
import com.neoutils.agent.feature.chat.domain.repository.ChatRepository
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Chat(
    private val terminal: TerminalUI,
    private val model: String
) : KoinComponent {

    private val repository: ChatRepository by inject()
    private val toolService: ToolService by inject()

    fun run() = runBlocking {

        val history = mutableListOf(ChatMessage(Role.System, SYSTEM_PROMPT))

        var toolCall: ToolCall? = null

        while (true) {
            if (toolCall != null) {

                history.add(
                    ChatMessage(
                        role = Role.Assistant,
                        toolCalls = listOf(toolCall),
                    )
                )

                val result = toolService.resolve(toolCall)
                    .onSuccess { tool ->
                        terminal.println(tool.toString(), TextColors.blue)
                    }.onFailure {
                        terminal.println(toolCall.toString(), TextColors.blue)
                    }.flatMap { tool ->
                        tool.invoke()
                    }

                val content = result.fold(
                    onSuccess = { it },
                    onFailure = {
                        it.message.orEmpty()
                    }
                )

                history.add(
                    ChatMessage(
                        role = Role.Tool,
                        content = content,
                    )
                )

                val contentColor = result.fold(
                    onSuccess = { TextColors.gray(0.4f) },
                    onFailure = { TextColors.red }
                )

                content.trim().lines().forEachIndexed { index, line ->
                    if (index == 0) {
                        terminal.println("L $line", contentColor)
                    } else {
                        terminal.println("  $line", contentColor)
                    }
                }

                toolCall = null
            } else {
                val input = terminal.prompt("> ") ?: break

                if (input.isEmpty()) continue

                history.add(ChatMessage(Role.User, input))
            }

            var isThinking = false
            val thinkingBuilder = StringBuilder()
            val responseBuilder = StringBuilder()

            val job = terminal.loading()

            repository.chat(
                messages = history,
                model = model,
                toolDefinitions = toolService.definitions,
            ).collect { message ->
                job.cancelAndJoin()

                when (message) {
                    is MessagePart.Thinking -> {
                        if (!isThinking) {
                            terminal.println()
                            isThinking = true
                        }
                        terminal.print(message.content, TextColors.gray(0.6f))
                        thinkingBuilder.append(message.content)
                    }

                    is MessagePart.Response -> {
                        if (isThinking) {
                            terminal.println()
                            terminal.println()
                            isThinking = false
                        }

                        terminal.print(message.content)
                        responseBuilder.append(message.content)
                    }

                    is MessagePart.Tooling -> {
                        toolCall = ToolCall(
                            name = message.name,
                            arguments = message.arguments,
                        )

                        terminal.println()
                    }
                }
            }

            if (responseBuilder.isNotEmpty()) {
                history.add(ChatMessage(Role.Assistant, responseBuilder.toString()))
            }

            if (thinkingBuilder.isNotEmpty() || responseBuilder.isNotEmpty()) {
                terminal.println()
            }
        }
    }
}

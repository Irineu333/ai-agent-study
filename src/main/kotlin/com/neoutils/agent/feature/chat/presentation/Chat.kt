package com.neoutils.agent.feature.chat.presentation

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.table.verticalLayout
import com.github.ajalt.mordant.terminal.prompt
import com.neoutils.agent.core.domain.model.MessagePart
import com.neoutils.agent.core.domain.service.ToolService
import com.neoutils.agent.core.presentation.loading
import com.neoutils.agent.feature.chat.domain.SYSTEM_PROMPT
import com.neoutils.agent.feature.chat.domain.model.ChatMessage
import com.neoutils.agent.feature.chat.domain.model.ChatMessage.Role
import com.neoutils.agent.feature.chat.domain.model.ToolCallInfo
import com.neoutils.agent.feature.chat.domain.repository.ChatRepository
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Chat : CliktCommand(name = "chat"), KoinComponent {

    private val repository: ChatRepository by inject()
    private val toolService: ToolService by inject()

    private val model by option("--model", "-m").required()

    override fun run() = runBlocking {

        val history = mutableListOf(ChatMessage(Role.System, SYSTEM_PROMPT))

        var toolCall: MessagePart.ToolCall? = null

        while (true) {
            if (toolCall != null) {

                history.add(
                    ChatMessage(
                        role = Role.Assistant,
                        toolCalls = listOf(ToolCallInfo(toolCall.name, toolCall.arguments))
                    )
                )

                val result = toolService.resolve(
                    toolCall.name,
                    toolCall.arguments
                ).onSuccess { tool ->
                    terminal.println(TextColors.blue(tool.toString()))
                }.onFailure {
                    terminal.println(TextColors.blue(toolCall.toString()))
                }.flatMap { tool ->
                    tool()
                }

                val content = result.fold(
                    onSuccess = {
                        it
                    },
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

                val contentStyle = result.fold(
                    onSuccess = {
                        TextColors.gray(0.4)
                    },
                    onFailure = {
                        TextColors.red
                    }
                )

                terminal.println(
                    verticalLayout {
                        content
                            .trim()
                            .lines()
                            .forEachIndexed { index, line ->
                                if (index == 0) {
                                    cell(contentStyle("L $line"))
                                } else {
                                    cell(contentStyle("  $line"))
                                }
                            }
                    }
                )
                toolCall = null
            } else {
                val input = terminal.prompt(prompt = ">", promptSuffix = " ") ?: break

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
                        terminal.print(TextColors.gray(0.6)(message.content))
                        thinkingBuilder.append(message.content)
                    }

                    is MessagePart.Response -> {
                        if (isThinking) {
                            terminal.println("\n")
                            isThinking = false
                        }

                        terminal.print(message.content)
                        responseBuilder.append(message.content)
                    }

                    is MessagePart.ToolCall -> {
                        toolCall = message
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

private inline fun <T, R> Result<T>.flatMap(block: (T) -> Result<R>): Result<R> {
    return fold(
        onSuccess = block,
        onFailure = {
            Result.failure(it)
        }
    )
}

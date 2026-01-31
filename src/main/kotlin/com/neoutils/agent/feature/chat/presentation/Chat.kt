package com.neoutils.agent.feature.chat.presentation

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.prompt
import com.neoutils.agent.domain.model.AgentConfig
import com.neoutils.agent.domain.model.MessagePart
import com.neoutils.agent.domain.model.ToolAction
import com.neoutils.agent.feature.chat.domain.model.ChatMessage
import com.neoutils.agent.feature.chat.domain.model.ChatMessage.Role
import com.neoutils.agent.feature.chat.domain.repository.ChatRepository
import com.neoutils.agent.presentation.loading
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.system.exitProcess

class Chat : CliktCommand(name = "chat"), KoinComponent {

    private val repository: ChatRepository by inject()

    private val config by requireObject<AgentConfig>()

    override fun run() = runBlocking {

        val history = mutableListOf<ChatMessage>()

        while (true) {

            val input = terminal.prompt(prompt = "prompt") ?: break

            if (input.isEmpty()) continue

            history.add(ChatMessage(Role.User, input))

            var isThinking = false
            val responseBuilder = StringBuilder()

            val job = terminal.loading()

            repository.chat(
                messages = history,
                model = config.model,
                tools = listOf(
                    ToolAction(
                        name = "exit",
                        description = "Exit the chat program. Call this tool when the user wants to leave, quit, or exit the conversation.",
                    )
                )
            ).collect { message ->
                job.cancelAndJoin()

                when (message) {
                    is MessagePart.Thinking -> {
                        isThinking = true
                        terminal.print(TextColors.gray(message.content))
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
                        if (message.name == "exit") {
                            exitProcess(0)
                        }
                    }
                }
            }

            terminal.println("\n")
            history.add(ChatMessage(Role.Assistant, responseBuilder.toString()))
        }
    }
}

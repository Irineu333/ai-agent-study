package com.neoutils.agent.feature.chat.presentation

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.prompt
import com.neoutils.agent.domain.model.AgentConfig
import com.neoutils.agent.domain.model.MessagePart
import com.neoutils.agent.domain.model.ToolAction
import com.neoutils.agent.domain.model.ToolResult
import com.neoutils.agent.feature.chat.domain.model.CallToolAction
import com.neoutils.agent.feature.chat.domain.model.ChatMessage
import com.neoutils.agent.feature.chat.domain.model.ChatMessage.Role
import com.neoutils.agent.feature.chat.domain.model.ToolCallInfo
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

    private val tools = listOf(
        ToolAction(
            name = "exit",
            description = "Exit the chat program. Call this tool when the user wants to leave, quit, or exit the conversation.",
            execute = {
                exitProcess(1)
            },
        ),
        ToolAction(
            name = "shell",
            description = "Execute a shell command on the user's terminal and return the output.",
            parameters = mapOf(
                "command" to ToolAction.ParameterInfo(
                    type = "string",
                    description = "The shell command to execute",
                    required = true,
                ),
            ),
            execute = { args ->
                val command = requireNotNull(args["command"])

                val process = ProcessBuilder("sh", "-c", command)
                    .redirectErrorStream(true)
                    .start()

                val output = process.inputStream.bufferedReader().readText()
                process.waitFor()
                ToolResult(content = output)
            },
        ),
    )

    override fun run() = runBlocking {

        val history = mutableListOf<ChatMessage>()
        var callToolAction: CallToolAction? = null

        while (true) {
            if (callToolAction != null) {

                val arguments = callToolAction.arguments
                    .map { "${it.key} = ${it.value}" }
                    .joinToString(prefix = "(", postfix = ")")

                terminal.println(TextColors.blue("${callToolAction.toolAction.name}$arguments\n"))

                history.add(
                    ChatMessage(
                        role = Role.Assistant,
                        toolCalls = listOf(ToolCallInfo(callToolAction.toolAction.name, callToolAction.arguments))
                    )
                )

                val result = callToolAction.toolAction.execute(callToolAction.arguments)

                history.add(
                    ChatMessage(
                        role = Role.Tool,
                        content = result.content
                    )
                )

                callToolAction = null
            } else {
                val input = terminal.prompt(prompt = "prompt") ?: break

                if (input.isEmpty()) continue

                history.add(ChatMessage(Role.User, input))
            }

            var isThinking = false
            val responseBuilder = StringBuilder()

            val job = terminal.loading()

            repository.chat(
                messages = history,
                model = config.model,
                tools = tools,
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
                        callToolAction = CallToolAction(
                            toolAction = tools.first { it.name == message.name },
                            arguments = message.arguments,
                        )
                    }
                }
            }

            if (callToolAction == null) {
                history.add(ChatMessage(Role.Assistant, responseBuilder.toString()))
            }

            terminal.println("\n")
        }
    }
}

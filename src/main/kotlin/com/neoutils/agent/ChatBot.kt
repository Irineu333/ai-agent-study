package com.neoutils.agent

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextColors
import com.neoutils.agent.domain.model.ChatMessagePart
import com.neoutils.agent.domain.repository.ChatRepository
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatBot : CliktCommand(name = "chatbot"), KoinComponent {

    private val repository: ChatRepository by inject()

    private val model by option("--model", "-m")

    private val prompt by argument()

    override fun run() = runBlocking {

        var isThinking = false

        repository.generate(
            prompt = prompt,
            model = requireNotNull(model) { "model is required" },
        ).collect { message ->
            when (message) {
                is ChatMessagePart.Thinking -> {
                    isThinking = true
                    terminal.print(TextColors.gray(message.content))
                }

                is ChatMessagePart.Response -> {
                    if (isThinking) {
                        terminal.println("\n")
                        isThinking = false
                    }
                    terminal.print(message.content)
                }
            }
        }
    }
}

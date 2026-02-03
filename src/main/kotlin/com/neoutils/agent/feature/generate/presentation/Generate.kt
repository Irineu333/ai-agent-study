package com.neoutils.agent.feature.generate.presentation

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.mordant.rendering.TextColors
import com.neoutils.agent.core.domain.model.MessagePart
import com.neoutils.agent.core.presentation.loading
import com.neoutils.agent.feature.generate.domain.repository.GenerateRepository
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Generate : CliktCommand(name = "generate"), KoinComponent {

    private val repository: GenerateRepository by inject()

    private val prompt by argument()

    private val model by option("--model", "-m").required()

    override fun run() = runBlocking {

        var isThinking = false

        val job = terminal.loading()

        repository.generate(
            prompt = prompt,
            model = model,
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
                }

                is MessagePart.ToolCall -> Unit
            }
        }
    }
}

package com.neoutils.agent.feature.generate.presentation

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.mordant.rendering.TextColors
import com.neoutils.agent.domain.model.AgentConfig
import com.neoutils.agent.domain.model.MessagePart
import com.neoutils.agent.feature.generate.domain.repository.GenerateRepository
import com.neoutils.agent.presentation.loading
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Generate : CliktCommand(name = "generate"), KoinComponent {

    private val repository: GenerateRepository by inject()

    private val config by requireObject<AgentConfig>()

    private val prompt by argument()

    override fun run() = runBlocking {

        var isThinking = false

        val job = terminal.loading()

        repository.generate(
            prompt = prompt,
            model = config.model,
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
            }
        }
    }
}

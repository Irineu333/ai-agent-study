package com.neoutils.agent.feature.generate.presentation

import com.neoutils.agent.core.domain.model.MessagePart
import com.neoutils.agent.core.presentation.TerminalUI
import com.neoutils.agent.core.presentation.TextColors
import com.neoutils.agent.feature.generate.domain.repository.GenerateRepository
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Generate(
    private val terminal: TerminalUI,
    private val prompt: String,
    private val model: String
) : KoinComponent {

    private val repository: GenerateRepository by inject()

    fun run() = runBlocking {

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
                    terminal.print(message.content, TextColors.gray(0.6f))
                }

                is MessagePart.Response -> {
                    if (isThinking) {
                        terminal.println()
                        terminal.println()
                        isThinking = false
                    }
                    terminal.print(message.content)
                }

                is MessagePart.Tooling -> Unit
            }
        }

        terminal.println()
    }
}

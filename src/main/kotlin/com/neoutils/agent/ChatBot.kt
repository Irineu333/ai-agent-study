package com.neoutils.agent

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextColors
import com.neoutils.agent.data.client.OllamaClient
import kotlinx.coroutines.runBlocking

class ChatBot : CliktCommand(name = "chatbot") {

    private val model by option("--model", "-m")

    private val prompt by argument()

    override fun run() = runBlocking {

        val client = OllamaClient()

        var thinking = false

        client.generate(
            prompt = prompt,
            model = requireNotNull(model) { "model is required" },
        ).collect { output ->
            if (output.thinking.isNotEmpty()) {
                thinking = true
                terminal.print(TextColors.gray(output.thinking))
                return@collect
            }

            if (thinking) {
                terminal.println("\n")
            }

            if (output.response.isNotEmpty()) {
                thinking = false
                terminal.print(output.response)
            }
        }

        client.close()
    }
}

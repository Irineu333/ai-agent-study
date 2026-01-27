package com.neoutils.agent

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.neoutils.agent.data.client.OllamaClient
import kotlinx.coroutines.runBlocking

class ChatBot : CliktCommand(name = "chatbot") {

    private val model by option("--model", "-m")

    private val prompt by argument()

    override fun run() = runBlocking {

        val client = OllamaClient()

        echo(
            client.generate(
                prompt = prompt,
                model = requireNotNull(model) {
                    "model is required"
                },
            )
        )

        client.close()
    }
}
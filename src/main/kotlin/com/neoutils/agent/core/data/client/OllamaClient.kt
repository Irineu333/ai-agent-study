package com.neoutils.agent.core.data.client

import com.neoutils.agent.feature.chat.data.model.ChatInput
import com.neoutils.agent.feature.chat.data.model.ChatInputMessage
import com.neoutils.agent.feature.chat.data.model.ChatOutput
import com.neoutils.agent.feature.chat.data.model.ChatTool
import com.neoutils.agent.feature.generate.data.model.GenerateInput
import com.neoutils.agent.feature.generate.data.model.GenerateOutput
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class OllamaClient(
    private val baseUrl: String = "http://localhost:11434"
) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
            socketTimeoutMillis = 120_000
        }
    }

    fun generate(
        prompt: String,
        model: String,
    ): Flow<GenerateOutput> = flow {

        val input = GenerateInput(
            prompt = prompt,
            model = model,
            stream = true,
        )

        val body = json.encodeToString(GenerateInput.serializer(), input)

        client.preparePost("$baseUrl/api/generate") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.execute { response ->
            val channel = response.bodyAsChannel()

            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line()?.takeIsNotBlank() ?: break

                val output = json.decodeFromString(GenerateOutput.serializer(), line)

                emit(output)

                if (output.done) break
            }
        }
    }

    fun chat(
        model: String,
        messages: List<ChatInputMessage>,
        tools: List<ChatTool>? = null,
    ): Flow<ChatOutput> = flow {

        val input = ChatInput(
            model = model,
            messages = messages,
            stream = true,
            tools = tools,
        )

        val body = json.encodeToString(ChatInput.serializer(), input)

        client.preparePost("$baseUrl/api/chat") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.execute { response ->
            val channel = response.bodyAsChannel()

            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line()?.takeIsNotBlank() ?: break

                val output = json.decodeFromString(ChatOutput.serializer(), line)

                emit(output)

                if (output.done) break
            }
        }
    }

    fun close() = client.close()
}

private fun String.takeIsNotBlank() = takeIf { it.isNotBlank() }

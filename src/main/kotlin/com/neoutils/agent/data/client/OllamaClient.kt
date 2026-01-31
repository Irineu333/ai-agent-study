package com.neoutils.agent.data.client

import com.neoutils.agent.data.model.Input
import com.neoutils.agent.data.model.Output
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
    ): Flow<Output> = flow {

        val input = Input(
            prompt = prompt,
            model = model,
            stream = true,
        )

        val body = json.encodeToString(Input.serializer(), input)

        client.preparePost("$baseUrl/api/generate") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.execute { response ->
            val channel = response.bodyAsChannel()

            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break

                val output = json.decodeFromString(Output.serializer(), line)
                emit(output)

                if (output.done) break
            }
        }
    }

    fun close() = client.close()
}

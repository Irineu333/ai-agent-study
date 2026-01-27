package com.neoutils.agent.data.client

import com.neoutils.agent.data.model.Input
import com.neoutils.agent.data.model.Output
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class OllamaClient(
    private val baseUrl: String = "http://localhost:11434"
) {
    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
            socketTimeoutMillis = 120_000
        }

        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                }
            )
        }
    }

    suspend fun generate(
        prompt: String,
        model: String,
    ): String {

        val input = Input(
            prompt = prompt,
            model = model,
            stream = false,
        )

        val output = client.post("$baseUrl/api/generate") {
            contentType(ContentType.Application.Json)
            setBody(input)
        }.body<Output>()

        return output.response
    }

    fun close() = client.close()
}
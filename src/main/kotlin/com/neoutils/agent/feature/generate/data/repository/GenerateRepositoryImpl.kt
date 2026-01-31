package com.neoutils.agent.feature.generate.data.repository

import com.neoutils.agent.data.client.OllamaClient
import com.neoutils.agent.domain.model.MessagePart
import com.neoutils.agent.feature.generate.domain.repository.GenerateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class GenerateRepositoryImpl(
    private val client: OllamaClient
) : GenerateRepository {

    override fun generate(prompt: String, model: String): Flow<MessagePart> {
        return client
            .generate(prompt, model)
            .mapNotNull { output ->
                when {
                    output.thinking.isNotEmpty() -> MessagePart.Thinking(output.thinking)
                    output.response.isNotEmpty() -> MessagePart.Response(output.response)
                    else -> null
                }
            }
    }
}

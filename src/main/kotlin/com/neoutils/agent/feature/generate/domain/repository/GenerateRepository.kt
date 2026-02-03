package com.neoutils.agent.feature.generate.domain.repository

import com.neoutils.agent.core.domain.model.MessagePart
import kotlinx.coroutines.flow.Flow

interface GenerateRepository {
    fun generate(prompt: String, model: String): Flow<MessagePart>
}

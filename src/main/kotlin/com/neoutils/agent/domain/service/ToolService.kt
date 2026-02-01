package com.neoutils.agent.domain.service

import com.neoutils.agent.domain.tool.ToolDefinition
import com.neoutils.agent.domain.tool.ToolExecutionResult

interface ToolService {

    val definitions: List<ToolDefinition>

    suspend fun execute(
        name: String,
        arguments: Map<String, Any>
    ): ToolExecutionResult
}
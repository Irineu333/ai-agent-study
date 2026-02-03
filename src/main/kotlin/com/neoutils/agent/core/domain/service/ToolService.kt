package com.neoutils.agent.core.domain.service

import com.neoutils.agent.core.domain.tool.ToolDefinition
import com.neoutils.agent.core.domain.tool.ToolExecution

interface ToolService {

    val definitions: List<ToolDefinition>

    fun resolve(
        name: String,
        arguments: Map<String, Any>
    ): Result<ToolExecution>
}

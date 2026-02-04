package com.neoutils.agent.core.data.service

import com.neoutils.agent.core.domain.model.ToolCall
import com.neoutils.agent.core.domain.service.ToolService
import com.neoutils.agent.core.domain.tool.ToolDefinition
import com.neoutils.agent.core.domain.tool.ToolExecution

class ToolServiceImpl(
    override val definitions: List<ToolDefinition>
) : ToolService {
    private val map = definitions.associateBy { it.name }

    override fun resolve(
        toolCall: ToolCall,
    ): Result<ToolExecution> {
        val tool = map[toolCall.name]

        if (tool == null) {
            val notFoundError = buildString {
                appendLine("Tool ${toolCall.name} not found.")
                appendLine(
                    map.keys.joinToString(
                        prefix = "Supported: ",
                        postfix = "."
                    )
                )
            }

            return Result.failure(Exception(notFoundError))
        }

        return tool.resolve(toolCall.arguments)
    }
}

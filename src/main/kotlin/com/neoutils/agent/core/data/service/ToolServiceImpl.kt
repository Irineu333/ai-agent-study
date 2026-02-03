package com.neoutils.agent.core.data.service

import com.neoutils.agent.core.domain.service.ToolService
import com.neoutils.agent.core.domain.tool.ToolDefinition
import com.neoutils.agent.core.domain.tool.ToolExecution

class ToolServiceImpl(
    override val definitions: List<ToolDefinition>
) : ToolService {
    private val map = definitions.associateBy { it.name }

    override fun resolve(
        name: String,
        arguments: Map<String, Any>
    ): Result<ToolExecution> {
        val tool = map[name]

        if (tool == null) {
            val notFoundError = buildString {
                appendLine("Tool $name not found.")
                appendLine(
                    map.keys.joinToString(
                        prefix = "Supported: ",
                        postfix = "."
                    )
                )
            }

            return Result.failure(Exception(notFoundError))
        }

        return tool.resolve(arguments)
    }
}

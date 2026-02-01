package com.neoutils.agent.domain.service

import com.neoutils.agent.domain.tool.Tool
import com.neoutils.agent.domain.tool.ToolExecutionResult

class ToolServiceImpl(
    private val tools: List<Tool>
) : ToolService {
    private val map = tools.associateBy { it.definition.name }

    override val definitions get() = tools.map { it.definition }

    override suspend fun execute(
        name: String,
        arguments: Map<String, Any>
    ): ToolExecutionResult {

        val tool = map[name] ?: return ToolExecutionResult.Failure(
            content = buildString {
                appendLine("Tool $name not found.")
                appendLine(definitions.joinToString(prefix = "Existing: ", postfix = "."))
            },
        )

        return tool.execute(arguments)
    }
}
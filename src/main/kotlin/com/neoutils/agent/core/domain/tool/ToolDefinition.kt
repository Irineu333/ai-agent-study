package com.neoutils.agent.core.domain.tool

data class ToolParameter(
    val name: String,
    val type: String,
    val description: String,
    val required: Boolean
)

sealed class ToolExecutionResult(open val content: String) {
    data class Success(
        override val content: String
    ) : ToolExecutionResult(content)

    data class Failure(
        override val content: String
    ) : ToolExecutionResult(content)
}

abstract class ToolDefinition(
    val name: String,
    val description: String,
    val parameters: List<ToolParameter>
) {
    abstract fun resolve(
        arguments: Map<String, Any>
    ): Result<ToolExecution>
}

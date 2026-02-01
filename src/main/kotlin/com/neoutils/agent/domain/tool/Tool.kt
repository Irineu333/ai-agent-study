package com.neoutils.agent.domain.tool

data class ToolDefinition(
    val name: String,
    val description: String,
    val parameters: List<ToolParameter>
)

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

abstract class Tool(val definition: ToolDefinition) {
    abstract suspend fun execute(
        arguments: Map<String, Any>
    ): ToolExecutionResult
}

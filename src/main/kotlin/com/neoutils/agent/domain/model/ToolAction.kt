package com.neoutils.agent.domain.model

data class ToolAction(
    val name: String,
    val description: String,
    val parameters: Map<String, ParameterInfo> = emptyMap(),
    val execute: (arguments: Map<String, String>) -> ToolResult,
) {
    data class ParameterInfo(val type: String, val description: String, val required: Boolean = false)
}

data class ToolResult(val content: String, val exit: Boolean = false)

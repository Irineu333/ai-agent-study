package com.neoutils.agent.domain.model

data class ToolAction(
    val name: String,
    val description: String,
    val parameters: Map<String, ParameterInfo> = emptyMap(),
) {
    data class ParameterInfo(val type: String, val description: String, val required: Boolean = false)
}

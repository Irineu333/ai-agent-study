package com.neoutils.agent.core.domain.model

data class ToolCall(
    val name: String,
    val arguments: Map<String, Any>,
) {
    override fun toString(): String {

        if (arguments.size == 1) {
            return "${name}(${arguments.values.first()})"
        }

        val arguments = arguments
            .entries
            .joinToString { "${it.key}: ${it.value}" }

        return "${name}($arguments)"
    }
}
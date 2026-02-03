package com.neoutils.agent.core.domain.model

sealed class MessagePart {
    data class Thinking(val content: String) : MessagePart()

    data class Response(val content: String) : MessagePart()

    data class ToolCall(
        val name: String,
        val arguments: Map<String, Any>
    ) : MessagePart() {
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
}

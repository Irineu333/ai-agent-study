package com.neoutils.agent.tool.shell

import com.neoutils.agent.core.domain.tool.ToolDefinition
import com.neoutils.agent.core.domain.tool.ToolExecution
import com.neoutils.agent.core.domain.tool.ToolParameter

class ShellToolDefinition : ToolDefinition(
    name = "shell",
    description = "Execute a shell command and return the output",
    parameters = listOf(
        ToolParameter(
            name = "command",
            type = "string",
            description = "The shell command to execute",
            required = true,
        )
    )
) {
    override fun resolve(
        arguments: Map<String, Any>
    ): Result<ToolExecution> {
        val command = arguments["command"] ?: return Result.failure(Exception("'command' parameter is required"))

        if (command !is String) {
            return Result.failure(Exception("'command' must be a string"))
        }

        return Result.success(ShellToolExecution(command = command))
    }
}

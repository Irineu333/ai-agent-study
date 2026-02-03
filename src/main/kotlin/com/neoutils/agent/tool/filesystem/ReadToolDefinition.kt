package com.neoutils.agent.tool.filesystem

import com.neoutils.agent.core.domain.tool.ToolDefinition
import com.neoutils.agent.core.domain.tool.ToolExecution
import com.neoutils.agent.core.domain.tool.ToolParameter
import java.nio.file.Path

class ReadToolDefinition : ToolDefinition(
    name = "read_file",
    description = "Read the contents of a file at a given path",
    parameters = listOf(
        ToolParameter(
            name = "path",
            type = "string",
            description = "The file path to read",
            required = true,
        ),
    )
) {
    override fun resolve(
        arguments: Map<String, Any>
    ): Result<ToolExecution> {
        val pathString = arguments["path"]
            ?: return Result.failure(Exception("'path' parameter is required"))

        if (pathString !is String) {
            return Result.failure(Exception("'path' must be a string"))
        }

        return Result.success(ReadToolExecution(path = Path.of(pathString)))
    }
}

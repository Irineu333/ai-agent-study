package com.neoutils.agent.tool.filesystem

import com.neoutils.agent.core.domain.tool.ToolDefinition
import com.neoutils.agent.core.domain.tool.ToolExecution
import com.neoutils.agent.core.domain.tool.ToolParameter
import java.nio.file.Path

class WriteToolDefinition : ToolDefinition(
    name = "write_file",
    description = "Write content to a file, creating it if it doesn't exist",
    parameters = listOf(
        ToolParameter(
            name = "path",
            type = "string",
            description = "The file path to write",
            required = true,
        ),
        ToolParameter(
            name = "content",
            type = "string",
            description = "The content to write to the file",
            required = true,
        ),
        ToolParameter(
            name = "overwrite",
            type = "boolean",
            description = "Overwrite if file already exists (default: false)",
            required = false,
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

        val content = arguments["content"]
            ?: return Result.failure(Exception("'content' parameter is required"))

        if (content !is String) {
            return Result.failure(Exception("'content' must be a string"))
        }

        val overwrite = arguments["overwrite"]?.toString()?.toBoolean() ?: false

        return Result.success(
            WriteToolExecution(
                path = Path.of(pathString),
                content = content,
                overwrite = overwrite
            )
        )
    }
}

package com.neoutils.agent.tool.filesystem

import com.neoutils.agent.core.domain.tool.ToolDefinition
import com.neoutils.agent.core.domain.tool.ToolExecution
import com.neoutils.agent.core.domain.tool.ToolParameter
import java.nio.file.Path

class ListToolDefinition : ToolDefinition(
    name = "list_path",
    description = "List files and directories at a given path",
    parameters = listOf(
        ToolParameter(
            name = "path",
            type = "string",
            description = "The directory path to list",
            required = true,
        ),
        ToolParameter(
            name = "recursive",
            type = "boolean",
            description = "List recursively (default: false)",
            required = false,
        ),
        ToolParameter(
            name = "show_hidden",
            type = "boolean",
            description = "Include hidden files (default: false)",
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

        val recursive = arguments["recursive"] == true
        val showHidden = arguments["show_hidden"] == true

        return Result.success(
            ListToolExecution(
                path = Path.of(pathString),
                recursive = recursive,
                showHidden = showHidden
            )
        )
    }
}

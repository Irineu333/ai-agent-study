package com.neoutils.agent.tool.filesystem

import com.neoutils.agent.core.domain.tool.ToolDefinition
import com.neoutils.agent.core.domain.tool.ToolExecution
import com.neoutils.agent.core.domain.tool.ToolParameter
import java.nio.file.Path

class GrepToolDefinition : ToolDefinition(
    name = "grep",
    description = "Search for a pattern in file contents. " +
            "Use this to find where something is defined or used in the codebase. " +
            "Returns matching lines with file paths and line numbers.",
    parameters = listOf(
        ToolParameter(
            name = "path",
            type = "string",
            description = "The file or directory to search in",
            required = true,
        ),
        ToolParameter(
            name = "pattern",
            type = "string",
            description = "The regex pattern to search for (e.g., 'fun main', 'TODO:', 'class.*Service')",
            required = true,
        ),
        ToolParameter(
            name = "glob",
            type = "string",
            description = "Filter files by glob pattern (e.g., '*.kt', '*.json'). Only used when path is a directory.",
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

        val pattern = arguments["pattern"]
            ?: return Result.failure(Exception("'pattern' parameter is required"))

        if (pattern !is String) {
            return Result.failure(Exception("'pattern' must be a string"))
        }

        val glob = arguments["glob"]?.toString()

        return Result.success(
            GrepToolExecution(
                path = Path.of(pathString),
                pattern = pattern,
                glob = glob
            )
        )
    }
}

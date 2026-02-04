package com.neoutils.agent.tool.filesystem

import com.neoutils.agent.core.domain.tool.ToolDefinition
import com.neoutils.agent.core.domain.tool.ToolExecution
import com.neoutils.agent.core.domain.tool.ToolParameter
import java.nio.file.Path

class GlobToolDefinition : ToolDefinition(
    name = "glob",
    description = "Find files matching a glob pattern. " +
            "Use this to quickly locate files by name pattern instead of manually listing directories. " +
            "Examples: '**/*.kt' (all Kotlin files), 'src/**/*.json' (JSON files in src), '*.md' (markdown in root).",
    parameters = listOf(
        ToolParameter(
            name = "path",
            type = "string",
            description = "The base directory to search in",
            required = true,
        ),
        ToolParameter(
            name = "pattern",
            type = "string",
            description = "The glob pattern to match files (e.g., '**/*.kt', 'src/**/*.json')",
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

        val pattern = arguments["pattern"]
            ?: return Result.failure(Exception("'pattern' parameter is required"))

        if (pattern !is String) {
            return Result.failure(Exception("'pattern' must be a string"))
        }

        return Result.success(
            GlobToolExecution(
                path = Path.of(pathString),
                pattern = pattern
            )
        )
    }
}

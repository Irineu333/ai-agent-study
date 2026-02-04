package com.neoutils.agent.tool.filesystem

import com.neoutils.agent.core.domain.tool.ToolDefinition
import com.neoutils.agent.core.domain.tool.ToolExecution
import com.neoutils.agent.core.domain.tool.ToolParameter
import java.nio.file.Path

class EditToolDefinition : ToolDefinition(
    name = "edit_file",
    description = "Edit a file by replacing a specific string with a new string. " +
            "Use this for surgical edits instead of rewriting the entire file.",
    parameters = listOf(
        ToolParameter(
            name = "path",
            type = "string",
            description = "The file path to edit",
            required = true,
        ),
        ToolParameter(
            name = "old_string",
            type = "string",
            description = "The exact string to find and replace",
            required = true,
        ),
        ToolParameter(
            name = "new_string",
            type = "string",
            description = "The new string to replace with",
            required = true,
        ),
        ToolParameter(
            name = "replace_all",
            type = "boolean",
            description = "Replace all occurrences instead of just the first (default: false)",
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

        val oldString = arguments["old_string"]
            ?: return Result.failure(Exception("'old_string' parameter is required"))

        if (oldString !is String) {
            return Result.failure(Exception("'old_string' must be a string"))
        }

        val newString = arguments["new_string"]
            ?: return Result.failure(Exception("'new_string' parameter is required"))

        if (newString !is String) {
            return Result.failure(Exception("'new_string' must be a string"))
        }

        val replaceAll = arguments["replace_all"]?.toString()?.toBoolean() ?: false

        return Result.success(
            EditToolExecution(
                path = Path.of(pathString),
                oldString = oldString,
                newString = newString,
                replaceAll = replaceAll
            )
        )
    }
}

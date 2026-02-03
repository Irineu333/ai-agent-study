package com.neoutils.agent.domain.tool

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*

class ListTool : Tool(
    definition = ToolDefinition(
        name = "list",
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
    )
) {
    override suspend fun execute(
        arguments: Map<String, Any>
    ): ToolExecutionResult {
        val pathString = arguments["path"]
            ?: return ToolExecutionResult.Failure("'path' parameter is required")

        val recursive = arguments["recursive"]?.toString()?.toBoolean() ?: false
        val showHidden = arguments["show_hidden"]?.toString()?.toBoolean() ?: false

        val path = Path(pathString.toString())

        if (!path.exists()) {
            return ToolExecutionResult.Failure("Path does not exist: $pathString")
        }

        if (!path.isDirectory()) {
            return ToolExecutionResult.Failure("Path is not a directory: $pathString")
        }

        val entries = if (recursive) {
            Files.walk(path)
                .skip(1)
                .toList()
        } else {
            path.listDirectoryEntries()
        }

        val filtered = if (showHidden) {
            entries
        } else {
            entries.filterNot {
                it.isHidden() || it.name.startsWith(".")
            }
        }

        val sorted = filtered.sortedWith(
            compareByDescending<Path> {
                it.isDirectory()
            }.thenBy {
                it.pathString
            }
        )

        val output = sorted.joinToString("\n") { entry ->
            val suffix = "/".takeIf { entry.isDirectory() }.orEmpty()
            "${entry.pathString}$suffix"
        }

        return ToolExecutionResult.Success(output.ifEmpty { "Directory is empty" })
    }
}

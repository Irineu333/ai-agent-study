package com.neoutils.agent.domain.tool

import kotlin.io.path.*

class ReadTool : Tool(
    definition = ToolDefinition(
        name = "read",
        description = "Read the contents of a file at a given path",
        parameters = listOf(
            ToolParameter(
                name = "path",
                type = "string",
                description = "The file path to read",
                required = true,
            ),
        )
    )
) {
    override suspend fun execute(
        arguments: Map<String, Any>
    ): ToolExecutionResult {
        val pathString = arguments["path"]
            ?: return ToolExecutionResult.Failure("'path' parameter is required")

        val path = java.nio.file.Path.of(pathString.toString())

        if (!path.exists()) {
            return ToolExecutionResult.Failure("Path does not exist: $pathString")
        }

        if (!path.isRegularFile()) {
            return ToolExecutionResult.Failure("Path is not a file: $pathString")
        }

        val content = path.readText()

        return ToolExecutionResult.Success(content.ifEmpty { "File is empty" })
    }
}

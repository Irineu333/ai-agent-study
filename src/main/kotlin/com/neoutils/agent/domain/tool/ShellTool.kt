package com.neoutils.agent.domain.tool

import java.io.BufferedReader

class ShellTool : Tool(
    definition = ToolDefinition(
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
    )
) {
    override suspend fun execute(
        arguments: Map<String, Any>
    ): ToolExecutionResult {
        val command = arguments["command"] ?: return ToolExecutionResult.Failure("'command' parameter is required")

        val process = ProcessBuilder()
            .command("sh", "-c", command.toString())
            .start()

        val output = process
            .inputStream
            .bufferedReader()
            .use(BufferedReader::readText)

        process.waitFor()

        if (process.exitValue() == 1) {

            val output = process
                .errorStream
                .bufferedReader()
                .use(BufferedReader::readText)

            return ToolExecutionResult.Failure(output.ifEmpty { "Command failed with no output" })
        }

        return ToolExecutionResult.Success(output.ifEmpty { "Command executed successfully with no output" })
    }
}

package com.neoutils.agent.tool.shell

import com.neoutils.agent.core.domain.tool.ToolExecution
import java.io.BufferedReader

class ShellToolExecution(
    val command: String,
) : ToolExecution {
    override suspend operator fun invoke(): Result<String> {

        val process = ProcessBuilder()
            .command("sh", "-c", command)
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

            Result.success(Exception(output.ifEmpty { "Command failed with no output" }))
        }

        return Result.success(output.ifEmpty { "Command executed successfully with no output" })
    }

    override fun toString(): String {
        return "shell($command})"
    }
}

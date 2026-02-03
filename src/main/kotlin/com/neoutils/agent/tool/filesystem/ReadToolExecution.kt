package com.neoutils.agent.tool.filesystem

import com.neoutils.agent.core.domain.tool.ToolExecution
import java.nio.file.Path
import kotlin.io.path.*

class ReadToolExecution(
    private val path: Path
) : ToolExecution {
    override suspend fun invoke(): Result<String> {
        if (!path.exists()) {
            return Result.failure(Exception("Path does not exist: $path"))
        }

        if (!path.isRegularFile()) {
            return Result.failure(Exception("Path is not a file: $path"))
        }

        val content = path.readText()

        return Result.success(content.ifEmpty { "File is empty" })
    }

    override fun toString(): String {
        return "read_file($path)"
    }
}

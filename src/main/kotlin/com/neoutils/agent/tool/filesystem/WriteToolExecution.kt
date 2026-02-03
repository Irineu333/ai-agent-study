package com.neoutils.agent.tool.filesystem

import com.neoutils.agent.core.domain.tool.ToolExecution
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.writeText

class WriteToolExecution(
    private val path: Path,
    private val content: String,
    private val overwrite: Boolean
) : ToolExecution {
    override suspend fun invoke(): Result<String> {
        if (path.exists() && !overwrite) {
            return Result.failure(Exception("File already exists: $path (use overwrite: true to replace)"))
        }

        path.parent?.createDirectories()
        path.writeText(content)

        return Result.success("File written successfully: $path")
    }

    override fun toString(): String {
        return listOfNotNull(
            path.toString(),
            "overwrite".takeIf { overwrite },
        ).joinToString(
            prefix = "write_file(",
            postfix = ")"
        )
    }
}

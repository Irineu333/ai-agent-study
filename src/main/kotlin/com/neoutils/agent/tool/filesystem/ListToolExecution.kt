package com.neoutils.agent.tool.filesystem

import com.neoutils.agent.core.domain.tool.ToolExecution
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.*

class ListToolExecution(
    private val path: Path,
    private val recursive: Boolean,
    private val showHidden: Boolean
) : ToolExecution {
    override suspend fun invoke(): Result<String> {
        if (!path.exists()) {
            return Result.failure(Exception("Path does not exist: $path"))
        }

        if (!path.isDirectory()) {
            return Result.failure(Exception("Path is not a directory: $path"))
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

        return Result.success(output.ifEmpty { "Directory is empty" })
    }

    override fun toString(): String {
        return listOfNotNull(
            path.toString(),
            "recursive".takeIf { recursive },
            "showHidden".takeIf { showHidden },
        ).joinToString(
            prefix = "list(",
            postfix = ")"
        )
    }
}

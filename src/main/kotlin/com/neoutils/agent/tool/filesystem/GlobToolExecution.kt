package com.neoutils.agent.tool.filesystem

import com.neoutils.agent.core.domain.tool.ToolExecution
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.streams.toList

class GlobToolExecution(
    private val path: Path,
    private val pattern: String
) : ToolExecution {
    override suspend fun invoke(): Result<String> {

        if (!path.exists()) {
            return Result.failure(Exception("Directory does not exist: $path"))
        }

        if (!path.isDirectory()) {
            return Result.failure(Exception("Path is not a directory: $path"))
        }

        val matcher = FileSystems.getDefault().getPathMatcher("glob:$pattern")

        val matches = Files.walk(path).use { stream ->
            stream
                .filter { it.isRegularFile() }
                .filter { matcher.matches(path.relativize(it)) }
                .toList()
                .sortedWith(compareBy({ !it.isDirectory() }, { it.toString() }))
                .map { formatEntry(it) }
        }

        if (matches.isEmpty()) {
            return Result.success("No files matching pattern '$pattern'")
        }

        return Result.success(matches.joinToString("\n"))
    }

    private fun formatEntry(entry: Path): String {
        val suffix = if (entry.isDirectory()) "/" else ""
        return "${path.relativize(entry)}$suffix"
    }

    override fun toString(): String {
        return "glob($path, $pattern)"
    }
}

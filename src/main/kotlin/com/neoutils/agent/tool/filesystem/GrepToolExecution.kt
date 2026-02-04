package com.neoutils.agent.tool.filesystem

import com.neoutils.agent.core.domain.tool.ToolExecution
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.readLines
import kotlin.streams.toList

class GrepToolExecution(
    private val path: Path,
    private val pattern: String,
    private val glob: String?
) : ToolExecution {
    override suspend fun invoke(): Result<String> {

        if (!path.exists()) {
            return Result.failure(Exception("Path does not exist: $path"))
        }

        val regex = try {
            Regex(pattern)
        } catch (e: Exception) {
            return Result.failure(Exception("Invalid regex pattern: ${e.message}"))
        }

        val files = if (path.isRegularFile()) {
            listOf(path)
        } else {
            collectFiles()
        }

        val results = mutableListOf<String>()

        for (file in files) {
            try {
                val lines = file.readLines()
                lines.forEachIndexed { index, line ->
                    if (regex.containsMatchIn(line)) {
                        val lineNumber = index + 1
                        val relativePath = if (path.isDirectory()) {
                            path.relativize(file)
                        } else {
                            file.fileName
                        }
                        results.add("$relativePath:$lineNumber:$line")
                    }
                }
            } catch (e: Exception) {
                // Skip files that can't be read (binary, permissions, etc.)
            }
        }

        if (results.isEmpty()) {
            return Result.success("No matches found for pattern '$pattern'")
        }

        return Result.success(results.joinToString("\n"))
    }

    private fun collectFiles(): List<Path> {
        val globMatcher = glob?.let {
            FileSystems.getDefault().getPathMatcher("glob:$it")
        }

        return Files.walk(path).use { stream ->
            stream
                .filter { it.isRegularFile() }
                .filter { file ->
                    globMatcher?.matches(file.fileName) ?: true
                }
                .toList()
                .sortedBy { it.toString() }
        }
    }

    override fun toString(): String {
        return listOfNotNull(
            path.toString(),
            pattern,
            glob?.let { "glob=$it" }
        ).joinToString(", ", prefix = "grep(", postfix = ")")
    }
}

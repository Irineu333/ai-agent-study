package com.neoutils.agent.tool.filesystem

import com.github.difflib.DiffUtils
import com.github.difflib.UnifiedDiffUtils
import com.neoutils.agent.core.domain.tool.ToolExecution
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
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

        if (path.exists()) {
            val oldContent = path.readText()
            path.writeText(content)

            return Result.success(
                generateUnifiedDiff(
                    oldLines = oldContent.lines(),
                    newLines = content.lines(),
                )
            )
        }

        path.parent?.createDirectories()
        path.writeText(content)

        return Result.success(
            generateUnifiedDiff(
                oldLines = emptyList(),
                newLines = content.lines(),
            )
        )
    }

    private fun generateUnifiedDiff(
        oldLines: List<String>,
        newLines: List<String>
    ): String {
        val patch = DiffUtils.diff(oldLines, newLines)

        val unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(
            null,
            null,
            oldLines,
            patch,
            3
        )
        return unifiedDiff.drop(2).joinToString("\n") { line ->
            when {
                line.startsWith("@@") -> "\u001B[90m$line\u001B[0m"
                line.startsWith("-") -> "\u001B[31m$line\u001B[0m"
                line.startsWith("+") -> "\u001B[32m$line\u001B[0m"
                else -> line
            }
        }.ifEmpty { "No changes" }
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

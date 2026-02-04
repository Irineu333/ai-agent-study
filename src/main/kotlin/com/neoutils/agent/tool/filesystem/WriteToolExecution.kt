package com.neoutils.agent.tool.filesystem

import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextColors.Companion.gray
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
                line.startsWith("@@") -> gray(0.5)(line)
                line.startsWith("-") -> red(line)
                line.startsWith("+") -> green(line)
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

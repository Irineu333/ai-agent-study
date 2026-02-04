package com.neoutils.agent.tool.filesystem

import com.github.ajalt.mordant.rendering.TextColors.Companion.gray
import com.github.ajalt.mordant.rendering.TextColors.green
import com.github.ajalt.mordant.rendering.TextColors.red
import com.github.difflib.DiffUtils
import com.github.difflib.UnifiedDiffUtils
import com.neoutils.agent.core.domain.tool.ToolExecution
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText
import kotlin.io.path.writeText

class EditToolExecution(
    private val path: Path,
    private val oldString: String,
    private val newString: String,
    private val replaceAll: Boolean
) : ToolExecution {
    override suspend fun invoke(): Result<String> {

        if (!path.exists()) {
            return Result.failure(Exception("File does not exist: $path"))
        }

        if (!path.isRegularFile()) {
            return Result.failure(Exception("Path is not a regular file: $path"))
        }

        val oldContent = path.readText()
        val occurrences = oldContent.windowed(oldString.length) { it.toString() }.count { it == oldString }


        if (occurrences == 0) {
            val preview = oldString.take(50) + if (oldString.length > 50) "..." else ""
            return Result.failure(Exception("String not found in file: '$preview'"))
        }

        if (occurrences > 1 && !replaceAll) {
            return Result.failure(
                Exception(
                    "Found $occurrences occurrences of the string. " +
                            "Provide more surrounding context to make it unique, or use replace_all=true"
                )
            )
        }

        val newContent = if (replaceAll) {
            oldContent.replace(oldString, newString)
        } else {
            oldContent.replaceFirst(oldString, newString)
        }

        if (oldContent == newContent) {
            return Result.success("No changes made (old_string equals new_string)")
        }

        path.writeText(newContent)

        return Result.success(
            generateUnifiedDiff(
                oldLines = oldContent.lines(),
                newLines = newContent.lines(),
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
            "replace_all".takeIf { replaceAll },
        ).joinToString(
            prefix = "edit_file(",
            postfix = ")"
        )
    }
}

package com.neoutils.agent.core.presentation

import com.googlecode.lanterna.SGR
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import kotlinx.coroutines.*
import java.io.Closeable

class TerminalUI : Closeable {

    private val terminal: Terminal = DefaultTerminalFactory().createTerminal()
    private var loadingJob: Job? = null

    fun print(text: String, color: TextColor? = null) {
        color?.let { terminal.setForegroundColor(it) }
        text.forEach { char ->
            terminal.putCharacter(char)
        }
        color?.let { terminal.resetColorAndSGR() }
        terminal.flush()
    }

    fun println(text: String = "", color: TextColor? = null) {
        print(text, color)
        terminal.putCharacter('\n')
        terminal.flush()
    }

    fun printStyled(text: String, color: TextColor, vararg sgr: SGR) {
        terminal.setForegroundColor(color)
        sgr.forEach { terminal.enableSGR(it) }
        text.forEach { char ->
            terminal.putCharacter(char)
        }
        terminal.resetColorAndSGR()
        terminal.flush()
    }

    fun prompt(promptText: String = "> "): String? {
        print(promptText)
        terminal.flush()

        val inputBuilder = StringBuilder()
        while (true) {
            val keyStroke = terminal.readInput()

            when (keyStroke.keyType) {
                com.googlecode.lanterna.input.KeyType.Enter -> {
                    terminal.putCharacter('\n')
                    terminal.flush()
                    return inputBuilder.toString()
                }
                com.googlecode.lanterna.input.KeyType.Escape,
                com.googlecode.lanterna.input.KeyType.EOF -> {
                    return null
                }
                com.googlecode.lanterna.input.KeyType.Backspace -> {
                    if (inputBuilder.isNotEmpty()) {
                        inputBuilder.deleteCharAt(inputBuilder.length - 1)
                        val cursorPos = terminal.cursorPosition
                        if (cursorPos.column > promptText.length) {
                            terminal.setCursorPosition(cursorPos.column - 1, cursorPos.row)
                            terminal.putCharacter(' ')
                            terminal.setCursorPosition(cursorPos.column - 1, cursorPos.row)
                            terminal.flush()
                        }
                    }
                }
                com.googlecode.lanterna.input.KeyType.Character -> {
                    val char = keyStroke.character
                    if (char != null) {
                        inputBuilder.append(char)
                        terminal.putCharacter(char)
                        terminal.flush()
                    }
                }
                else -> Unit
            }
        }
    }

    context(scope: CoroutineScope)
    fun loading(): Job {
        val spinnerChars = listOf("⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏")
        var index = 0

        loadingJob = scope.launch {
            try {
                while (isActive) {
                    val cursorPos = terminal.cursorPosition
                    print(spinnerChars[index], TextColor.ANSI.CYAN)
                    delay(80)
                    terminal.setCursorPosition(cursorPos.column, cursorPos.row)
                    terminal.putCharacter(' ')
                    terminal.setCursorPosition(cursorPos.column, cursorPos.row)
                    terminal.flush()
                    index = (index + 1) % spinnerChars.size
                }
            } finally {
                val cursorPos = terminal.cursorPosition
                terminal.setCursorPosition(cursorPos.column, cursorPos.row)
                terminal.putCharacter(' ')
                terminal.setCursorPosition(cursorPos.column, cursorPos.row)
                terminal.flush()
            }
        }

        return loadingJob!!
    }

    override fun close() {
        loadingJob?.cancel()
        terminal.close()
    }
}

object TextColors {
    val gray: (Float) -> TextColor = { opacity ->
        val value = (opacity * 255).toInt()
        TextColor.RGB(value, value, value)
    }

    val red: TextColor = TextColor.ANSI.RED
    val green: TextColor = TextColor.ANSI.GREEN
    val blue: TextColor = TextColor.ANSI.BLUE
    val cyan: TextColor = TextColor.ANSI.CYAN
    val yellow: TextColor = TextColor.ANSI.YELLOW
    val white: TextColor = TextColor.ANSI.WHITE
    val black: TextColor = TextColor.ANSI.BLACK
}

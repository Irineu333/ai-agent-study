package com.neoutils.agent

import com.neoutils.agent.core.presentation.TerminalUI
import com.neoutils.agent.di.appModule
import com.neoutils.agent.feature.chat.presentation.Chat
import com.neoutils.agent.feature.generate.presentation.Generate
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.getKoin

fun main(args: Array<String>) {
    startKoin {
        modules(appModule)
    }

    val terminal = getKoin().get<TerminalUI>()

    terminal.use {
        when {
            args.isEmpty() || args[0] == "help" -> printHelp(terminal)
            args[0] == "chat" -> {
                val model = extractOption(args, "--model", "-m")
                if (model == null) {
                    terminal.println("Error: --model is required", com.googlecode.lanterna.TextColor.ANSI.RED)
                    return@use
                }
                Chat(terminal, model).run()
            }
            args[0] == "generate" -> {
                val model = extractOption(args, "--model", "-m")
                val prompt = args.drop(1).filterNot { it.startsWith("-") || it == model }.joinToString(" ")
                if (model == null) {
                    terminal.println("Error: --model is required", com.googlecode.lanterna.TextColor.ANSI.RED)
                    return@use
                }
                if (prompt.isEmpty()) {
                    terminal.println("Error: prompt is required", com.googlecode.lanterna.TextColor.ANSI.RED)
                    return@use
                }
                Generate(terminal, prompt, model).run()
            }
            else -> {
                terminal.println("Unknown command: ${args[0]}", com.googlecode.lanterna.TextColor.ANSI.RED)
                printHelp(terminal)
            }
        }
    }
}

private fun printHelp(terminal: TerminalUI) {
    terminal.println("Usage: agent <command> [options]")
    terminal.println()
    terminal.println("Commands:")
    terminal.println("  chat      Interactive chat mode")
    terminal.println("  generate  One-shot generation mode")
    terminal.println()
    terminal.println("Options:")
    terminal.println("  --model, -m  Model name (required)")
}

private fun extractOption(args: Array<String>, vararg names: String): String? {
    for ((index, arg) in args.withIndex()) {
        if (arg in names && index + 1 < args.size) {
            return args[index + 1]
        }
        for (name in names) {
            if (arg.startsWith("$name=")) {
                return arg.substringAfter("=")
            }
        }
    }
    return null
}

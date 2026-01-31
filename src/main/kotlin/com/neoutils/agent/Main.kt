package com.neoutils.agent

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.obj
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.neoutils.agent.di.appModule
import com.neoutils.agent.domain.model.AgentConfig
import com.neoutils.agent.feature.chat.presentation.Chat
import com.neoutils.agent.feature.generate.presentation.Generate
import org.koin.core.context.startKoin

class Agent : CliktCommand(name = "agent") {

    private val model by option("--model", "-m").required()

    override fun run() {
        currentContext.obj = AgentConfig(model = model)
    }
}

fun main(args: Array<String>) {
    startKoin {
        modules(appModule)
    }

    Agent()
        .subcommands(Generate(), Chat())
        .main(args)
}

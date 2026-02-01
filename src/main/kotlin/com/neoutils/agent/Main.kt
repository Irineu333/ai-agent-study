package com.neoutils.agent

import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.neoutils.agent.di.appModule
import com.neoutils.agent.feature.chat.presentation.Chat
import com.neoutils.agent.feature.generate.presentation.Generate
import org.koin.core.context.startKoin

fun main(args: Array<String>) {
    startKoin {
        modules(appModule)
    }

    Agent()
        .subcommands(Generate(), Chat())
        .main(args)
}

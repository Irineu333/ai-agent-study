package com.neoutils.agent

import com.github.ajalt.clikt.core.main
import com.neoutils.agent.di.appModule
import org.koin.core.context.startKoin

fun main(args: Array<String>) {
    startKoin {
        modules(appModule)
    }

    ChatBot().main(args)
}

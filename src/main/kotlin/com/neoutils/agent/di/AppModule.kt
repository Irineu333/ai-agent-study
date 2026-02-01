package com.neoutils.agent.di

import com.neoutils.agent.data.client.OllamaClient
import com.neoutils.agent.domain.service.ToolService
import com.neoutils.agent.domain.service.ToolServiceImpl
import com.neoutils.agent.domain.tool.ShellTool
import com.neoutils.agent.feature.chat.data.repository.ChatRepositoryImpl
import com.neoutils.agent.feature.chat.domain.repository.ChatRepository
import com.neoutils.agent.feature.generate.data.repository.GenerateRepositoryImpl
import com.neoutils.agent.feature.generate.domain.repository.GenerateRepository
import org.koin.dsl.module

val appModule = module {
    single { OllamaClient() }

    single<GenerateRepository> { GenerateRepositoryImpl(client = get()) }
    single<ChatRepository> { ChatRepositoryImpl(client = get()) }

    single<ToolService> {
        ToolServiceImpl(
            tools = listOf(
                ShellTool(),
            )
        )
    }
}

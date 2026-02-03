package com.neoutils.agent.di

import com.neoutils.agent.core.data.client.OllamaClient
import com.neoutils.agent.feature.chat.data.repository.ChatRepositoryImpl
import com.neoutils.agent.feature.chat.domain.repository.ChatRepository
import com.neoutils.agent.feature.generate.data.repository.GenerateRepositoryImpl
import com.neoutils.agent.feature.generate.domain.repository.GenerateRepository
import com.neoutils.agent.tool.di.toolModule
import org.koin.dsl.module

val appModule = module {
    includes(toolModule)

    single { OllamaClient() }

    single<GenerateRepository> { GenerateRepositoryImpl(client = get()) }
    single<ChatRepository> { ChatRepositoryImpl(client = get()) }
}

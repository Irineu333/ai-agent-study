package com.neoutils.agent.di

import com.neoutils.agent.data.client.OllamaClient
import com.neoutils.agent.data.repository.ChatRepositoryImpl
import com.neoutils.agent.domain.repository.ChatRepository
import org.koin.dsl.module

val appModule = module {
    single { OllamaClient() }
    single<ChatRepository> { ChatRepositoryImpl(client = get()) }
}

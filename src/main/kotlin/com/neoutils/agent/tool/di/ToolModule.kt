package com.neoutils.agent.tool.di

import com.neoutils.agent.core.data.service.ToolServiceImpl
import com.neoutils.agent.core.domain.service.ToolService
import com.neoutils.agent.tool.filesystem.EditToolDefinition
import com.neoutils.agent.tool.filesystem.ListToolDefinition
import com.neoutils.agent.tool.filesystem.ReadToolDefinition
import com.neoutils.agent.tool.filesystem.WriteToolDefinition
import com.neoutils.agent.tool.shell.ShellToolDefinition
import org.koin.dsl.module

val toolModule = module {
    single<ToolService> {
        ToolServiceImpl(
            definitions = listOf(
                ShellToolDefinition(),
                ListToolDefinition(),
                ReadToolDefinition(),
                WriteToolDefinition(),
                EditToolDefinition(),
            )
        )
    }
}

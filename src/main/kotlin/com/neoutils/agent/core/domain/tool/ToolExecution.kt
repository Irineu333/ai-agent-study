package com.neoutils.agent.core.domain.tool

interface ToolExecution {
    suspend operator fun invoke(): Result<String>
}

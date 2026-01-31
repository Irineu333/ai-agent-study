package com.neoutils.agent.feature.chat.domain.model

import com.neoutils.agent.domain.model.ToolAction

data class CallToolAction(
    val toolAction: ToolAction,
    val arguments: Map<String, String>
)
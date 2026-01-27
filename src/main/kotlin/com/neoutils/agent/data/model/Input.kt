package com.neoutils.agent.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Input(
    @SerialName("prompt")
    val prompt: String,
    @SerialName("model")
    val model: String,
    @SerialName("stream")
    val stream: Boolean,
)
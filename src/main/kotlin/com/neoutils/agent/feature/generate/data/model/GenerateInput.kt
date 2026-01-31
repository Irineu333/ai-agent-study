package com.neoutils.agent.feature.generate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerateInput(
    @SerialName("prompt")
    val prompt: String,
    @SerialName("model")
    val model: String,
    @SerialName("stream")
    val stream: Boolean,
)

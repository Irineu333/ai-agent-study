package com.neoutils.agent.feature.generate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerateOutput(
    @SerialName("model")
    val model: String,
    @SerialName("response")
    val response: String = "",
    @SerialName("thinking")
    val thinking: String = "",
    @SerialName("done")
    val done: Boolean = false,
)

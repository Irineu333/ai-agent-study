package com.neoutils.agent.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Output(
    @SerialName("model")
    val model: String,
    @SerialName("response")
    val response: String = "",
    @SerialName("thinking")
    val thinking: String = "",
    @SerialName("done")
    val done: Boolean = false,
)

package com.neoutils.agent.core.domain.extension

inline fun <T, R> Result<T>.flatMap(block: (T) -> Result<R>): Result<R> {
    return fold(
        onSuccess = block,
        onFailure = {
            Result.failure(it)
        }
    )
}
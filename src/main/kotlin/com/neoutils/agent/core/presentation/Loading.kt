package com.neoutils.agent.core.presentation

import com.github.ajalt.mordant.animation.coroutines.animateInCoroutine
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.widgets.Spinner
import com.github.ajalt.mordant.widgets.progress.progressBarLayout
import com.github.ajalt.mordant.widgets.progress.spinner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

context(scope: CoroutineScope)
fun Terminal.loading(): Job {
    val animator = progressBarLayout {
        spinner(Spinner.Dots())
    }.animateInCoroutine(this)

    return scope.launch {
        try {
            animator.execute()
        } finally {
            animator.clear()
        }
    }
}

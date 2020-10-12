package com.arsfutura.sampleappble.impl.util

import com.arsfutura.sampleappble.impl.Command
import kotlinx.coroutines.CompletableDeferred

data class DeferredCommand(
    val command: Command,
    val completableDeferred: CompletableDeferred<Unit>
)
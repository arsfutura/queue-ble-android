package com.arsfutura.sampleappble.impl

import com.arsfutura.sampleappble.impl.util.Result

interface CommandQueue {

    suspend fun enqueue(command: Command): Result<Unit>

    suspend fun close()
}
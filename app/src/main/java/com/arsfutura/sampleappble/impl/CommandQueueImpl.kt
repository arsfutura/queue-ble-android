package com.arsfutura.sampleappble.impl

import android.util.Log
import com.arsfutura.sampleappble.impl.util.DeferredCommand
import com.arsfutura.sampleappble.impl.util.Result
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class CommandQueueImpl(coroutineScope: CoroutineScope) : CommandQueue {

    companion object {
        private val TAG: String = CommandQueueImpl::class.java.simpleName
    }

    private val queue = Channel<DeferredCommand>()

    init {
        coroutineScope.launch {
            queue.consumeEach {
                Log.d(TAG, "Consume ${it.command} from queue")
                try {
                    it.command.execute()
                    it.completableDeferred.complete(Unit)
                } catch (e: Exception) {
                    it.completableDeferred.completeExceptionally(e)
                }
            }
        }
    }

    override suspend fun enqueue(command: Command): Result<Unit> {
        Log.d(TAG, "Send $command to queue")
        val deferredCommand = DeferredCommand(command, CompletableDeferred())

        queue.send(deferredCommand)

        return try {
            deferredCommand.completableDeferred.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.error(e)
        }
    }

    override suspend fun close() {
        queue.close()
        Log.i(TAG, "Channel closed!")
    }
}
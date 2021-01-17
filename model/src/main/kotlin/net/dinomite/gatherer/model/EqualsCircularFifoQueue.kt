package net.dinomite.gatherer.model

import org.apache.commons.collections4.queue.CircularFifoQueue
import java.util.*

class EqualsCircularFifoQueue<T>(private val queue: CircularFifoQueue<T>) : Queue<T> by queue {
    constructor(size: Int) : this(CircularFifoQueue<T>(size))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EqualsCircularFifoQueue<*>

        if (queue.size != other.queue.size) return false
        queue.forEachIndexed { index, observation ->
            if (observation != other.queue[index]) return false
        }

        return true
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + queue.first().hashCode()
        result = prime * result + queue.last().hashCode()
        return result
    }
}

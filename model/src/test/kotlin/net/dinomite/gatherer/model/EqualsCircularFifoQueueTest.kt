package net.dinomite.gatherer.model

import org.apache.commons.collections4.queue.CircularFifoQueue
import kotlin.test.Test
import kotlin.test.assertEquals

internal class EqualsCircularFifoQueueTest {
    @Test
    fun equals() {
        val first = EqualsCircularFifoQueue<Int>(CircularFifoQueue(10)).apply { add(7) }
        val second = EqualsCircularFifoQueue<Int>(CircularFifoQueue(10)).apply { add(7) }
        assertEquals(first, second)
    }

    @Test
    fun hashcode() {
        val first = EqualsCircularFifoQueue<Int>(CircularFifoQueue(10)).apply { add(7) }
        val second = EqualsCircularFifoQueue<Int>(CircularFifoQueue(10)).apply { add(7) }
        assertEquals(first.hashCode(), second.hashCode())
    }
}

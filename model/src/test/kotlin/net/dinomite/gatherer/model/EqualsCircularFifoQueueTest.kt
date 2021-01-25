package net.dinomite.gatherer.model

import org.apache.commons.collections4.queue.CircularFifoQueue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class EqualsCircularFifoQueueTest {
    @Test
    fun equals_SameObject() {
        val queue = EqualsCircularFifoQueue<Int>(CircularFifoQueue(10)).apply { add(7) }
        assertEquals(queue, queue)
    }

    @Test
    fun equals_EqualObjects() {
        val first = EqualsCircularFifoQueue<Int>(CircularFifoQueue(10)).apply { add(7) }
        val second = EqualsCircularFifoQueue<Int>(CircularFifoQueue(10)).apply { add(7) }
        assertEquals(first, second)
    }

    @Test
    fun equals_DifferentSize() {
        val first = EqualsCircularFifoQueue<Int>(CircularFifoQueue(10)).apply { add(7); add(3) }
        val second = EqualsCircularFifoQueue<Int>(CircularFifoQueue(10)).apply { add(9) }
        assertNotEquals(first, second)
    }

    @Test
    fun equals_DifferentContents() {
        val first = EqualsCircularFifoQueue<Int>(CircularFifoQueue(10)).apply { add(7) }
        val second = EqualsCircularFifoQueue<Int>(CircularFifoQueue(10)).apply { add(9) }
        assertNotEquals(first, second)
    }

    @Test
    fun hashcode_SameObject() {
        val first = EqualsCircularFifoQueue<Int>(CircularFifoQueue(10)).apply { add(7) }
        assertEquals(first.hashCode(), first.hashCode())
    }

    @Test
    fun hashcode_EqualObjects() {
        val first = EqualsCircularFifoQueue<Int>(CircularFifoQueue(10)).apply { add(7); add(10) }
        val second = EqualsCircularFifoQueue<Int>(CircularFifoQueue(10)).apply { add(7); add(10) }
        assertEquals(first.hashCode(), second.hashCode())
    }

    @Test
    fun hashcode_DifferentObjects() {
        val first = EqualsCircularFifoQueue<Int>(CircularFifoQueue(10)).apply { add(7); add(3) }
        val second = EqualsCircularFifoQueue<Int>(CircularFifoQueue(10)).apply { add(9); add(1) }
        assertNotEquals(first.hashCode(), second.hashCode())
    }
}

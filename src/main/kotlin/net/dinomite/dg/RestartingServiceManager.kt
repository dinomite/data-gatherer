@file:Suppress("UnstableApiUsage")

package net.dinomite.dg

import com.google.common.util.concurrent.MoreExecutors
import com.google.common.util.concurrent.Service
import kotlinx.coroutines.Runnable
import org.slf4j.LoggerFactory
import java.util.concurrent.locks.ReentrantLock

interface ServiceFactory {
    fun makeService(): Service
}

class RestartingServiceManager(private val name: String, private val serviceFactory: ServiceFactory) : Runnable {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.name)
    }

    private val lock = ReentrantLock()
    private val failed = lock.newCondition()
    private val listener = object : Service.Listener() {
        override fun failed(from: Service.State, failure: Throwable) = try {
            logger.warn("$name failed from $from")
            lock.lock()
            failed.signal()
        } finally {
            lock.unlock()
        }
    }

    override fun run() {
        lock.lock()
        while (true) {
            logger.info("Starting $name")
            serviceFactory.makeService().apply {
                addListener(listener, MoreExecutors.directExecutor())
                startAsync()
            }
            failed.await()
        }
    }

}
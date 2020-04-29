@file:Suppress("UnstableApiUsage")

package net.dinomite.dg

import com.google.common.util.concurrent.AbstractScheduledService
import com.google.common.util.concurrent.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.Executors

private val logger: Logger = LoggerFactory.getLogger("RestartingServiceManagerTest")

fun main(args: Array<String>) {
    val executor = Executors.newCachedThreadPool()
    executor.submit(RestartingServiceManager("println", object : ServiceFactory {
        override fun makeService(): Service = printlnService()
    }))
    executor.submit(RestartingServiceManager("crashingPrintln", object : ServiceFactory {
        override fun makeService(): Service = crashingPrintlnService()
    }))
}

class PrintlnService(private val iteration: (Int) -> Unit) : AbstractScheduledService() {
    private var number = 0
    override fun scheduler(): Scheduler = Scheduler.newFixedRateSchedule(Duration.ZERO, Duration.ofSeconds(2))
    override fun runOneIteration() {
        iteration.invoke(number)
        number++
    }
}

private fun crashingPrintlnService(): AbstractScheduledService = PrintlnService { number ->
    if (number > 2) {
        throw RuntimeException("foobar")
    }
    logger.info("Crashable $number")
}

private fun printlnService(): AbstractScheduledService = PrintlnService { number ->
    logger.info("No crashes $number")
}

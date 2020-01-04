@file:Suppress("UnstableApiUsage")

package net.dinomite.dg

import com.google.common.util.concurrent.Service
import kotlinx.coroutines.runBlocking
import net.dinomite.dg.services.TimeService
import java.time.Duration

class App {
    private val services = mutableListOf<Service>()

    fun start() {
        services.add(TimeService(Duration.ofSeconds(2)))

        services.forEach {
            it.startAsync()
        }
    }

    fun isRunning(): Boolean {
        val first = services.firstOrNull {
            it.isRunning
        }

        return first != null
    }

    fun stop() {
        services.forEach {
            it.stopAsync()
        }
    }
}

fun main(args: Array<String>) {
    val app = App()
    app.start()

    Runtime.getRuntime().addShutdownHook(object: Thread() {
        override fun run() = runBlocking {
            println("Shutting down")
            app.stop()
            println("Done")
        }
    })

    while (app.isRunning()) {
        Thread.sleep(1000)
    }
}

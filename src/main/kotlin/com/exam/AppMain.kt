package com.exam

import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import kotlin.system.measureTimeMillis

@SpringBootApplication
class AppMain

private val log = LoggerFactory.getLogger(AppMain::class.java) as Logger

fun main(args: Array<String>) {
    log.info("========== AppMain Run   START ==========")
    val elapsed = measureTimeMillis {
        runApplication<AppMain>(*args)
    }
    log.info("========== AppMain Run     END ========== : $elapsed ms")
}

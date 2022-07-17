package com.exam

import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.env.Environment
import kotlin.system.measureTimeMillis

@SpringBootApplication
class AppMain

private val log = LoggerFactory.getLogger(AppMain::class.java) as Logger

fun main(args: Array<String>) {
    log.info("========== AppMain Run   START ==========")
    val elapsed = measureTimeMillis {
        System.setProperty("java.net.preferIPv4Stack", "true")

        // Spring Boot Run
        val app = runApplication<AppMain>(*args){
            setBannerMode(Banner.Mode.OFF)
        }
        // Check Profile
        log.info("========== Check Profile START ==========")
        val env = app.getBean("environment") as Environment
        env.activeProfiles.toList()
            .forEach { log.info(it) }
        log.info("========== Check Profile   END ========== : activate profile count: ${env.activeProfiles.count()}")
    }
    log.info("========== AppMain Run     END ========== : $elapsed ms")
}

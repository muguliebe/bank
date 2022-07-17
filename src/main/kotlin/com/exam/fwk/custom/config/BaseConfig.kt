package com.exam.fwk.custom.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableScheduling
@EnableAsync
@EnableTransactionManagement(proxyTargetClass = true)
class BaseConfig {

    /**
     * 쓰레드 개수 & 이름
     */
    @Primary
    @Bean(name = ["threadPoolTaskExecutor"])
    fun taskExecutor(): TaskExecutor {
        val taskExecutor = ThreadPoolTaskExecutor()
        taskExecutor.corePoolSize = 100
        taskExecutor.queueCapacity = 1000
        taskExecutor.maxPoolSize = 200
        taskExecutor.setThreadNamePrefix("exe-")
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true)
        taskExecutor.setAwaitTerminationSeconds(20)
        return taskExecutor
    }

}

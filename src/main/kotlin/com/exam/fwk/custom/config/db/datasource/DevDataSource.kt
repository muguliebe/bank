package com.exam.fwk.custom.config.db.datasource

import ch.qos.logback.classic.Logger
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import javax.sql.DataSource

/**
 * 개발계 DB 사용
 */

@Profile("dev")
@Configuration
class DevDataSource {

    companion object {
        private val log = LoggerFactory.getLogger(DevDataSource::class.java) as Logger
    }


    @Primary
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean(name = ["firstDatasource"])
    fun firstDataSource(
        @Value("\${db.datasource.driver-class-name}") driverClassName: String,
        @Value("\${db.datasource.username}") username: String,
        @Value("\${db.datasource.password}") password: String,
        @Value("\${db.datasource.url}") jdbcUrl: String,
    ): DataSource {
        log.info("=============== firstDataSource Setting Start =============== ")

        val ds = HikariDataSource()
        ds.jdbcUrl = jdbcUrl
        ds.username = username
        ds.password = password
        ds.minimumIdle = 5
        ds.maximumPoolSize = 100
        ds.idleTimeout = 3000
        ds.connectionInitSql = "set time zone 'Asia/Seoul'"

        log.info("=============== firstDataSource Setting End   =============== ")

        return ds
    }

}

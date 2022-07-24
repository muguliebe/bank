package com.exam.fwk.custom.config.db.datasource

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import javax.sql.DataSource

/**
 * 로컬에서 embedded postgresql 로 동작
 */
@Profile("local")
@Configuration
class EmbeddedDatasource {

    @Primary
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean(name = ["firstDatasource"])
    fun memoryPg(): DataSource {
        return EmbeddedPostgres.builder()
            .setServerConfig("timezone", "Asia/Seoul")
            .start().postgresDatabase
    }

}

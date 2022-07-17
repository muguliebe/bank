package com.exam.fwk.custom.config.db.datasource

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import javax.sql.DataSource

@Configuration
class EmbeddedDataSource {

    @Primary
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean(name = ["embeddedDatasource"])
    fun memoryPg(): DataSource {
        return EmbeddedPostgres.builder()
                .setServerConfig("timezone", "Asia/Seoul")
                .start().postgresDatabase
    }

}

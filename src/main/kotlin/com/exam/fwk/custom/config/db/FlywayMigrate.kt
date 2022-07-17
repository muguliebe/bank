package com.exam.fwk.custom.config.db

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.sql.DataSource

@Component
class FlywayMigrate {

    @Autowired lateinit var ds: DataSource

    @PostConstruct
    fun flyway() {
        val flyway = Flyway.configure()
                .dataSource(ds)
                .locations("db/migration")
                .load()
        flyway.migrate()
    }
}

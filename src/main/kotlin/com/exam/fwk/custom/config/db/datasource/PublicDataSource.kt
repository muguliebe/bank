package com.exam.fwk.custom.config.db.datasource

import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.transaction.ChainedTransactionManager
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.Database
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.exam.bank.repo.jpa"],
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "publicTransactionManager"
)
class PublicDataSource {
    companion object {
        private val log = LoggerFactory.getLogger(PublicDataSource::class.java) as Logger
    }

//    @Bean(name = ["emPublicDataSource"])
//    @DependsOn("FirstDatasource")
//    @Order(Ordered.LOWEST_PRECEDENCE)
//    fun dataSource(
//        @Value("\${spring.datasource.driver-class-name}") driverClassName: String,
//        @Value("\${spring.datasource.username}") username: String,
//        @Value("\${spring.datasource.password}") password: String,
//        @Value("\${spring.datasource.url}") jdbcUrl: String,
//    ): DataSource {
//        log.info("=============== JPA Public DataSource Setting Start =============== ")
//
//        val ds = HikariDataSource()
//        ds.jdbcUrl = jdbcUrl
//        ds.username = username
//        ds.password = password
//        ds.minimumIdle = 5
//        ds.maximumPoolSize = 100
//        ds.idleTimeout = 3000
//        ds.connectionInitSql = "set time zone 'Asia/Seoul'"
//
//        log.info("=============== JPA Public DataSource Setting End   =============== ")
//
//        return ds
//    }

    @Bean(name = ["entityManagerFactory"])
    @Order(Ordered.LOWEST_PRECEDENCE)
    fun entityManagerFactory(
        @Qualifier("firstDatasource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {

        val vendorAdapter = HibernateJpaVendorAdapter()
        vendorAdapter.setDatabase(Database.POSTGRESQL)
        vendorAdapter.setGenerateDdl(true)

        val properties: HashMap<String, Any> = hashMapOf()
        properties["hibernate.default_schema"] = "public"
        properties["hibernate.hbm2ddl.auto"] = "none"
        properties["hibernate.ddl-auto"] = "none"
        properties["hibernate.dialect"] = "org.hibernate.dialect.PostgreSQLDialect"
        properties["hibernate.physical_naming_strategy"] =
            "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy"
        properties["hibernate.cache.use_second_level_cache"] = false
        properties["hibernate.cache.use_query_cache"] = false
        properties["hibernate.show_sql"] = false
        properties["javax.persistence.validation.mode"] = "none"

        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = dataSource
        em.jpaVendorAdapter = vendorAdapter
        em.setPackagesToScan("com.exam.bank.entity")
        em.setJpaPropertyMap(properties)

        return em
    }

    @Bean(name = ["publicTransactionManager"])
    @Order(Ordered.LOWEST_PRECEDENCE)
    fun transactionManager(
        @Qualifier("entityManagerFactory") entityManagerFactory: EntityManagerFactory,
        @Qualifier("firstDatasource") dataSource: DataSource
    ): PlatformTransactionManager {
        val jtm = JpaTransactionManager(entityManagerFactory)
        val dstm = DataSourceTransactionManager()
        dstm.dataSource = dataSource

        val ctm = ChainedTransactionManager(jtm, dstm)
        return ctm
    }


}

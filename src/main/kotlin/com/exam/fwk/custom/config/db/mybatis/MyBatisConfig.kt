package com.exam.fwk.custom.config.db.mybatis

import ch.qos.logback.classic.Logger
import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import java.io.IOException
import javax.sql.DataSource

@Configuration
abstract class MyBatisConfig {
    companion object {
        private val log = LoggerFactory.getLogger(MyBatisConfig::class.java) as Logger
        const val typeAliasesPackage = "com.exam.bank.model"
        const val configLocation = "classpath:mybatis/mybatis-config.xml"
        const val mapperLocation = "classpath:mybatis/mapper/**/*.xml"
    }


    @Throws(IOException::class)
    fun configureSqlSessionFactory(sessionFactoryBean: SqlSessionFactoryBean, dataSource: DataSource) {
        log.info("=============== configSqlSessionFactory Start ===============")
        val pathResolver = PathMatchingResourcePatternResolver()
        sessionFactoryBean.setDataSource(dataSource)
        sessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage)
        sessionFactoryBean.setConfigLocation(pathResolver.getResource(configLocation))
        sessionFactoryBean.setMapperLocations(*pathResolver.getResources(mapperLocation))
        sessionFactoryBean.vfs = SpringBootVFS::class.java
        log.info("=============== configSqlSessionFactory End   ===============")
    }
}

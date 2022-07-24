package com.exam.fwk.custom.config.db.mybatis

import ch.qos.logback.classic.Logger
import org.apache.ibatis.session.SqlSessionFactory
import org.mybatis.spring.SqlSessionFactoryBean
import org.mybatis.spring.annotation.MapperScan
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.io.IOException
import javax.sql.DataSource

@Configuration
@MapperScan(basePackages = ["com.exam.bank.repo.mybatis"]
        , sqlSessionFactoryRef = "embeddedSqlSessionFactory"
)
class MyBatisEmbedded : MyBatisConfig() {

    companion object {
        val log = LoggerFactory.getLogger(MyBatisEmbedded::class.java) as Logger
    }

    @Bean
    @Primary
    @Throws(IOException::class)
    fun embeddedSqlSessionFactory(@Qualifier("firstDatasource") dataSource: DataSource): SqlSessionFactory {
        log.info("=============== embeddedSqlSessionFactory Start ===============")
        val sessionFactoryBean = SqlSessionFactoryBean()
        configureSqlSessionFactory(sessionFactoryBean, dataSource)
        log.info("=============== embeddedSqlSessionFactory End   ===============")
        return sessionFactoryBean.`object`!!
    }

}

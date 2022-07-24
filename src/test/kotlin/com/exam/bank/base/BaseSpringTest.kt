package com.exam.bank.base

import com.exam.AppMain
import org.apache.http.Header
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicHeader
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.util.DefaultUriBuilderFactory


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = [AppMain::class])
class BaseSpringTest : BaseTest() {

    @Autowired lateinit var ctx: ApplicationContext
    @Autowired lateinit var rest: TestRestTemplate

    // URL 호출을 위한 entry point
    val protocol: String = "http://"
    val host: String = "localhost"

    @Value("\${local.server.port}")
    var port: Int = 0

    // URL
    lateinit var basicUri: String

    @BeforeAll
    fun before() {
        basicUri = "$protocol$host:$port"

        // TestRestTemplate 에 Authorization 헤더가 디폴트로 지정 되도록 합니다.
        val listHeader = ArrayList<Header>()
        listHeader.add(BasicHeader("Authorization", "1"))
        val client = HttpClients.custom().setDefaultHeaders(listHeader).build()
        rest.restTemplate.requestFactory = HttpComponentsClientHttpRequestFactory(client)
        rest.restTemplate.uriTemplateHandler = DefaultUriBuilderFactory(basicUri)

        // SpringBootTest 로 구동된 어플리케이션의 주소 및 포트를 확인 합니다.
        log.info("basicUri = $basicUri")
    }

}

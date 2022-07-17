package com.exam.bank.controller

import com.exam.bank.base.BaseSpringTest
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class HomeControllerTest:BaseSpringTest() {

    @Test
    fun `홈 컨트롤러, 정상응답`(){
        // when
        val res = rest.getForEntity("/sam", String::class.java)

        // assert
        MatcherAssert.assertThat(res.statusCode, Matchers.equalTo(HttpStatus.OK))
        MatcherAssert.assertThat(res.body, Matchers.notNullValue())

    }
}

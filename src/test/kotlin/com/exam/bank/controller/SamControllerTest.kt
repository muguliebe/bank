package com.exam.bank.controller

import com.exam.bank.base.BaseSpringTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus


class SamControllerTest : BaseSpringTest() {

    @Test
    fun `sample 콘트롤러, 정상 응답`() {
        // when
        val res = rest.getForEntity("/sam", String::class.java)

        // assert
        assertThat(res.statusCode, equalTo(HttpStatus.OK))
        assertThat(res.body, notNullValue())

    }
}

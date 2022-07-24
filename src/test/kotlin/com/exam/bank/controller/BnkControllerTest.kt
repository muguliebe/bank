package com.exam.bank.controller

import com.exam.bank.base.BaseSpringTest
import com.exam.bank.controller.BnkController.*
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import org.hamcrest.Matchers.*

class BnkControllerTest : BaseSpringTest() {

    @Test
    fun `이체처리 정상 테스트`() {
        // given
        val input = TransferIn(
            wdBankCodeStd = "097",
            wdAccountNum = "0001234567890123",
            tranAmt = "500",
            consigneeNm = "목킹명",
            dpsBankCodeStd = "097",
            dpsAccountNum = "0012345678901234"
        )


        // when
        val res = rest.postForEntity("/bnk/transfer", input, TransferOut::class.java)

        // assert
        assertThat(res.statusCode, `is`(HttpStatus.OK))

    }
}

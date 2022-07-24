package com.exam.bank.util

import com.exam.bank.controller.BnkController.*
import com.exam.fwk.custom.utils.ValidUtils
import com.exam.fwk.core.error.BadRequestException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class ValidUtilTest {

    @Test
    fun `checkNullRequired 정상 테스트`() {
        val input = TransferIn(
            wdBankCodeStd = "123",
            wdAccountNum = "123",
            tranAmt = "001",
            consigneeNm = "dasf",
        )

        ValidUtils.checkRequired(input)
    }

    @Test
    fun `checkNullRequired 금액 에러 테스트`() {
        val input = TransferIn(
            wdBankCodeStd = "123",
            wdAccountNum = "123",
            tranAmt = "001",
            consigneeNm = "dasf",
        )

        assertThatThrownBy { ValidUtils.checkRequired(input) }
            .isExactlyInstanceOf(BadRequestException::class.java)

    }

    @Test
    fun `checkNullRequired 일반 에러 테스트`() {
        val input = TransferIn(
            wdBankCodeStd = "",
            wdAccountNum = "123",
            tranAmt = "000",
            consigneeNm = "dasf",
        )

        assertThatThrownBy { ValidUtils.checkRequired(input) }
            .isExactlyInstanceOf(BadRequestException::class.java)

    }
}

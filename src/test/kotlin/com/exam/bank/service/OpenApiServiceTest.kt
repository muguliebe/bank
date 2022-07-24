package com.exam.bank.service

import com.exam.bank.base.BaseSpringTest
import com.exam.bank.ext.OpenApiService
import com.exam.bank.ext.OpenApiServiceDto.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class OpenApiServiceTest: BaseSpringTest() {

    @Autowired lateinit var service: OpenApiService

    @Test
    fun `거래고유번호 일관성 유일성 체크`(){
        // 중복되는 거래고유번호가 있는지 체크한다
        val set: HashSet<String> = hashSetOf()

        // when
        for(i in 1..100) {
            val id = service.getBankTranId()
            // then
            assertTrue(set.contains(id) == false)

            set.add(id)
        }
    }

    @Test
    fun `수취인조회 서비스 정상 호출 확인`(){

        // given
        val bankTranId = service.getBankTranId()
        val input = ConsigneeGetReq(
            bankTranId = bankTranId,
            cntrAccountNum = "0012345678901234",
            bankCodeStd = "333",
            reqClientNum = "00000123456789012345"
        )

        // when
        val res = service.getConsignee(input)

        // then
        assertNotNull(res)

    }

    @Test
    fun `출금이체 서비스 정상 호출 확인`(){

        // given
        val bankTranId = service.getBankTranId()

        val input = WdtrReq(
            bankTranId = bankTranId,
            cntrAccountNum = "123",
            wdBankCodeStd = "097", // 오픈은행
            reqClientNum = "00000123456789012345"
        )

        // when
        val res = service.postWdtrReq(input)

        // then
        assertNotNull(res)

    }

    @Test
    fun `입금이체 서비스 정상 호출 확인`(){

        // given
        val inputReqList = DpstrReqSub(
            bankTranId = service.getBankTranId(),
            reqClientNum = "0000123456789012345"
        )
        val input = DpstrReq(
            cntrAccountNum = "1234567890123456",
            reqList = arrayListOf(inputReqList)
        )

        // when
        val res = service.postDpstr(input)

        // then
        assertNotNull(res)

    }

}

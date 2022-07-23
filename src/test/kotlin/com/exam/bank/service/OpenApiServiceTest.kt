package com.exam.bank.service

import com.exam.bank.base.BaseSpringTest
import com.exam.bank.ext.OpenApiService
import com.exam.bank.ext.OpenApiServiceDto
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
        val input = OpenApiServiceDto.GetConsigneeIn(
            "123", "", "500", "", reqClientNum = "1"
        )

        // when
        val res = service.getConsignee(input)

        // then

    }
}

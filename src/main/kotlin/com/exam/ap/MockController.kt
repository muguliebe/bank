@file:Suppress("unused")

package com.exam.ap

import com.exam.bank.ext.OpenApiServiceDto
import com.exam.fwk.core.base.BaseController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/ext")
class MockController : BaseController() {

    /**
     * 수취인 조회 Mocking
     */
    @PostMapping("/v2.0/inquiry/receive")
    fun getConsignee(@RequestBody input: OpenApiServiceDto.ConsigneeGetReq): OpenApiServiceDto.ConsigneeGetRes {
        log.info("오픈뱅킹] 수취인 조회 API 호출 되었습니다.")
        log.debug("오픈뱅킹] 인풋:$input")
        val res = OpenApiServiceDto.ConsigneeGetRes()
        res.apiTranId = UUID.randomUUID().toString()
        log.info("오픈뱅킹] 수취인 조회 API 응답 합니다.")
        return res
    }
}

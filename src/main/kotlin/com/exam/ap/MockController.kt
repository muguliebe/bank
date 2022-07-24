@file:Suppress("unused")

package com.exam.ap

import com.exam.bank.ext.OpenApiServiceDto.*
import com.exam.fwk.core.base.BaseController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

/**
 * 오픈뱅킹 Mocking Controller
 */
@RestController
@RequestMapping("/ext")
class MockController : BaseController() {

    /**
     * 수취인 조회 mocking
     */
    @PostMapping("/v2.0/inquiry/receive")
    fun consigneeGet(@RequestBody input: ConsigneeGetReq): ConsigneeGetRes {
        log.info("오픈뱅킹] 수취인 조회 API 호출 되었습니다.")
        log.debug("오픈뱅킹] 인풋:$input")

        val res = ConsigneeGetRes()
        res.apiTranId = UUID.randomUUID().toString()
        res.accountHolderName = "목킹명"
        res.rspCode = "A0000"
        res.rspMessage = "처리 성공"

        log.info("오픈뱅킹] 수취인 조회 API 응답 합니다.")
        return res
    }

    /**
     * 출금이체 mocking
     */
    @PostMapping("/v2.0/transfer/withdraw/acnt_num")
    fun wdtr(@RequestBody input: WdtrReq): WdtrRes {
        log.info("오픈뱅킹] 출금이체 API 호출 되었습니다.")
        log.debug("오픈뱅킹] 인풋:$input")

        val res = WdtrRes()
        res.rspCode = "A0000"
        res.rspMessage = "처리 성공"
        res.apiTranId = UUID.randomUUID().toString()

        log.info("오픈뱅킹] 출금이체 API 응답 합니다.")
        return res
    }

    @PostMapping("/v2.0/transfer/deposit/acnt_num")
    fun dptr(@RequestBody input: DpstrReq): DpstrRes{
        log.info("오픈뱅킹] 입금이체 API 호출 되었습니다.")
        log.debug("오픈뱅킹] 인풋:${input.reqList[0].bankTranId}")

        val dpstrResSub = DpstrResSub()
        dpstrResSub.bankRspCode = "000"

        val res = DpstrRes()
        res.resCnt = "1"
        res.resList.add(dpstrResSub)
        res.apiTranId = UUID.randomUUID().toString()
        res.rspCode = "A0000"
        res.rspMessage = "처리 성공"

        log.info("오픈뱅킹] 입금이체 API 응답 합니다.")
        return res
    }
}

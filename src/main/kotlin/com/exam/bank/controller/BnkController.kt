package com.exam.bank.controller

import com.exam.bank.ext.OpenApiService
import com.exam.bank.service.BnkService
import com.exam.fwk.core.annotation.Required
import com.exam.fwk.core.error.BizException
import com.exam.fwk.custom.utils.DateUtils
import com.exam.fwk.custom.utils.ValidUtils
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Suppress("unused")
@RestController
@RequestMapping("/bnk")
@Api(value = "/bnk", description = "뱅킹 콘트롤러")
class BnkController {

    @Autowired lateinit var service: BnkService
    @Autowired lateinit var serviceOpenApi: OpenApiService

    /**
     * 이체 처리
     */
    @PostMapping("/transfer")
    @ApiOperation(value = "이체 처리")
    fun transfer(@RequestBody input: TransferIn) {

        // validation ==================================================================================================
        ValidUtils.checkRequired(input)

        // 수취인조회 입력값 생성
        input.bankTranId = serviceOpenApi.getBankTranId()   // 거래고유번호
        input.bankTranIdConsigneeGet = input.bankTranId     // 거래고유번호(수취조회사용시)
        input.trDy = DateUtils.currentDy()
        input.trTm = DateUtils.currentTm()

        // 수취인조회 =====================================================================================================
        val getConsigneeOut = service.getConsignee(input)
        if (getConsigneeOut.rspCode != "A0000") {
            throw BizException("수취인명 조회가 불가합니다. ${getConsigneeOut.rspMessage}")
        }
        if (input.consigneeNm != getConsigneeOut.consigneeNm) {
            throw BizException("수취인명이 일치하지 않습니다.")
        }

        // 출금처리 ======================================================================================================
        // 출금처리 중 재처리 대상은 -> TaskT1 재처리대상(BnkTask.taskT1())
        input.bankTranId = serviceOpenApi.getBankTranId()   // 거래고유번호
        input.bnkTrsHstSeq = getConsigneeOut.bnkTrsHstSeq   // 이체내역순번
        service.procWd(input)


        // 입금처리 ======================================================================================================
        // 입금처리 중 재처리 대상은 -> TaskT2 재처리대상(BnkTask.taskT2())
        input.bankTranId = serviceOpenApi.getBankTranId()   // 거래고유번호
        service.procDps(input)

    }

    /**
     * 송금 주체입장에서의 요청 입력 DTO
     */
    data class TransferIn(
        @Required val wdBankCodeStd: String = "",   // 출금은행.표준코드 (요청고객)
        @Required val wdAccountNum: String = "",    // 출금계좌번호 (요청고객)
        @Required val tranAmt: String = "",         // 거래금액
        @Required val consigneeNm: String = "",     // 수취인명
        @Required val dpsBankCodeStd: String = "",  // 입금은행.표준코드
        @Required val dpsAccountNum: String = "",   // 입금계좌번호
        var wdPrintContent: String = "",            // 출금인자내용 (요청고객)
        var dpsPrintContent: String = "",           // 입금인자내용
        var bankTranId: String = "",                // 거래고유번호
        var trDy: String = "",                      // 거래일자(yyyyMMdd)
        var trTm: String = "",                      // 거래일시(HHmmss)
        var bnkTrsHstSeq: Int = 0,                  // 이체내역순번
        var bankTranIdConsigneeGet: String = ""     // 거래고유번호(수취조회사용시)
    )

    data class TransferOut(
        val successYn: String = "",     // 처리여부
        val reprocYn: String = "",      // 재처리여부(재처리응답이 나갈 경우, 추후 안내 나갈 것으로 팝업)
    )
}

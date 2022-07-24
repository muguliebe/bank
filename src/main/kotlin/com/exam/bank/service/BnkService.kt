package com.exam.bank.service

import com.exam.bank.controller.BnkController.*
import com.exam.bank.ext.OpenApiService
import com.exam.bank.ext.OpenApiServiceDto.*
import com.exam.fwk.custom.utils.ValidUtils
import com.exam.fwk.core.base.BaseService
import com.exam.fwk.core.error.BizException
import com.exam.fwk.core.error.EtrErrException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BnkService : BaseService() {

    @Autowired lateinit var service: OpenApiService
    @Autowired lateinit var serviceDao: BnkTrsHstDaoService

    /**
     * 이용기관 계좌번호 조회
     */
    fun getAccountNum(): String {
        return "0001234567890123"
    }

    /**
     * 입금이체용 암호문구
     */
    fun getPassPhrase(): String {
        return when {
            "prd" in ctx.environment.activeProfiles -> {
                // 운영환경은 오픈뱅킹 포털에서 발급 하여 셋팅 필요 합니다.
                TODO()
            }
            else -> "NONE"
        }
    }

    /**
     * 수취인조회
     */
    fun getConsignee(input: TransferIn): GetConsigneeOut {
        // init
        var result = GetConsigneeOut()

        // 이체내역 생성
        val bnkTrsHst = serviceDao.saveBnkTrsHst(
            userId = commons.area.user!!.userId,
            trDy = input.trDy,
            trTm = input.trTm,
            bankTranId = input.bankTranId,
            wdBankCd = input.wdBankCodeStd,
            wdAcctNo = input.wdAccountNum,
            consigneeNm = input.consigneeNm,
            dpsBankCd = input.dpsBankCodeStd,
            dpsAcctNo = input.dpsAccountNum,
            trsAmt = input.tranAmt,
            wdPrintContent = input.wdPrintContent,
            dpsPrintContent = input.dpsPrintContent,
        )

        result.bnkTrsHstSeq = bnkTrsHst.seq

        try {
            // 수취인조회 API 호출
            val inGetConsignee = ConsigneeGetReq()
            inGetConsignee.bankTranId = input.bankTranId
            inGetConsignee.cntrAccountType = "N"                                        // 약정계좌구분[N:계좌]
            inGetConsignee.cntrAccountNum = input.wdAccountNum
            inGetConsignee.printContent = "오픈뱅킹이체-${commons.area.user!!.userNm}"
            inGetConsignee.tranAmt = input.tranAmt
            inGetConsignee.reqClientName = commons.area.user!!.userNm
            inGetConsignee.reqClientNum = commons.area.user!!.userId.toString()
            inGetConsignee.transferPurpose = "TR"                                       // 이체용도[TR:송금]

            val res = service.getConsignee(inGetConsignee)
            result.consigneeNm = res.accountHolderName
            result.rspCode = res.rspCode
            result.rspMessage = res.rspMessage

        } catch (e: EtrErrException) {
            throw e
        }

        // 이체내역 수정
        // 이체 상태 코드 [A1:수취조회요청, A2:수최조회응답, B1: 출금이체요청, B2:출금이체수신, C1: 입금이체요청, C2:입금이체수신, D1:완료, E1:불능]
        serviceDao.updateBnkTrsHst(bnkTrsHst.seq, input.bankTranId, "A2", "N")

        return result
    }

    /**
     * 수취인 조회 리턴
     */
    data class GetConsigneeOut(
        var consigneeNm: String = "",   // 수취인명
        var rspCode: String = "",       // 응답코드
        var rspMessage: String = "",    // 응답메시지
        var bnkTrsHstSeq: Int = 0,      // 이체내역순번
    )

    /**
     * 출금 처리
     * - 수취조회를 먼저 하여야 한다.
     */
    fun procWd(input: TransferIn): ProcWdOut {
        // init
        val result = ProcWdOut(successYn = "N")

        // validation
        ValidUtils.checkRequired(input)

        if (input.bnkTrsHstSeq == 0)
            throw BizException("이체내역순번은 필수 입력 입니다.")

        // 이체내역 수정
        // 이체 상태 코드 [A1:수취조회요청, A2:수최조회응답, B1: 출금이체요청, B2:출금이체수신, C1: 입금이체요청, C2:입금이체수신, D1:완료, E1:불능]
        serviceDao.updateBnkTrsHst(input.bnkTrsHstSeq, input.bankTranId, "B1", "N")

        // 이체처리 API 호출 입력 셋팅
        val inWdtrReq = WdtrReq()
        inWdtrReq.bankTranId = input.bankTranId
        inWdtrReq.cntrAccountType = "N"                                         // 약정계좌구분[N:계좌]
        inWdtrReq.cntrAccountNum = getAccountNum()                              // 약정계좌번호 <- 이용기관계좌번호
        inWdtrReq.wdBankCodeStd = input.wdBankCodeStd                           // 출금은행.표준코드
        inWdtrReq.wdAccountNum = input.wdAccountNum                             // 출금은행계좌번호
        inWdtrReq.wdPrintContent = "오픈뱅킹이체-${commons.area.user!!.userNm}"    // 출금인자내역
        inWdtrReq.tranAmt = input.tranAmt                                       // 거래금액
        inWdtrReq.userSeqNo = commons.area.user!!.userSeqNo                     // 사용자일련번호
        inWdtrReq.tranDtime = input.trDy + input.trTm                           // 거래일시(yyyyMMddHHmmss) = 14자리
        inWdtrReq.reqClientName = commons.area.user!!.userNm                    // 요청고객성명
        inWdtrReq.reqClientNum = commons.area.user!!.userId.toString()          // 요청고객회원번호
        inWdtrReq.transferPurpose = "TR"                                        // 이체용도[TR:송금]

        try {
            val resPostWdtrReq = service.postWdtrReq(inWdtrReq)
            when (resPostWdtrReq.rspCode) {
                "A0000" -> {
                    // 이체내역 수정
                    // 이체 상태 코드 [A1:수취조회요청, A2:수최조회응답, B1: 출금이체요청, B2:출금이체수신, C1: 입금이체요청, C2:입금이체수신, D1:완료, E1:불능]
                    serviceDao.updateBnkTrsHst(input.bnkTrsHstSeq, input.bankTranId, "B2", "N")

                    result.successYn = "Y"

                }
                "A0326" -> {
                    // 시스템 알람이 필요.
                    throw BizException("센터 문의바랍니다. (거래고유번호 중복)") // 보완 개발 필요
                }
                else -> {
                    // 이체내역 수정: 재처리 대상입니다.
                    // 이체 상태 코드 [A1:수취조회요청, A2:수최조회응답, B1: 출금이체요청, B2:출금이체수신, C1: 입금이체요청, C2:입금이체수신, D1:완료, E1:불능]
                    serviceDao.updateBnkTrsHst(input.bnkTrsHstSeq, input.bankTranId, "B2", "Y")
                    throw BizException("처리중 지연이 발생하였습니다. 처리 완료 시 알람 발송 예정입니다.")
                }
            }

        } catch (e: EtrErrException) {
            // 이체내역 수정: 재처리 대상입니다.
            // 이체 상태 코드 [A1:수취조회요청, A2:수최조회응답, B1: 출금이체요청, B2:출금이체수신, C1: 입금이체요청, C2:입금이체수신, D1:완료, E1:불능]
            serviceDao.updateBnkTrsHst(input.bnkTrsHstSeq, input.bankTranId, "B1", "Y")
            throw BizException(e, "처리중 지연이 발생하였습니다. 처리 완료 시 알람 발송 예정입니다.")
        }

        return result

    }

    /**
     * 출금 처리 리턴
     */
    data class ProcWdOut(
        var successYn: String = ""
    )


    /**
     * 입금 처리
     */
    fun procDps(input: TransferIn): ProcDpsOut {
        // init ========================================================================================================
        val result = ProcDpsOut(successYn = "N")

        // validation
        ValidUtils.checkRequired(input)

        if (input.bnkTrsHstSeq == 0)
            throw BizException("이체내역순번은 필수 입력 입니다.")

        // 이체내역 수정 ==================================================================================================
        // 이체 상태 코드 [A1:수취조회요청, A2:수최조회응답, B1: 출금이체요청, B2:출금이체수신, C1: 입금이체요청, C2:입금이체수신, D1:완료, E1:불능]
        serviceDao.updateBnkTrsHst(input.bnkTrsHstSeq, input.bankTranId, "C1", "N")

        // 입금API 입력 셋팅 ==============================================================================================
        val inPostDpstr = DpstrReq()
        val dpstrReqSub = DpstrReqSub()
        dpstrReqSub.tranNo = "00001"                                        // 거래순번
        dpstrReqSub.bankTranId = input.bankTranId                           // 거래고유번호
        dpstrReqSub.bankCodeStd = input.dpsBankCodeStd                      // 입금은행.표준코드
        dpstrReqSub.accountNum = input.dpsAccountNum                        // 계좌번호 <- 입금계좌번호
        dpstrReqSub.tranAmt = input.tranAmt                                 // 거래금액
        dpstrReqSub.reqClientName = commons.area.user!!.userNm              // 요청고객성명
        dpstrReqSub.reqClientNum = commons.area.user!!.userId.toString()    // 요청고객회원번호
        dpstrReqSub.transferPurpose = "TR"                                  // 이체용도[TR:송금]
        dpstrReqSub.recvBankTranId = input.bankTranIdConsigneeGet           // 수취조회 거래고유번호

        inPostDpstr.cntrAccountType = "N"                                         // 약정계좌구분[N:계좌]
        inPostDpstr.cntrAccountNum = getAccountNum()                              // 약정계좌번호 <- 이용기관계좌번호
        inPostDpstr.wdPassPhrase = getPassPhrase()                                // 입금이체용 암호문구
        inPostDpstr.nameCheckOption = "on"                                        // 수취인성명 검증 여부
        inPostDpstr.wdPrintContent = "오픈뱅킹이체-${commons.area.user!!.userNm}"    // 출금인자내역
        inPostDpstr.tranDtime = input.trDy + input.trTm                           // 거래일시(yyyyMMddHHmmss) = 14자리
        inPostDpstr.reqCnt = "1"                                                  // 입금요청건수
        inPostDpstr.reqList.add(dpstrReqSub)


        // 입금이체 API 호출
        try {
            val resDpstr = service.postDpstr(inPostDpstr)
            val bankRspCode = resDpstr.resList[0].bankRspCode

            val regex = "400|803|804".toRegex() // 400:입금처리중, 803:내부처리에러, 804:처리시간초과에러
            when {
                bankRspCode.matches(regex) || resDpstr.rspCode == "A00007" -> {
                    // 이체내역 수정: 재처리 대상입니다.
                    // 이체 상태 코드 [A1:수취조회요청, A2:수최조회응답, B1: 출금이체요청, B2:출금이체수신, C1: 입금이체요청, C2:입금이체수신, D1:완료, E1:불능]
                    serviceDao.updateBnkTrsHst(input.bnkTrsHstSeq, input.bankTranId, "C2", "Y")
                }
                bankRspCode == "822" -> {
                    // 이체내역 수정
                    // 이체 상태 코드 [A1:수취조회요청, A2:수최조회응답, B1: 출금이체요청, B2:출금이체수신, C1: 입금이체요청, C2:입금이체수신, D1:완료, E1:불능]
                    serviceDao.updateBnkTrsHst(input.bnkTrsHstSeq, input.bankTranId, "E1", "N")

                }
                else -> {
                    // 이체내역 수정
                    // 이체 상태 코드 [A1:수취조회요청, A2:수최조회응답, B1: 출금이체요청, B2:출금이체수신, C1: 입금이체요청, C2:입금이체수신, D1:완료, E1:불능]
                    serviceDao.updateBnkTrsHst(input.bnkTrsHstSeq, input.bankTranId, "D1", "N")
                    result.successYn = "Y"
                }
            }

        } catch (e: EtrErrException) {
            // 이체내역 수정: 재처리 대상입니다.
            // 이체 상태 코드 [A1:수취조회요청, A2:수최조회응답, B1: 출금이체요청, B2:출금이체수신, C1: 입금이체요청, C2:입금이체수신, D1:완료, E1:불능]
            serviceDao.updateBnkTrsHst(input.bnkTrsHstSeq, input.bankTranId, "B2", "Y")
            throw BizException(e, "처리중 지연이 발생하였습니다. 처리 완료 시 알람 발송 예정입니다.")
        }

        return result
    }

    data class ProcDpsOut(
        var successYn: String = ""
    )

}

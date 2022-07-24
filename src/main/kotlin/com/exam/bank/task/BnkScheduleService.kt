package com.exam.bank.task

import com.exam.bank.controller.BnkController.TransferIn
import com.exam.bank.ext.OpenApiService
import com.exam.bank.ext.OpenApiServiceDto.GetTransResultReq
import com.exam.bank.ext.OpenApiServiceDto.GetTransResultReqSub
import com.exam.bank.repo.mybatis.BnkTrsHstMapper
import com.exam.bank.service.BnkService
import com.exam.bank.service.BnkTrsHstDaoService
import com.exam.fwk.core.base.BaseService
import com.exam.fwk.custom.utils.DateUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BnkTask : BaseService() {

    @Autowired lateinit var serviceBnk: BnkService
    @Autowired lateinit var serviceBnktrsDao: BnkTrsHstDaoService
    @Autowired lateinit var serviceOpenApi: OpenApiService
    @Autowired lateinit var repo: BnkTrsHstMapper

    /**
     * 출금 재처리 필요 대상 처리
     */
    @Scheduled(cron = "0 0/5 * * * *")
    fun taskT1() {
        log.info("===== taskT1 start ====")
        val targets = repo.selectForT1()

        for (target in targets) {

            // 이체내역 재처리 건수를 높인다
            serviceBnktrsDao.updateBnkTrsHstCntUp(target.seq)

            // 이체결과조회 입력값 셋팅
            val sub = GetTransResultReqSub()
            sub.tranNo = "1"                        // 요청순번
            sub.orgBankTranId = target.bankTranId   // 원거래 거래고유번호
            sub.orgTranAmt = target.trsAmt          // 원거래 거래금액

            val inGetTransResult = GetTransResultReq()
            inGetTransResult.checkType = "1"                        // 체크유형[1:출금이체, 2:입금이체]
            inGetTransResult.tranDtime = DateUtils.currentDtm()     // 요청일시
            inGetTransResult.reqCnt = "1"                           // 요청건수
            inGetTransResult.reqList.add(sub)

            // 이체결과조회 API 호출
            val getTransResultRes = serviceOpenApi.getTransResult(inGetTransResult)

            // 성공 하였을 시
            if (getTransResultRes.resList[0].bankRspCode == "000") {
                serviceBnktrsDao.updateBnkTrsHst(
                    seq = target.seq,
                    bankTranId = target.bankTranId,
                    // 이체 상태 코드 [A1:수취조회요청, A2:수최조회응답, B1: 출금이체요청, B2:출금이체수신, C1: 입금이체요청, C2:입금이체수신, D1:완료, E1:불능]
                    trsStatCd = "B2",
                    reprocYn = "N",
                )

                // 입금이체 요청값 셋팅
                val inProcDps = TransferIn(
                    wdBankCodeStd = target.wdBankCd,
                    wdAccountNum = target.wdAcctNo,
                    tranAmt = target.trsAmt,
                    consigneeNm = target.consigneeNm,
                    dpsBankCodeStd = target.dpsBankCd,
                    dpsAccountNum = target.dpsAcctNo,
                    bankTranId = serviceOpenApi.getBankTranId(),
                    trDy = DateUtils.currentDy(),
                    trTm = DateUtils.currentTm(),
                    wdPrintContent = target.wdPrintContent,
                    dpsPrintContent = target.dpsPrintContent
                )

                // 입금이체 API 호출
                serviceBnk.procDps(inProcDps)

                // 이체내역 재처리 건수를 클리어 합니다.
                serviceBnktrsDao.updateBnkTrsHstCntClear(target.seq)

                // TODO: 사용자에게 알람이 필요 합니다.

            }

        }


        log.info("===== taskT1 end  ====")
    }

    /**
     * 입금 재처리 필요 대상 처리
     */
    @Scheduled(cron = "0 0/5 * * * *")
    fun taskT2() {
        log.info("===== taskT2 start ====")

        val targets = repo.selectForT2()

        for (target in targets) {
            // 이체내역 재처리 건수를 높인다
            serviceBnktrsDao.updateBnkTrsHstCntUp(target.seq)

            // 이체결과조회 입력값 셋팅
            val sub = GetTransResultReqSub()
            sub.tranNo = "1"                        // 요청순번
            sub.orgBankTranId = target.bankTranId   // 원거래 거래고유번호
            sub.orgTranAmt = target.trsAmt          // 원거래 거래금액

            val inGetTransResult = GetTransResultReq()
            inGetTransResult.checkType = "2"                        // 체크유형[1:출금이체, 2:입금이체]
            inGetTransResult.tranDtime = DateUtils.currentDtm()     // 요청일시
            inGetTransResult.reqCnt = "1"                           // 요청건수
            inGetTransResult.reqList.add(sub)

            // 이체결과조회 API 호출
            val getTransResultRes = serviceOpenApi.getTransResult(inGetTransResult)

            // 성공 하였을 시
            if (getTransResultRes.resList[0].bankRspCode == "000") {
                serviceBnktrsDao.updateBnkTrsHst(
                    seq = target.seq,
                    bankTranId = target.bankTranId,
                    // 이체 상태 코드 [A1:수취조회요청, A2:수최조회응답, B1: 출금이체요청, B2:출금이체수신, C1: 입금이체요청, C2:입금이체수신, D1:완료, E1:불능]
                    trsStatCd = "C2",
                    reprocYn = "N",
                )
            }
        }

        log.info("===== taskT2 end  ====")
    }

}

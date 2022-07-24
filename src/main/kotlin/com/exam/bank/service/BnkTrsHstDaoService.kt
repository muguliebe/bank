package com.exam.bank.service

import com.exam.bank.entity.BnkTrsHst
import com.exam.bank.repo.jpa.BnkTrsHstRepo
import com.exam.fwk.custom.utils.DateUtils
import com.exam.fwk.core.base.BaseService
import com.exam.fwk.core.error.BizException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger

@Service
class BnkTrsHstDaoService : BaseService() {

    @Autowired lateinit var repo: BnkTrsHstRepo

    /**
     * 이체내역 생성
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveBnkTrsHst(
        userId: BigInteger,         // 사용자 ID
        trDy: String,               // 거래일자
        trTm: String,               // 거래일시
        bankTranId: String,         // 거래고유번호
        wdBankCd: String,           // 출금은행코드(097:오픈은행)
        wdAcctNo: String,           // 출금계좌번호
        consigneeNm: String,        // 수취인명
        dpsBankCd: String,          // 입금은행코드
        dpsAcctNo: String,          // 입금계좌번호
        trsAmt: String,             // 이체금액
        wdPrintContent: String,     // 출금인자내용
        dpsPrintContent: String     // 입금인자내용
    ): BnkTrsHst {
        // init
        var result: BnkTrsHst

        // prepare for save
        val inSave = BnkTrsHst()
        inSave.createDt = DateUtils.currentTimeStamp()  // 생성일시
        inSave.createPgmId = javaClass.simpleName       // 생성프로그램 ID
        inSave.createUserId = userId.toString(20)       // 생성자 ID
        inSave.gid = commons.area.gid                   // gid
        inSave.trDy = trDy                              // 거래일자
        inSave.trTm = trTm                              // 거래일시
        inSave.bankTranId = bankTranId                  // 거래고유번호
        inSave.userId = userId                          // 사용자 ID
        inSave.reprocYn = "N"                           // 재처리여부
        inSave.wdBankCd = wdBankCd                      // 출금은행코드
        inSave.trsAmt = trsAmt                          // 거래금액

        // 이체상태코드 [A1:수취조회요청, A2:수최조회응답, B1: 출금이체요청, B2:출금이체수신, C1: 입금이체요청, C2:입금이체수신, D1:완료]
        inSave.trsStatCd = "A1"

        try {
            // DB Insert: 이체내역
            result = repo.save(inSave)
        } catch (e: Exception) {
            throw BizException("이체내역 저장 중 에러가 발생하였습니다.")
        }

        return result
    }

    /**
     * 이체내역 수정 - 이체거래상태코드, 재처리여부 update 용
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateBnkTrsHst(seq:Int, bankTranId: String, trsStatCd: String, reprocYn: String): BnkTrsHst {

        // init
        var result: BnkTrsHst

        // prepare for save
        val exist = repo.findById(seq).get()
        exist.updateDt = DateUtils.currentTimeStamp()       // 수정일시
        exist.updatePgmId = javaClass.simpleName            // 수정프로그램 ID
        exist.reprocYn = reprocYn                           // 재처리여부
        exist.bankTranId = bankTranId

        // 이체 상태 코드 [A1:수취조회요청, A2:수최조회응답, B1: 출금이체요청, B2:출금이체수신, C1: 입금이체요청, C2:입금이체수신, D1:완료, E1:불능]
        exist.trsStatCd = trsStatCd

        try {
            // DB Update : 대외거래내역
            result = repo.save(exist)
        } catch (e: Exception) {
            throw BizException("이체내역 저장 중 에러가 발생하였습니다.")
        }

        return result

    }

}

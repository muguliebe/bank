package com.exam.bank.entity

import org.springframework.context.annotation.Description
import java.math.BigInteger
import java.time.OffsetDateTime
import javax.persistence.*

@Entity
@Table(name = "bnk_trs_hst")
@Description("이체내역")
class BnkTrsHst(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var seq: Int = 0,                           // 순번
    var createDt: OffsetDateTime? = null,       // 생성일시
    var createUserId: String? = null,           // 생성자 ID
    var createPgmId: String? = null,            // 생성프로그램 ID
    var updateDt: OffsetDateTime? = null,       // 수정일시
    var updateUserId: String? = null,           // 수정자 ID
    var updatePgmId: String? = null,            // 수정프로그램 ID
    var gid: String = "",                       // gid
    var trDy: String = "",                      // 거래일자
    var trTm: String = "",                      // 거래일시
    var bankTranId: String = "",                // 거래고유번호
    var userId: BigInteger = BigInteger.ZERO,   // 사용자번호
    var trsStatCd: String = "",                 // 이체 상태 코드 [A1:수취조회요청, A2:수최조회응답, B1: 출금이체요청, B2:출금이체수신, C1: 입금이체요청, C2:입금이체수신, D1:완료, E1:불능]
    var reprocYn: String = "",                  // 재처리여부
    var retryCnt: Int = 0,                      // 재처리건수
    var wdBankCd: String = "",                  // 출금은행코드
    var wdAcctNo: String = "",                  // 출금계좌번호
    var consigneeNm: String = "",               // 수취인명
    var dpsBankCd: String = "",                 // 입금은행코드
    var dpsAcctNo: String = "",                 // 입금계좌번호
    var trsAmt: String = "",                    // 거래금액
    var wdPrintContent: String = "",            // 출금인자내용
    var dpsPrintContent: String = "",           // 입금인자내용
)

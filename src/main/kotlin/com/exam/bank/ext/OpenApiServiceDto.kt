package com.exam.bank.ext

import com.exam.fwk.core.annotation.Length
import com.exam.fwk.core.annotation.Required
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@Suppress("unused")
class OpenApiServiceDto {

    /**
     * 오픈뱅킹 수취인 조회 요청
     */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class ConsigneeGetReq(
        @Required @Length(len = 20) var bankTranId: String = "",            // 거래고유번호
        @Required @Length(len = 1) var cntrAccountType: String = "N",       // 약정계좌구분[N:계좌, C:계정]
        @Required @Length(len = 16) var cntrAccountNum: String = "",        // 약정 계좌번호
        @Length(len = 3) var bankCodeStd: String? = null,                   // 입금은행 표준코드
        @Length(len = 16) var accountNum: String? = null,                   // 계좌번호
        @Length(len = 3) var accountSeq: String? = null,                    // 계좌일련번호
        @Length(len = 24) var fintechUseNum: String? = null,                // 핀테크이용번호
        @Required @Length(len = 20) var printContent: String = "",          // 입금계좌인자내역
        @Required @Length(len = 12) var tranAmt: String = "",               // 거래금액
        @Required @Length(len = 20) var reqClientName: String = "",         // 요청고객성명
        @Length(len = 3) var reqClientBankCode: String? = null,             // 요청고객계좌 개설기관.표준코드
        @Length(len = 16) var reqClientAccountNum: String? = null,          // 요청고객계좌번호
        @Length(len = 24) var reqClientFintechUseNum: String? = null,       // 요청고객핀테크이용번호
        @Required @Length(len = 20) var reqClientNum: String = "",          // 요청고객회원번호
        @Required @Length(len = 2) var transferPurpose: String = "TR",      // 이체용도[TR:송금, ST:결제, RC:충전, AU:인증, WD:출금, EX:환전]
        @Length(len = 40) var subFrncName: String? = null,                  // 하위가맹점명
        @Length(len = 20) var subFrncNum: String? = null,                    // 하위가맹점번호
        @Length(len = 10) var subFrncBusinessNum: String? = null,            // 하위가맹점 사업자등록번호
        @Length(len = 32) var cmsNum: String? = null                         // CMS 번호
    )

    /**
     * 오픈뱅킹 수취인 조회 응답
     */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class ConsigneeGetRes(
        @Length(len = 40) var apiTranId: String = "",          // 거래고유번호
        @Length(len = 20) var apiTranDtm: Number = 0,           // 거래일시(밀리세컨드)
        @Length(len = 5) var rspCode: String = "",              // 응답코드
        @Length(len = 300) var rspMessage: String = "",         // 응답메시지
        @Length(len = 3) var bankCodeStd: String = "",          // 입금기관.표준코드
        @Length(len = 7) var bankCodeSub: String = "",          // 입금기관.점별코드
        @Length(len = 20) var bankName: String = "",            // 입금기관명
        @Length(len = 20) var savingsBankName: String = "",     // 개별저축은행명
        @Length(len = 16) var accountNum: String = "",          // 입금계좌번호
        @Length(len = 3) var accountSeq: String = "",           // 회차번호
        @Length(len = 20) var accountNumMasked: String = "",    // 입금계좌번호(출력용)
        @Length(len = 20) var printContent: String = "",        // 입금계좌인자내역
        @Length(len = 20) var accountHolderName: String = "",   // 수취인성명
        @Length(len = 20) var bankTranId: String = "",          // 거래고유번호(참가기관)
        @Length(len = 8) var bankTranDate: String = "",         // 거래일자(참가기관)
        @Length(len = 3) var bankCodeTran: String = "",         // 응답코드를 부여한 참가기관.표준코드
        @Length(len = 3) var bankRspCode: String = "",          // 응답코드(참가기관)
        @Length(len = 100) var bankRspMessage: String = "",     // 응답메시지(참가기관)
        @Length(len = 3) var wdBankCodeStd: String = "",        // 출금(개설)기관.표준코드
        @Length(len = 20) var wdBankName: String = "",          // 출금(개설)기관명
        @Length(len = 16) var wdAccountNum: String = "",        // 출금계좌번호
        @Length(len = 12) var tranAmt: String = "",             // 거래금액
        @Length(len = 32) var cmsNum: String = "",              // CMS번호
    )

    /**
     * 오픈뱅킹 출금이체 요청
     */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class WdtrReq(
        @Required @Length(len = 20) var bankTranId: String = "",            // 거래고유번호
        @Required @Length(len = 1) var cntrAccountType: String = "N",       // 약정계좌구분[N:계좌, C:계정]
        @Required @Length(len = 16) var cntrAccountNum: String = "",        // 약정 계좌번호
        @Required @Length(len = 3) var wdBankCodeStd: String = "",          // 출금은행.표준코드
        @Required @Length(len = 16) var wdAccountNum: String = "",          // 출금계좌번호
        @Required @Length(len = 12) var tranAmt: String = "",               // 거래금액
        @Length(len = 14) var wdPrintContent: String = "",                  // 출금계좌인자내역
        @Required @Length(len = 10) var userSeqNo: String = "",             // 사용자일련번호
        @Required @Length(len = 14) var tranDtime: String = "",             // 요청일시
        @Required @Length(len = 20) var reqClientName: String = "",         // 요청고객성명
        @Length(len = 3) var reqClientBankCode: String? = null,             // 요청고객계좌 개설기관.표준코드
        @Length(len = 16) var reqClientAccountNum: String? = null,          // 요청고객계좌번호
        @Length(len = 24) var reqClientFintechUseNum: String? = null,       // 요청고객핀테크이용번호
        @Required @Length(len = 20) var reqClientNum: String = "",          // 요청고객회원번호
        @Required @Length(len = 2) var transferPurpose: String = "TR",      // 이체용도[TR:송금, ST:결제, RC:충전, AU:인증, WD:출금, EX:환전]
        @Length(len = 40) var subFrncName: String? = null,                  // 하위가맹점명
        @Length(len = 20) var subFrncNum: String? = null,                   // 하위가맹점번호
        @Length(len = 10) var subFrncBusinessNum: String? = null,           // 하위가맹점 사업자등록번호
        @Length(len = 20) var recvClientName: String? = null,               // 최종수취고객성명
        @Length(len = 3) var recvClientBankCode: String? = null,            // 최종수취고객계좌 개설기관.표준코드
        @Length(len = 16) var recvClientAcountNum: String? = null,          // 최종수취고객계좌번호
    )

    /**
     * 오픈뱅킹 출금이체 응답
     */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class WdtrRes(
        @Length(len = 40) var apiTranId: String = "",               // 거래고유번호
        @Length(len = 20) var apiTranDtm: Number = 0,               // 거래일시(밀리세컨드)
        @Length(len = 5) var rspCode: String = "",                  // 응답코드
        @Length(len = 300) var rspMessage: String = "",             // 응답메시지
        @Length(len = 3) var dpsBankCodeStd: String = "",           // 입금기관.표준코드
        @Length(len = 7) var dpsBankCodeSub: String = "",           // 입금기관.점별코드
        @Length(len = 20) var dpsBankName: String = "",             // 입금기관명
        @Length(len = 20) var dpsAccountNumMasked: String = "",     // 입금계좌번호(출력용)
        @Length(len = 20) var dpsPrintContent: String = "",         // 입금계좌인자내역
        @Length(len = 20) var dpsAccountHolderName: String = "",    // 수취인성명
        @Length(len = 20) var bankTranId: String = "",              // 거래고유번호(참가기관)
        @Length(len = 8) var bankTranDate: String = "",             // 거래일자(참가기관)
        @Length(len = 3) var bankCodeTran: String = "",             // 응답코드를 부여한 참가기관.표준코드
        @Length(len = 3) var bankRspCode: String = "",              // 응답코드(참가기관)
        @Length(len = 100) var bankRspMessage: String = "",         // 응답메시지(참가기관)
        @Length(len = 24) var fintechUseNum: String = "",           // 출금계좌핀테크이용번호
        @Length(len = 50) var accountAlias: String = "",            // 출금계좌별명
        @Length(len = 3) var bankCodeStd: String = "",              // 출금(개설)기관.표준코드
        @Length(len = 20) var bankName: String = "",                // 출금(개설)기관명
        @Length(len = 20) var savingsBankName: String = "",         // 개별저축은행명
        @Length(len = 20) var accountNumMasked: String = "",        // 입금계좌번호(출력용)
        @Length(len = 20) var printContent: String = "",            // 출금계좌인자내역
        @Length(len = 20) var accountHolderName: String = "",       // 송금인성명
        @Length(len = 12) var tranAmt: String = "",                 // 거래금액
        @Length(len = 12) var wdLimitRemainAmt: String = "",        // 출금한도잔여금액
    )

    /**
     * 오픈뱅킹 입금이체 요청
     */
    data class DpstrReq(
        @Required @Length(len = 1) var cntrAccountType: String = "N",       // 약정계좌구분[N:계좌, C:계정]
        @Required @Length(len = 16) var cntrAccountNum: String = "",        // 약정 계좌번호
        @Required @Length(len = 128) var wdPassPhrase: String = "",         // 입금이체용 암호문구
        @Required @Length(len = 20) var wdPrintContent: String = "",        // 출금계좌인자내역
        @Required @Length(len = 3) var nameCheckOption: String = "on",      // 수취인성명 검증 여부[on:검증함, off:미검증]
        @Length(len = 40) var subFrncName: String? = null,                  // 하위가맹점명
        @Length(len = 20) var subFrncNum: String? = null,                   // 하위가맹점번호
        @Length(len = 10) var subFrncBusinessNum: String? = null,           // 하위가맹점 사업자등록번호
        @Required @Length(len = 14) var tranDtime: String = "",             // 요청일시
        @Required @Length(len = 5) var reqCnt: String = "",                 // 요청건수
        var reqList: ArrayList<DpstrReqSub> = arrayListOf()                 // 입금요청목록
    )

    /**
     * 오픈뱅킹 입금이체 요청 Array
     */
    data class DpstrReqSub(
        @Required @Length(len = 5) var tranNo: String = "",                 // 거래순번
        @Required @Length(len = 20) var bankTranId: String = "",            // 거래고유번호
        @Required @Length(len = 3) var bankCodeStd: String = "",            // 입금은행.표준코드
        @Required @Length(len = 16) var accountNum: String = "",            // 계좌번호
        @Length(len = 20) var accountHolderName: String = "",               // 입금계좌예금주명
        @Required @Length(len = 20) var printContent: String = "",          // 입금계좌인자내역
        @Required @Length(len = 12) var tranAmt: String = "",               // 거래금액
        @Required @Length(len = 20) var reqClientName: String = "",         // 요청고객성명
        @Length(len = 3) var reqClientBankCode: String? = null,             // 요청고객계좌 개설기관.표준코드
        @Length(len = 16) var reqClientAccountNum: String? = null,          // 요청고객계좌번호
        @Length(len = 24) var reqClientFintechUseNum: String? = null,       // 요청고객핀테크이용번호
        @Required @Length(len = 20) var reqClientNum: String = "",          // 요청고객회원번호
        @Required @Length(len = 2) var transferPurpose: String = "TR",      // 이체용도[TR:송금, ST:결제, RC:충전, AU:인증, WD:출금, EX:환전]
        @Length(len = 20) var recvBankTranId: String = "",                  // 수취조회 거래고유번호(참가기관)
        @Length(len = 32) var cmsNum: String = "",                          // CMS번호

    )

    /**
     * 오픈뱅킹 입금이체 응답
     */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class DpstrRes(
        @Length(len = 40) var apiTranId: String = "",               // 거래고유번호
        @Length(len = 20) var apiTranDtm: Number = 0,               // 거래일시(밀리세컨드)
        @Length(len = 5) var rspCode: String = "",                  // 응답코드
        @Length(len = 300) var rspMessage: String = "",             // 응답메시지
        @Length(len = 3) var wdBankCodeStd: String = "",            // 출금기관.표준코드
        @Length(len = 7) var wdBankCodeSub: String = "",            // 출금기관.점별코드
        @Length(len = 20) var wdBankName: String = "",              // 출금기관명
        @Length(len = 20) var wdAccountNumMasked: String = "",      // 출금계좌번호(출력용)
        @Length(len = 20) var wdPrintContent: String = "",          // 출금계좌인자내역
        @Length(len = 20) var wdAccountHolderName: String = "",     // 송금인성명
        @Length(len = 5) var resCnt: String = "",                   // 입금건수
        var resList: ArrayList<DpstrResSub> = arrayListOf()         // 입금목록

    )

    /**
     * 오픈뱅킹 입금이체 응답 Array
     */
    data class DpstrResSub(
        @Required @Length(len = 5) var tranNo: String = "",         // 거래순번
        @Length(len = 20) var bankTranId: String = "",              // 거래고유번호(참가기관)
        @Length(len = 8) var bankTranDate: String = "",             // 거래일자(참가기관)
        @Length(len = 3) var bankCodeTran: String = "",             // 응답코드를 부여한 참가기관.표준코드
        @Length(len = 3) var bankRspCode: String = "",              // 응답코드(참가기관)
        @Length(len = 100) var bankRspMessage: String = "",         // 응답메시지(참가기관)
        @Length(len = 24) var fintechUseNum: String = "",           // 핀테크이용번호
        @Length(len = 50) var accountAlias: String = "",            // 계좌별명
        @Length(len = 3) var bankCodeStd: String = "",              // 입금(개설)기관.표준코드
        @Length(len = 7) var bankCodeSub: String = "",              // 입금(개설)기관.점별코드
        @Length(len = 20) var bankName: String = "",                // 입금(개설)기관명
        @Length(len = 20) var savingsBankName: String = "",         // 개별저축은행명
        @Length(len = 20) var accountNumMasked: String = "",        // 입금계좌번호(출력용)
        @Length(len = 20) var printContent: String = "",            // 입금계좌인자내역
        @Length(len = 20) var accountHolderName: String = "",       // 수취인성명
        @Length(len = 12) var tranAmt: String = "",                 // 거래금액
        @Length(len = 32) var cmsNum: String = "",                  // CMS 번호
    )
}

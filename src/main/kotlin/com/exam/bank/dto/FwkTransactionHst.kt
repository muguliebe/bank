package com.exam.bank.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate
import java.time.OffsetDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FwkTransactionHst(
        var trDy: LocalDate = LocalDate.now(), // 거래 일자
        var gid: String = "",                             // 글로벌 ID
        var method: String = "",                          // HTTP Method ( GET, PUT.. )
        var path: String = "",                            // API URL
        var startTime: String? = null,                    // 거래 시작 시간 (yyMMdd)
        var endTime: String? = null,                      // 거래 종료 시간
        var elapsed: Long? = null,                        // 수행 시간 (ms)
        var referrer: String? = null,                     // 호출 URL
        var remoteIp: String = "",                        // 호출지 IP
        var statCode: String? = null,                     // HTTP 상태 코드 (200, 500..)
        var queryStr: String? = null,                     // HTTP Query String
        var body: String? = null,                         // HTTP Input Body
        var errMsg: String? = null,                       // 에러 메시지
        var createUserId: String? = null,                 // 생성자 ID
        var createDt: OffsetDateTime? = null              // 생성 일시
)

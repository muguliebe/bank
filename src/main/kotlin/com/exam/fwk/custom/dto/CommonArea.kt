package com.exam.fwk.custom.dto

import java.time.OffsetDateTime

data class CommonArea(
        var date: String = "",                 // 거래 일자
        var gid: String = "",                  // 글로벌 ID
        var referrer: String? = null,          // 호출 URL
        var method: String = "",               // HTTP Method ( GET, PUT.. )
        var path: String = "",                 // API URL
        var statCd: String? = null,            // HTTP 상태 코드 (200, 500..)
        var startDt: OffsetDateTime? = null,   // 거래 시작 시간 (yyMMdd)
        var endDt: OffsetDateTime? = null,     // 거래 종료 시간
        var elapsed: Long? = 0,                // 수행 시간 (ms)
        var remoteIp: String = "",             // 호출지 IP
        var queryString: String? = null,       // HTTP Query String
        var body: String? = null,              // HTTP Input Body
        var err: Exception? = null,            // 에러
        var errMsg: String? = null,            // 에러 메시지
        var user: ComUser? = null              // 사용자
)

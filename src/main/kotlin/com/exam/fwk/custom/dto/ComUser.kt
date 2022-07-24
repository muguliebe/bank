package com.exam.fwk.custom.dto

import java.math.BigInteger

data class ComUser(
    var userId: BigInteger = BigInteger.ZERO,       // 사용자 ID
    var userNm: String = "",                        // 사용자명
    var userSeqNo: String = "",                     // 사용자일련번호(오픈뱅킹용)
)

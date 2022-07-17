package com.exam.fwk.custom.dto

data class ComUser(
        var userId: Int = 0,     // 사용자 ID
        var email: String = "",  // 이메일
        var jwt: String = ""     // 토큰
)

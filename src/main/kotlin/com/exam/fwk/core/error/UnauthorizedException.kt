package com.exam.fwk.core.error

import org.springframework.http.HttpStatus

open class UnauthorizedException(
    override val httpStatus: HttpStatus = HttpStatus.UNAUTHORIZED,
    override val msgCd: String = "인증이 필요 합니다.",
    e: Throwable? = null,
    override val msgArgs: List<String>?=null
) : BizException(httpStatus, msgCd, e, msgArgs) {
    constructor(e: Throwable, msgCd: String) : this(HttpStatus.UNAUTHORIZED, msgCd, e)
    constructor(e: Throwable, msgCd: String, msgArgs: List<String>?) : this(HttpStatus.UNAUTHORIZED, msgCd, e, msgArgs)
    constructor(msgCd: String) : this(HttpStatus.UNAUTHORIZED, msgCd)
    constructor(msgCd: String, msgArgs: List<String>?) : this(HttpStatus.UNAUTHORIZED, msgCd, msgArgs = msgArgs)
}

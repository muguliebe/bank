package com.exam.fwk.core.error

import org.springframework.http.HttpStatus

/**
 * 대외거래 에러일 경우 사용
 */
open class EtrErrException(
        override val httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        override val msgCd: String,
        e: Throwable? = null,
        override val msgArgs: List<String>? = null
) : BaseException(httpStatus, msgCd, e) {
    constructor(msgCd: String) : this(HttpStatus.INTERNAL_SERVER_ERROR, msgCd)
    constructor(msgCd: String, msgArgs: List<String>?) : this(HttpStatus.INTERNAL_SERVER_ERROR, msgCd, msgArgs = msgArgs)
    constructor(e: Throwable, msgCd: String, msgArgs: List<String>?) : this(HttpStatus.INTERNAL_SERVER_ERROR, msgCd, e, msgArgs)
    constructor(e: Throwable, msgCd: String) : this(HttpStatus.INTERNAL_SERVER_ERROR, msgCd, e)
}

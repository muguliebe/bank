package com.exam.fwk.core.error

import org.springframework.http.HttpStatus

/**
 * 시스템 에러일 경우 사용
 */
open class SystemException(
        override val httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        override val msgCd: String,
        e: Throwable? = null,
        override val msgArgs: List<String>? = null
) : BaseException(httpStatus, msgCd, e) {
    constructor(msgCd: String) : this(HttpStatus.INTERNAL_SERVER_ERROR, msgCd)
    constructor(msgCd: String, msgArgs: List<String>?) : this(HttpStatus.INTERNAL_SERVER_ERROR, msgCd, msgArgs = msgArgs)
}

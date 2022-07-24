package com.exam.fwk.core.error

import org.springframework.http.HttpStatus

class BadRequestException(
    override val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST,
    override val msgCd: String,
    e: Throwable? = null,
    override val msgArgs: List<String>? = null,
    override val fieldName: String? = null
) : BizException(httpStatus, msgCd, e, msgArgs) {
    constructor(e: Throwable, msgCd: String) : this(HttpStatus.BAD_REQUEST, msgCd, e)
    constructor(e: Throwable, msgCd: String, msgArgs: List<String>?) : this(HttpStatus.BAD_REQUEST, msgCd, e, msgArgs)
    constructor(msgCd: String) : this(HttpStatus.BAD_REQUEST, msgCd)
    constructor(msgCd: String, msgArgs: List<String>?) : this(HttpStatus.BAD_REQUEST, msgCd = msgCd, msgArgs = msgArgs)
    constructor(msgCd: String, msgArgs: List<String>?, fieldName: String) : this(HttpStatus.BAD_REQUEST, msgCd = msgCd, msgArgs = msgArgs, fieldName = fieldName)
}

package com.exam.fwk.core.error

import com.exam.bank.utils.DateUtils
import com.fasterxml.jackson.annotation.JsonIgnore
import javax.servlet.http.HttpServletRequest

class DefaultExceptionResponse(request: HttpServletRequest, exception: Exception?) {
    var timeStamp: String = DateUtils.currentTimestampString()
    var gid: String = ""
    var status: String = ""
    var statusName: String = ""
    var path: String = request.requestURI
    var errorMessageCd: String? = null
    var errorMessage: String? = null
    var errorType: String = "S"
    var errorClassName: String? = null
    var errorStack: ArrayList<String> = arrayListOf()
    @JsonIgnore var request: HttpServletRequest = request
    @JsonIgnore var exception: Exception? = exception
}

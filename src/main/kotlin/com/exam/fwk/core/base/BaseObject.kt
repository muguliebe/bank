package com.exam.fwk.core.base

import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

/**
 * 최상위 클래스
 */
abstract class BaseObject {

    companion object {
        @JvmStatic
        protected val logError = LoggerFactory.getLogger("fileError") as Logger
    }

    @Autowired lateinit var ctx: ApplicationContext


}

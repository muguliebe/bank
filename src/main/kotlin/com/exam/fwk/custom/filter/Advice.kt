package com.exam.fwk.custom.filter

import ch.qos.logback.classic.Logger
import com.exam.bank.repo.jpa.UserRepo
import com.exam.fwk.core.component.Commons
import com.exam.fwk.core.error.BaseException
import com.exam.fwk.core.error.UnauthorizedException
import com.exam.fwk.custom.dto.ComUser
import com.exam.fwk.custom.service.TransactionService
import com.exam.fwk.custom.utils.DateUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.net.URI
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.system.measureTimeMillis

@Suppress("unused")
@Aspect
@Component
class Advice {

    companion object {
        val log: Logger = LoggerFactory.getLogger(Advice::class.java) as Logger
    }

    @Autowired lateinit var commons: Commons                        // Common 영역
    @Autowired lateinit var serviceTransaction: TransactionService  // 거래내역 서비스
    @Autowired lateinit var repoUser: UserRepo                      // 사용자 Repo

    /**
     * 콘트롤러 전/후 처리
     */
    @Around("PointcutList.allController()")
    fun aroundController(pjp: ProceedingJoinPoint): Any? {

        // Init --------------------------------------------------------------------------------------------------------
        var result: Any?
        val req = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val comArea = commons.area
        val signatureName = "${pjp.signature.declaringType.simpleName}.${pjp.signature.name}"

        setAuth(req)        // 사용자 인증
        setCommonArea(req)  // CommonArea 설정

        // Main --------------------------------------------------------------------------------------------------------
        log.info("[${comArea.gid}] >>>>>  controller start [$signatureName() from [${req.remoteAddr}] by ${req.method} ${req.requestURI}")
        try {
            validPermission() // 권한이 있는 접근인지 검사
            comArea.elapsed = measureTimeMillis {
                result = pjp.proceed()
            }
        } catch (e: Exception) {
            log.info("[${comArea.gid}] <<<<<  controller   end [$signatureName() from [${comArea.remoteIp}] [${comArea.elapsed}ms] with Error [${e.javaClass.simpleName}]")
            saveTransaction(e) // 거래내역 저장
            throw e
        }

        // End ---------------------------------------------------------------------------------------------------------
        saveTransaction() // 거래내역 저장

        log.info("[${comArea.gid}] <<<<<  controller   end [$signatureName() from [${comArea.remoteIp}] [${comArea.elapsed}ms]")
        return result

    }

    /**
     * 사용자 인증
     * - 토큰이 아닌 사용자 ID로 인증처리 합니다.
     * - 사용자 ID 로 DB 조회
     */
    private fun setAuth(req: HttpServletRequest) {
        val authorization = req.getHeader("Authorization")

        if (authorization != null && authorization.isNotEmpty()) {
            val optionUser = repoUser.findById(authorization.toBigInteger())
            if (!optionUser.isPresent)
                throw UnauthorizedException("존재하지 않는 사용자입니다.")

            val user = optionUser.get()

            commons.area.user = ComUser(userId = user.userId, userNm = user.userNm, userSeqNo = user.userSeqNo)
        }
    }

    /**
     * 권한이 있는 접근인지 검사합니다.
     */
    private fun validPermission() {
        val path = commons.area.path
        val isExceptUrl = when {
            path == "/" -> true
            path.startsWith("/sam") -> true
            else -> false
        }

        if(isExceptUrl) return

        if (commons.area.user == null) {
            throw UnauthorizedException("인가되지 않은 접근입니다.")
        }
    }

    /**
     * Common Area 셋팅
     */
    private fun setCommonArea(req: HttpServletRequest) {

        val comArea = commons.area
        comArea.startDt = OffsetDateTime.now(ZoneId.of("+9"))
        comArea.date = DateUtils.currentDate()
        comArea.gid = UUID.randomUUID().toString()
        comArea.path = req.requestURI
        comArea.remoteIp = req.remoteAddr
        comArea.queryString = req.queryString
        comArea.method = req.method

        if (req.getHeader("referer") != null) {
            val referrer = req.getHeader("referer")
            comArea.referrer = URI(referrer).path
        }

    }

    /**
     * 거래내역 저장
     */
    private fun saveTransaction(ex: Exception? = null) {

        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val response =
            (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).response as HttpServletResponse
        val comArea = commons.area
        comArea.err = ex
        comArea.endDt = OffsetDateTime.now(ZoneId.of("+9"))
        comArea.elapsed = Duration.between(comArea.startDt, comArea.endDt).toMillis()


        comArea.statCd = response.status.toString()

        val originalUri = request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI)
        originalUri?.let { comArea.path = originalUri.toString() }

        ex?.let {
            // error message
            val errorStack: ArrayList<String> = arrayListOf()
            var cause: Throwable = ex
            while (cause.cause != null) {
                errorStack.add(cause.cause!!.message.toString())
                cause = cause.cause!!
            }
            comArea.errMsg = cause.message

            // status code
            if (ex is BaseException) comArea.statCd = ex.httpStatus.value().toString()
            else comArea.statCd = "500"
        }

        serviceTransaction.insertTransaction(comArea)
    }

    /**
     * 서비스 전/후 처리
     */
    @Around("PointcutList.allServices()")
    fun aroundService(pjp: ProceedingJoinPoint): Any? {

        // Init --------------------------------------------------------------------------------------------------------
        var result: Any? = null
        val serviceFullName = (pjp.signature.declaringType.simpleName + "." + pjp.signature.name)
        val args = pjp.args.toList().joinToString(",")
        var elapsed: Long = 0
        val withArgs = if (args.isNotEmpty()) {
            if (args.length > 120) "with ${args.slice(0..120)}..."
            "with $args"
        } else ""

        log.info(" >>>>>  service start   [$serviceFullName()] $withArgs ")

        // Main --------------------------------------------------------------------------------------------------------
        try {
            elapsed = measureTimeMillis {
                result = pjp.proceed()
            }
        } catch (e: Exception) {
            log.error("     >  [$serviceFullName()] occurred error {${e.message}}]")
            throw e
        } finally {
            val returnForLog = when {
                result != null && result.toString().length > 120 -> "{ ${
                    result.toString()
                        .slice(0..120)
                }...}"
                result != null -> "{ $result }"
                else -> ""
            }

            log.info(" >>>>>  service   end   [$serviceFullName()] [${elapsed}ms] $returnForLog")
        }

        // End ---------------------------------------------------------------------------------------------------------
        return result

    }

}

package com.exam.fwk.custom.service

import com.exam.bank.dto.FwkTransactionHst
import com.exam.bank.repo.mybatis.TransactionMapper
import com.exam.fwk.custom.utils.DateUtils
import com.exam.fwk.core.base.BaseService
import com.exam.fwk.custom.dto.CommonArea
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 거래내역 서비스
 */
@Service
class TransactionService : BaseService() {

    @Autowired
    lateinit var mapper: TransactionMapper  // 거래내역

    /**
     * 거래내역 생성
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun insertTransaction(commons: CommonArea) {

        // set insert input
        val tr = FwkTransactionHst()
        tr.trDy = LocalDate.parse(commons.date)
        tr.gid = commons.gid
        tr.method = commons.method
        tr.path = commons.path
        tr.statCode = commons.statCd
        tr.startTime = commons.startDt?.format(DateTimeFormatter.ofPattern("HHmmss"))
        tr.endTime = commons.endDt?.format(DateTimeFormatter.ofPattern("HHmmss"))
        tr.elapsed = when (commons.elapsed) {
            null -> Duration.between(commons.startDt, commons.endDt).toMillis()
            else -> commons.elapsed
        }
        tr.remoteIp = commons.remoteIp
        tr.queryStr = commons.queryString
        tr.body = commons.body
        tr.errMsg = commons.errMsg
        tr.referrer = commons.referrer
        tr.createDt = DateUtils.currentTimeStamp()
        tr.createUserId = commons.user?.userId.toString()

        // insert
        mapper.insertTransaction(tr)
    }

}

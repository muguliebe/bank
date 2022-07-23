package com.exam.bank.repo.mybatis

import com.exam.bank.dto.FwkTransactionHst
import org.springframework.stereotype.Repository

@Repository
interface SequenceMapper {
    fun selectDailySequence(): Long
}

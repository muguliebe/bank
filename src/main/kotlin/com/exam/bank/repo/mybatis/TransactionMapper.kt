package com.exam.bank.repo.mybatis

import com.exam.bank.dto.FwkTransactionHst
import org.springframework.stereotype.Repository

@Repository
interface TransactionMapper {
    fun insertTransaction(transaction: FwkTransactionHst)
}

package com.exam.bank.repo.mybatis

import org.springframework.stereotype.Repository

@Repository
interface SequenceMapper {
    fun selectDailySequence(): Long
}

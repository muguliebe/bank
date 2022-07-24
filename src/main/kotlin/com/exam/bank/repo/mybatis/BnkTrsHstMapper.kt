package com.exam.bank.repo.mybatis

import com.exam.bank.entity.BnkTrsHst
import org.springframework.stereotype.Repository

@Repository
interface BnkTrsHstMapper {
    fun selectForT1() : List<BnkTrsHst>
    fun selectForT2() : List<BnkTrsHst>
}

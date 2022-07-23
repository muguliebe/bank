package com.exam.bank.repo.jpa

import com.exam.bank.entity.BnkEtrHst
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BnkEtrRepo : JpaRepository<BnkEtrHst, Int> {
    fun findByTrDyAndBankTranId(trDy: String, bankTranId: String): BnkEtrHst
}


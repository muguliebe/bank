package com.exam.bank.repo.jpa

import com.exam.bank.entity.BnkTrsHst
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BnkTrsHstRepo : JpaRepository<BnkTrsHst, Int>




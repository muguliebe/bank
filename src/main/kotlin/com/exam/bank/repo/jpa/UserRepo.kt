package com.exam.bank.repo.jpa

import com.exam.bank.entity.ComUserMst
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.math.BigInteger

@Repository
interface UserRepo : JpaRepository<ComUserMst, BigInteger>


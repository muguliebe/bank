package com.exam.bank.repo.jpa

import com.exam.bank.entity.ComUserMst
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepo : CrudRepository<ComUserMst, Int>


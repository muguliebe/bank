package com.exam.bank.service

import com.exam.fwk.core.base.BaseService
import org.springframework.stereotype.Service

@Service
class SamService : BaseService() {

    fun sample(): String {
        return "sample"
    }
}

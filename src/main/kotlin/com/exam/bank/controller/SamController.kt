package com.exam.bank.controller

import com.exam.fwk.core.base.BaseController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sam")
class SamController: BaseController() {

    @GetMapping
    fun sam(): String {
        return "sam"
    }

}

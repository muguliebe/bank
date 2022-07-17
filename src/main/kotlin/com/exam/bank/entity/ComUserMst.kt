package com.exam.bank.entity

import org.springframework.context.annotation.Description
import javax.persistence.*

@Entity
@Table(name = "com_user_mst")
@Description("사용자 마스터")
data class ComUserMst(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var userId: Int      = 0  // 사용자 ID
        , var email: String  = "" // 이메일
        , var userNm: String = "" // 성명
)

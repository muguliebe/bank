package com.exam.bank.entity

import org.hibernate.Hibernate
import org.springframework.context.annotation.Description
import java.math.BigInteger
import java.time.OffsetDateTime
import javax.persistence.*

@Entity
@Table(name = "com_user_mst")
@Description("사용자 마스터")
data class ComUserMst(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var userId: BigInteger = BigInteger.ZERO,   // 사용자 ID
    var createDt: OffsetDateTime? = null,       // 생성일시
    var createUserId: String? = null,           // 생성자 ID
    var createPgmId: String? = null,            // 생성프로그램 ID
    var updateDt: OffsetDateTime? = null,       // 수정일시
    var updateUserId: String? = null,           // 수정자 ID
    var UpdatePgmId: String? = null,            // 수정프로그램 ID
    var userNm: String = "",                    // 성명
    var userSeqNo: String = "",                 // 사용자일련번호(오픈뱅킹용)

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ComUserMst

        return userId == other.userId
    }

    override fun hashCode(): Int = javaClass.hashCode()
}

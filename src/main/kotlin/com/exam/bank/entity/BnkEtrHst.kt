package com.exam.bank.entity

import org.hibernate.Hibernate
import org.springframework.context.annotation.Description
import java.math.BigInteger
import java.time.OffsetDateTime
import javax.persistence.*

@Entity
@Table(name = "bnk_etr_hst")
@Description("대외거래 내역")
data class BnkEtrHst(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var seq: BigInteger = BigInteger.ZERO,      // 순번
    var createDt: OffsetDateTime? = null,       // 생성일시
    var createUserId: String? = null,           // 생성자 ID
    var createPgmId: String? = null,            // 생성프로그램 ID
    var updateDt: OffsetDateTime? = null,       // 수정일시
    var updateUserId: String? = null,           // 수정자 ID
    var updatePgmId: String? = null,            // 수정프로그램 ID
    var gid: String = "",                       // gid
    var trDy: String = "",                      // 거래일자
    var etrUrl: String = "",                    // 대외거래URL
    var userId: BigInteger = BigInteger.ZERO,   // 사용자번호
    var bankTranId: String = "",                // 거래고유번호
    var etrStatCd: String = "",                 // 대외거래상태코드 [01:요청, 02:타임아웃, 03:에러수신, 04:정상수신]
    var rspCode: String = "",                   // 응답코드(대외기관)
    var rspMessage: String = "",                // 응답메시지(대외기관)
    var etrReqVal: String = "",                 // 대외거래요청값
    var etrResVal: String = "",                 // 대외거래응답값
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as BnkEtrHst

        return seq == other.seq
    }

    override fun hashCode(): Int = javaClass.hashCode()
    override fun toString(): String {
        return "BnkEtrHst(seq=$seq, gid='$gid', trDy='$trDy', etrUrl='$etrUrl', userId=$userId, bankTranId='$bankTranId', etrStatCd='$etrStatCd', etrReqVal='$etrReqVal', etrResVal='$etrResVal', createDt=$createDt, createUserId=$createUserId, createPgmId=$createPgmId, updateDt=$updateDt, updateUserId=$updateUserId, updatePgmId=$updatePgmId)"
    }

}

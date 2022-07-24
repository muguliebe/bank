package com.exam.bank.ext

import com.exam.bank.entity.BnkEtrHst
import com.exam.bank.ext.OpenApiServiceDto.*
import com.exam.bank.repo.jpa.BnkEtrRepo
import com.exam.bank.repo.mybatis.SequenceMapper
import com.exam.fwk.custom.utils.DateUtils
import com.exam.fwk.custom.utils.ValidUtils
import com.exam.fwk.core.base.BaseService
import com.exam.fwk.core.error.BizException
import com.exam.fwk.core.error.EtrErrException
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.result.Result
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct


@Service
class OpenApiService : BaseService() {

    @Autowired lateinit var mapperSequence: SequenceMapper
    @Autowired lateinit var repoEtr: BnkEtrRepo

    lateinit var objectMapper: ObjectMapper             // JSON Mapper
    @Value("\${server.port}") lateinit var openBankPort: String    // 오픈뱅킹 포트
    val bankIdPrefix = "OPENBANK11U"                    // 이용기관코드

    @PostConstruct
    fun init() {
        objectMapper = ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY) // 빈 필드는 JSON key 생기지 않도록
        val gson: Gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

        // 오픈뱅킹 호출 prepare
        FuelManager.instance.basePath = "http://localhost:${openBankPort}/ext"
        FuelManager.instance.baseHeaders = mapOf("Content-Type" to "application/json;charset=UTF-8")
        FuelManager.instance.addRequestInterceptor(beforeRequest())     // 오픈뱅킹 호출 시 선처리
        FuelManager.instance.addResponseInterceptor(afterResponse())    // 오픈뱅킹 호출 시 선처리
    }

    /**
     * 거래고유번호 가져오기
     * - 20자리
     * - 이용기관코드(10자리) + 생성주체구분코드(U) + 부여번호(9자리)
     * - 부여번호 규칙 = 현재시간(HHmmss) + Random 3자리 숫자
     */
    fun getBankTranId(): String {
        return bankIdPrefix + mapperSequence.selectDailySequence().toString().padStart(9, '0')
    }

    /**
     * 대외거래 요청 전처리
     */
    private fun beforeRequest() = { next: (Request) -> Request ->
        { req: Request ->
            log.info(">>>>>>>>>> API Called   to ${req.url}")
            next(req)
        }
    }

    /**
     * 대외거래 응답 후처리
     */
    private fun afterResponse() = { next: (Request, Response) -> Response ->
        { req: Request, res: Response ->
            log.info("<<<<<<<<<< API Response from ${req.url}")
            next(req, res)
        }
    }

    /**
     * 수취인 조회
     * - bankTranId 는 OpenApiService.getBankTranId() 를 통하여 채번하여 넘겨주어야 합니다.
     */
    fun getConsignee(input: ConsigneeGetReq): ConsigneeGetRes {

        // init
        val url = "/v2.0/inquiry/receive"

        // validation
        ValidUtils.checkRequired(input)

        // 오픈뱅킹 수취인 조회 요청 JSON 생성
        val reqJson = objectMapper.writeValueAsString(input)

        // 요청 전 대외거래내역 저장
        val bnkEtrHst = saveBnkEtrHst(
            bankTranId = input.bankTranId,
            url = url,
            userId = input.reqClientNum,
            etrReqVal = reqJson
        )

        // Response
        val (request, response, result) = Fuel.post(url)
            .jsonBody(reqJson)
            .responseString()

        when (result) {
            is Result.Failure -> {
                log.error("오픈뱅킹 수취인조회 API 에러 발생:${response.responseMessage}")

                updateBnkEtrHst(
                    trDy = bnkEtrHst.trDy,
                    bankTranId = input.bankTranId,
                    etrStatCd = "03", // 대외거래 상태코드 [01:요청, 02:타임아웃, 03:에러수신, 04:정상수신]
                    etrResVal = response.responseMessage,
                    rspCode = null,
                    rspMessage = null,
                )

                throw EtrErrException(result.getException(), "대외거래응답 에러입니다.")
            }
            is Result.Success -> {


                val consigneeGetRes = objectMapper.readValue(result.get(), ConsigneeGetRes::class.java)

                updateBnkEtrHst(
                    trDy = bnkEtrHst.trDy,
                    bankTranId = input.bankTranId,
                    etrStatCd = "04", // 대외거래 상태코드 [01:요청, 02:타임아웃, 03:에러수신, 04:정상수신]
                    etrResVal = result.value,
                    rspCode = consigneeGetRes.rspCode,
                    rspMessage = consigneeGetRes.rspMessage
                )

                return consigneeGetRes
            }
        }

    }

    /**
     * 출금이체 요청
     * - bankTranId 는 OpenApiService.getBankTranId() 를 통하여 채번하여 넘겨주어야 합니다.
     */
    fun postWdtrReq(input: WdtrReq): WdtrRes {
        // init
        val url = "/v2.0/transfer/withdraw/acnt_num"

        // 오픈뱅킹 수취인 조회 요청 JSON 생성
        val reqJson = objectMapper.writeValueAsString(input)

        // 요청 전 대외거래내역 저장
        val bnkEtrHst = saveBnkEtrHst(
            bankTranId = input.bankTranId,
            url = url,
            userId = input.reqClientNum,
            etrReqVal = reqJson
        )

        // Response
        val (request, response, result) = Fuel.post(url)
            .jsonBody(reqJson)
            .responseString()

        when (result) {
            is Result.Failure -> { // 요청 실패 시: 대외거래내역 업데이트 후, 에러 throw
                log.error("오픈뱅킹 출금이체 API 에러 발생:${response.responseMessage}")

                updateBnkEtrHst(
                    trDy = bnkEtrHst.trDy,
                    bankTranId = input.bankTranId,
                    etrStatCd = "03", // 대외거래 상태코드 [01:요청, 02:타임아웃, 03:에러수신, 04:정상수신]
                    etrResVal = response.responseMessage,
                    rspCode = null,
                    rspMessage = null
                )

                throw EtrErrException(result.getException(), "ETR0001") // ETR0001:대외거래응답 에러입니다.
            }
            is Result.Success -> { // 요청 성공 시: 대외거래내역 업데이트 후, 응답 리턴

                val wdtrRes = objectMapper.readValue(result.get(), WdtrRes::class.java)
                updateBnkEtrHst(
                    trDy = bnkEtrHst.trDy,
                    bankTranId = input.bankTranId,
                    etrStatCd = "04", // 대외거래 상태코드 [01:요청, 02:타임아웃, 03:에러수신, 04:정상수신]
                    etrResVal = result.value,
                    rspCode = wdtrRes.rspCode,
                    rspMessage = wdtrRes.rspMessage
                )

                return wdtrRes
            }
        }
    }

    /**
     * 입금이체 단건 요청
     * - bankTranId 는 OpenApiService.getBankTranId() 를 통하여 채번하여 넘겨주어야 합니다.
     * - 입금이체 요청은 단건만 지원합니다.
     */
    fun postDpstr(input: DpstrReq): DpstrRes {

        // init
        val url = "/v2.0/transfer/deposit/acnt_num"

        // 오픈뱅킹 수취인 조회 요청 JSON 생성
        val reqJson = objectMapper.writeValueAsString(input)

        // 요청 전 대외거래내역 저장
        val bnkEtrHst = saveBnkEtrHst(
            bankTranId = input.reqList[0].bankTranId,
            url = url,
            userId = input.reqList[0].reqClientNum,
            etrReqVal = reqJson
        )

        // Response
        val (request, response, result) = Fuel.post(url)
            .jsonBody(reqJson)
            .responseString()

        when (result) {
            is Result.Failure -> { // 요청 실패 시: 대외거래내역 업데이트 후, 에러 throw
                log.error("오픈뱅킹 입금이체 API 에러 발생:${response.responseMessage}")

                updateBnkEtrHst(
                    trDy = bnkEtrHst.trDy,
                    bankTranId = input.reqList[0].bankTranId,
                    etrStatCd = "03", // 대외거래 상태코드 [01:요청, 02:타임아웃, 03:에러수신, 04:정상수신]
                    etrResVal = response.responseMessage,
                    rspCode = null,
                    rspMessage = null,
                )

                throw EtrErrException(result.getException(), "ETR0001") // ETR0001:대외거래응답 에러입니다.
            }
            is Result.Success -> { // 요청 성공 시: 대외거래내역 업데이트 후, 응답 리턴

                val dpstrRes = objectMapper.readValue(result.get(), DpstrRes::class.java)
                updateBnkEtrHst(
                    trDy = bnkEtrHst.trDy,
                    bankTranId = input.reqList[0].bankTranId,
                    etrStatCd = "04", // 대외거래 상태코드 [01:요청, 02:타임아웃, 03:에러수신, 04:정상수신]
                    etrResVal = result.value,
                    rspCode = dpstrRes.rspCode,
                    rspMessage = dpstrRes.rspMessage
                )

                return dpstrRes
            }
        }


    }


    // =================================================================================================================
    // 아래부터는 대외거래내역 DB func
    // =================================================================================================================

    /**
     * 대외거래내역 저장
     */
    private fun saveBnkEtrHst(bankTranId: String, url: String, userId: String, etrReqVal: String): BnkEtrHst {
        // init
        var result: BnkEtrHst

        // prepare for save
        val inSave = BnkEtrHst()
        inSave.createDt = DateUtils.currentTimeStamp()  // 생성일시
        inSave.createPgmId = javaClass.simpleName       // 생성프로그램 ID
        inSave.createUserId = userId                    // 생성자 ID
        inSave.gid = commons.area.gid                   // gid
        inSave.trDy = DateUtils.currentDy()             // 거래일자
        inSave.bankTranId = bankTranId                  // 거래고유번호
        inSave.etrUrl = url                             // 대외거래요청URL
        inSave.userId = userId.toBigInteger()           // 사용자ID
        inSave.etrStatCd = "01"                         // 대외거래 상태코드 [01:요청, 02:타임아웃, 03:에러수신, 04:정상수신]
        inSave.etrReqVal = etrReqVal                    // 대외거래요청값

        try {
            // DB Insert : 대외거래내역
            result = repoEtr.save(inSave)
        } catch (e: Exception) {
            throw BizException("대외거래내역 [{0}] 저장 중 에러가 발생하였습니다.")
        }

        return result
    }

    /**
     * 대외거래내역 수정
     * - 거래일자, 거래고유번호, 대외요청응답값
     */
    private fun updateBnkEtrHst(
        trDy: String,
        bankTranId: String,
        etrStatCd: String,
        etrResVal: String,
        rspCode: String?,
        rspMessage: String?
    ): BnkEtrHst {
        // init
        val result: BnkEtrHst

        // prepare for save
        val exist = repoEtr.findByTrDyAndBankTranId(trDy, bankTranId)
        exist.updateDt = DateUtils.currentTimeStamp()       // 수정일시
        exist.updatePgmId = javaClass.simpleName            // 수정프로그램 ID
        exist.etrStatCd = etrStatCd                         // 대외거래 상태코드 [01:요청, 02:타임아웃, 03:에러수신, 04:정상수신]
        exist.etrResVal = etrResVal
        rspCode?.let {
            exist.rspCode = rspCode
        }
        rspMessage?.let {
            exist.rspMessage = rspMessage
        }


        try {
            // DB Update : 대외거래내역
            result = repoEtr.save(exist)
        } catch (e: Exception) {
            throw BizException("대외거래내역 저장 중 에러가 발생하였습니다.")
        }

        return result
    }

}

package com.exam.bank.ext

import com.exam.bank.entity.BnkEtrHst
import com.exam.bank.repo.jpa.BnkEtrRepo
import com.exam.bank.repo.mybatis.SequenceMapper
import com.exam.bank.utils.DateUtils
import com.exam.fwk.core.base.BaseService
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import com.github.kittinunf.result.Result;


@Service
class OpenApiService : BaseService() {

    @Autowired lateinit var mapperSequence: SequenceMapper
    @Autowired lateinit var repoEtr: BnkEtrRepo

    lateinit var objectMapper: ObjectMapper             // JSON Mapper
    lateinit var gson: Gson                             // GSON
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
     */
    fun getConsignee(input: OpenApiServiceDto.GetConsigneeIn) {
        // init
        val url = "/v2.0/inquiry/receive"
        val req: OpenApiServiceDto.ConsigneeGetReq = OpenApiServiceDto.ConsigneeGetReq() // 오픈뱅킹용 req body
        BeanUtils.copyProperties(input, req)

        // 오픈뱅킹 수취인 조회 요청 JSON 생성
        req.bankTranId = getBankTranId() // 거래고유번호
        val reqJson = objectMapper.writeValueAsString(req)

        // 요청 전 대외거래내역 저장
        val bnkEtrHst = saveBnkEtrHst(req.bankTranId, url, input.reqClientNum, reqJson)

        // Response
        val (request, response, result) = Fuel.post(url)
            .jsonBody(reqJson)
            .responseString()

        when (result) {
            is Result.Failure -> {
                log.error("오픈뱅킹 수취인조회 API 에러 발생")
                updateErrBnkEtrHst(bnkEtrHst.trDy, req.bankTranId, response.responseMessage)
            }
            is Result.Success -> {
                updateOkBnkEtrHst(bnkEtrHst.trDy, req.bankTranId, result.value)
//                val json = objectMapper.readValue(result.value, GetConsigneeRes::class.java)
            }
        }


    }

    /**
     * 출금이체 요청
     */
    fun postWdtrReq () {

    }

    /**
     * 입금이체 요청
     */
    fun postDpstr() {

    }


    // =================================================================================================================
    // 아래부터는 대외거래내역 DB func
    // =================================================================================================================

    /**
     * 대외거래내역 저장
     */
    private fun saveBnkEtrHst(bankTranId: String, url: String, reqClientNum: String, etrReqVal: String): BnkEtrHst {
        // DB Insert : 대외거래내역
        val inSave = BnkEtrHst()
        inSave.createDt = DateUtils.currentTimeStamp()  // 생성일시
        inSave.createPgmId = javaClass.simpleName       // 생성프로그램 ID
        inSave.createUserId = reqClientNum              // 생성자 ID
        inSave.gid = commons.area.gid                   // gid
        inSave.trDy = DateUtils.currentDy()             // 거래일자
        inSave.bankTranId = bankTranId                  // 거래고유번호
        inSave.etrUrl = url                             // 대외거래요청URL
        inSave.userId = Integer.valueOf(reqClientNum)   // 사용자ID
        inSave.etrStatCd = "01"                         // 대외거래 상태 코드 [01:요청, 02:타임아웃, 03:에러수신, 04:정상수신]
        inSave.etrReqVal = etrReqVal                    // 대외거래요청값
        val saved = repoEtr.save(inSave)
        log.debug("대외거래내역 저장:${saved}")
        return inSave
    }

    /**
     * 대외거래내역 정상응답 수정
     * - 거래일자, 거래고유번호, 대외요청응답값
     */
    private fun updateOkBnkEtrHst(trDy: String, bankTranId: String, etrResVal: String) {
        val exist = repoEtr.findByTrDyAndBankTranId(trDy, bankTranId)
        exist.updateDt = DateUtils.currentTimeStamp()       // 생성일시
        exist.updatePgmId = javaClass.simpleName            // 생성프로그램 ID
        exist.etrStatCd = "04"                              // 대외거래 상태 코드 [01:요청, 02:타임아웃, 03:에러수신, 04:정상수신]
        exist.etrResVal = etrResVal
        log.debug("대외거래내역 수정:${exist}")
        repoEtr.save(exist)
    }

    /**
     * 대외거래내역 에러응답 수정
     * - 거래일자, 거래고유번호, 대외요청응답값
     */
    private fun updateErrBnkEtrHst(trDy: String, bankTranId: String, etrResVal: String?) {
        val exist = repoEtr.findByTrDyAndBankTranId(trDy, bankTranId)
        exist.updateDt = DateUtils.currentTimeStamp()       // 생성일시
        exist.updatePgmId = javaClass.simpleName            // 생성프로그램 ID
        exist.etrStatCd = "04"                              // 대외거래 상태 코드 [01:요청, 02:타임아웃, 03:에러수신, 04:정상수신]
        etrResVal?.let{ exist.etrResVal = etrResVal }
        val saved = repoEtr.save(exist)
        log.debug("대외거래내역 수정:${saved}")
    }


}

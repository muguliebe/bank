package com.exam.fwk.custom.utils

import com.exam.fwk.core.annotation.Required
import com.exam.fwk.core.error.BadRequestException
import com.exam.fwk.core.error.BaseException
import com.exam.fwk.core.error.SystemException
import java.lang.reflect.Field

/**
 * Validation 유틸
 */
class ValidUtils {


    companion object {

        @Throws(IllegalAccessException::class, NoSuchFieldException::class)
        private fun getMemberFields(
            obj: Any
        ): Array<Field> {
            val objClass = obj.javaClass
            return objClass.declaredFields
        }


        /**
         * Dto 내 @Required 어노테이션이 선언된 필드가 null 혹은 공백 인지 체크 합니다.
         * - Required 속성 중 includeZero 가 ture 인 경우, 0 도 에러 처리 합니다.
         */
        @Throws(BaseException::class)
        fun checkRequired(obj: Any) {
            try {
                val fields = getMemberFields(obj)
                for (field in fields) {
                    field.isAccessible = true
                    val required = field.getAnnotation(Required::class.java)
                    if (field.get(obj) == null || field.get(obj) == 0 || (field.get(obj) != null && field.get(obj).equals(""))) {
                        when {
                            (required != null && required.value) && (field.get(obj) == null || (field.get(obj) != null && field.get(obj).equals(""))
                                    ) -> {
                                throw BadRequestException( "${field.name} 은(는) 필수 입력값 입니다.")
                            }
                            required != null && !required.includeZero && field.get(obj) == 0 -> {
                                throw BadRequestException( "${field.name} 은(는) 0보다 이상이어야 합니다.")
                            }
                        }
                    }
                }

            } catch (e: IllegalAccessException) {
                throw SystemException( "Input Check 중 에러입니다. 사유:{${e.message.toString()}}")
            } catch (e: NoSuchFieldException) {
                throw SystemException( "Input Check 중 에러입니다. 사유:{${e.message.toString()}}")
            }

        }
    }

}

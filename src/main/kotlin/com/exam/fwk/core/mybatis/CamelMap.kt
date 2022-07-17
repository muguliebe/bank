package com.exam.fwk.core.mybatis

import com.google.common.base.CaseFormat

class CamelMap : HashMap<Any, Any>() {

    override fun put(key: Any, value: Any): Any? {
        return super.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key as String), value)
    }

}

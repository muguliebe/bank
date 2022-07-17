package com.exam.fwk.core.component

import com.exam.fwk.custom.dto.CommonArea
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
data class Area(
        var commons: CommonArea = CommonArea()
) : Cloneable {

    override fun clone(): CommonArea {
        return super.clone() as CommonArea
    }

}

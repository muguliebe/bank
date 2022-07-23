package com.exam.fwk.core.annotation

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Required(
    val value: Boolean = true,
    val includeZero: Boolean = false
)

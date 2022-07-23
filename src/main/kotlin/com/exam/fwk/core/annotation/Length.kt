package com.exam.fwk.core.annotation

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Length(
    val len: Long=0,
    val min: Long=0,
    val max: Long=0
)

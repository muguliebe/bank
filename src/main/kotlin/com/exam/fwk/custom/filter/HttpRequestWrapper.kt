package com.exam.fwk.custom.filter

import org.apache.commons.io.IOUtils
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

/**
 * Servlet Request 의 Header 를 추가/제거 가능토록 구현한 클래스
 */
class HttpRequestWrapper @Throws(IOException::class)
constructor(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    private var headerMap: MutableMap<String, String> = HashMap()
    private val bodyData: ByteArray

    init {
        val inputStream = super.getInputStream()
        bodyData = IOUtils.toByteArray(inputStream)
    }

    fun addHeader(name: String, value: String) {
        headerMap[name] = value
    }

    override fun getHeader(name: String): String? {
        return if (headerMap.containsKey(name))
            headerMap[name]
        else
            super.getHeader(name)
    }

    override fun getHeaderNames(): Enumeration<String> {
        val names = super.getHeaderNames().toList() as MutableList<String>
        names.addAll(headerMap.keys)
        return Collections.enumeration(names)
    }

    override fun getHeaders(name: String): Enumeration<String> {
        val values = super.getHeaders(name).toList() as MutableList<String>
        if (headerMap.containsKey(name))
            headerMap[name]?.let { values.add(it) }
        return Collections.enumeration(values)
    }

    override fun getRemoteAddr(): String {
        return getHeader("real-ip")?.let {
            getHeader("real-ip")
        }.let {
            super.getRemoteAddr()
        }
    }

    fun getBody(): ByteArray {
        return this.bodyData
    }

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream = ServletImpl(ByteArrayInputStream(bodyData))

    internal inner class ServletImpl(private val inputStream: InputStream) : ServletInputStream() {

        @Throws(IOException::class)
        override fun read(): Int = inputStream.read()

        @Throws(IOException::class)
        override fun read(b: ByteArray): Int = inputStream.read(b)

        override fun isFinished(): Boolean = false
        override fun isReady(): Boolean = false
        override fun setReadListener(listener: ReadListener) {}

    }

}

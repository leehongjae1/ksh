@file:Suppress("DEPRECATION")

package com.choistec.cmspc.api.define

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.experimental.and
import kotlin.math.pow

/**
 * 공통 사용 함수
 *
 * 여기저기서 쓰이는 함수들
 *
 * @author  김민규 2018-03-29.
 * @since 2.1.8
 */
object StaticFunc {
    fun ClosedRange<Int>.random() =
            Random().nextInt((endInclusive + 1) - start) +  start
    fun ClosedFloatingPointRange<Double>.random() =
            Random().nextDouble()*((endInclusive)-start) +  start
    fun Double.round(digit:Int) =
            Math.round(this*10.0.pow(digit))/10.0.pow(digit)

    fun <T> List<T>.use(separater:Int,block: List<T>.() -> Unit):List<T> {
        take(separater).block()
        return this.drop(separater)
    }

    fun ByteArray.toHex(
            separator:String = ""
    ) = joinToString(separator) { String.format("%02x", it and 0xff.toByte()) }

    fun sha256encode(password:String) = MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray()).fold("") { str, it->str + "%02x".format(it)}

    val offset = SimpleDateFormat("Z", Locale.ROOT)
            .format(Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault()).time)
            .toInt()/100.0f

    /**기본 날짜 포맷*/
    var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    /**현재 시간 가져오기
     *
     * @return new date*/
    val now: String
        get() = dateFormat.format(Date())

}

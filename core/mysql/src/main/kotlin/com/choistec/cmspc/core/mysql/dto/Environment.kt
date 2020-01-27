package com.choistec.cmspc.core.mysql.dto

import com.choistec.cmspc.core.dto.*

data class Environment(
        var admin_id: String?=null,
        var device_id: Int?=null,
        var time: Long = now(),
        var dust_data: Float?=null,
        var co2_data: Float?=null,
        var temp_data: Float?=null,
        var humi_data: Float?=null
){
    fun toRealTimeQuery(): String = StringBuilder("").apply {

        /*주변 환경 정보를 문자형으로 바꿔 이어 붙인다.*/
        if(dust_data!=null) {
            append("${DUST}_$DATA=$dust_data,")
            append("${DUST}_$MOD_DATE=$NOW,")
        }
        if(co2_data!=null){
            append("${CO2}_$DATA=$co2_data,")
            append("${CO2}_$MOD_DATE=$NOW,")
        }
        if(temp_data!=null){
            append("${TEMP}_$DATA=$temp_data,")
            append("${TEMP}_$MOD_DATE=$NOW,")
        }
        if(humi_data!=null){
            append("${HUMI}_$DATA=$humi_data,")
            append("${HUMI}_$MOD_DATE=$NOW,")
        }
    }.dropLast(1).toString()

    fun toGraphQuery(): String = StringBuilder("").apply {

        /*주변 환경 정보를 문자형으로 바꿔 이어 붙인다.*/
        if(dust_data!=null) {
            append("${DUST}_$DATA=$dust_data,")
        }
        if(co2_data!=null){
            append("${CO2}_$DATA=$co2_data,")
        }
        if(temp_data!=null){
            append("${TEMP}_$DATA=$temp_data,")
        }
        if(humi_data!=null){
            append("${HUMI}_$DATA=$humi_data,")
        }
        append("$REG_DATE=$NOW,")

    }.dropLast(1).toString()

    companion object {
        val now = {System.currentTimeMillis()/1000}

        fun nullQuery(): String = StringBuilder("").apply {
            append("${DUST}_$DATA=null,")
            append("${CO2}_$DATA=null,")
            append("${TEMP}_$DATA=null,")
            append("${HUMI}_$DATA=null,")
        }.dropLast(1).toString()
    }
}

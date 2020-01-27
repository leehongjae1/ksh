package com.choistec.cmspc.core.mysql.dto

import com.choistec.cmspc.core.dto.*

data class Measure(
        var baby_id : String?=null,
        var time : Long = now(),
        var temp_data : Float?=null,
        var temp_battery : Int?=null,
        var heart_data : Float?=null,
        var heart_battery : Int?=null,
        var breath_data : Float?=null,
        var breath_battery : Int?=null,
        var spo2_data : Float?=null,
        var spo2_battery : Int?=null,
        var weight_data : Float?=null,
        var weight_battery : Int?=null
){
    fun toRealTimeQuery(): String = StringBuilder("").apply {
        /**
         * 실시간 측정 데이터 중에
         * 받은 데이터가 있으면
         * 쿼리문에 추가해서 값을
         * 넣어준다.
         */
        if(temp_data!=null) {
            append("${TEMP}_$DATA=$temp_data,")
            append("${TEMP}_$BATTERY=$temp_battery,")
            append("${TEMP}_$MOD_DATE=$NOW,")
        }
        if(heart_data!=null){
            append("${HEART}_$DATA=$heart_data,")
            append("${HEART}_$BATTERY=$heart_battery,")
            append("${HEART}_$MOD_DATE=$NOW,")
        }
        if(breath_data!=null) {
            append("${BREATH}_$DATA=$breath_data,")
            append("${BREATH}_$BATTERY=$breath_battery,")
            append("${BREATH}_$MOD_DATE=$NOW,")
        }
        if(spo2_data!=null){
            append("${SPO2}_$DATA=$spo2_data,")
            append("${SPO2}_$BATTERY=$spo2_battery,")
            append("${SPO2}_$MOD_DATE=$NOW,")
        }
        if(weight_data!=null){
            append("${WEIGHT}_$DATA=$weight_data,")
            append("${WEIGHT}_$BATTERY=$weight_battery,")
            append("${WEIGHT}_$MOD_DATE=$NOW,")
        }

    }.dropLast(1).toString()

    fun toQuery(): String = StringBuilder("").apply {
        temp_data?.let { append("$TEMP=$it,") }
        heart_data?.let { append("$HEART=$it,") }
        breath_data?.let { append("$BREATH=$it,") }
        spo2_data?.let { append("$SPO2=$it,") }
        weight_data?.let { append("$WEIGHT=$it,") }
    }.dropLast(1).toString()

    companion object {
        val now = {System.currentTimeMillis()/1000}

        fun nullQuery(): String = StringBuilder("").apply {
            append("${TEMP}_$DATA=null,")
            append("${HEART}_$DATA=null,")
            append("${BREATH}_$DATA=null,")
            append("${SPO2}_$DATA=null,")
            append("${WEIGHT}_$DATA=null,")
        }.dropLast(1).toString()
    }
}

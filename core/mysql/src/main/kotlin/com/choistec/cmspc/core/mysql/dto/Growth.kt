package com.choistec.cmspc.core.mysql.dto

import com.choistec.cmspc.core.dto.*

data class Growth(
        var admin_id: String?=null,
        var baby_id: String?=null,
        var device_id: Int?=null,
        var time: Long = now(),
        var nurse_data: Float?=null,
        var nurse_index_data: Float?=null,
        var urine_data: Float?=null,
        var urine_index_data: Float?=null,
        var feces_data: Float?=null,
        var feces_index_data: Float?=null,
        var vomit_data: Float?=null,
        var vomit_index_data: Float?=null
){
    fun toRealTimeQuery(): String = StringBuilder("").apply {

        /*주변 환경 정보를 문자형으로 바꿔 이어 붙인다.*/
        if(nurse_data!=0.toFloat()) {
            append("nurse=$nurse_data,")
            append("nurse_mod_date=$NOW,")
        }
        if(feces_data!=0.toFloat()){
            append("feces=$feces_data,")
            append("feces_mod_date=$NOW,")
        }
        if(urine_data!=0.toFloat()){
            append("urine=$urine_data,")
            append("urine_mod_date=$NOW,")
        }
        if(vomit_data!=0.toFloat()){
            append("vomit=$vomit_data,")
            append("vomit_mod_date=$NOW,")
        }
        append("reg_date=$NOW,")

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

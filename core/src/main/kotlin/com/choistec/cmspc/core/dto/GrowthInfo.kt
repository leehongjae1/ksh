package com.choistec.cmspc.core.dto

data class GrowthInfo (
        var growth_id:String? = null,
        var admin_id:String? = null,
        var baby_id:String? = null,
        var nurse:String? = null,
        var nurse_mod_date:String? = null,
        var feces:String? = null,
        var feces_mod_date:String? = null,
        var urine:String? = null,
        var urine_mod_date:String? = null,
        var vomit:String? = null,
        var vomit_mod_date:String? = null,
        var reg_date:String? = null
){

    fun toRealTimeQuery(): String = StringBuilder("").apply {

        /*주변 환경 정보를 문자형으로 바꿔 이어 붙인다.*/
        if(nurse?.toFloat()!=0.toFloat()) {
            append("nurse=$nurse,")
            append("nurse_mod_date=$NOW,")
        }
        if(feces?.toFloat()!=0.toFloat()){
            append("feces=$feces,")
            append("feces_mod_date=$NOW,")
        }
        if(urine?.toFloat()!=0.toFloat()){
            append("urine=$urine,")
            append("urine_mod_date=$NOW,")
        }
        if(vomit?.toFloat()!=0.toFloat()){
            append("vomit=$vomit,")
            append("vomit_mod_date=$NOW,")
        }
        append("reg_date=$NOW,")

    }.dropLast(1).toString()

}



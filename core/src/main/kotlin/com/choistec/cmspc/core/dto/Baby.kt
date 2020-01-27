package com.choistec.cmspc.core.dto

data class Baby (
        var admin_id:String? = null,
        var baby_position_number:Int? = null,
        var baby_id:Int? = null,
        var baby_name:String? = null,
        var baby_sex:Int? = null,
        var baby_birthday:String?   = null,
        var rearer_name:String? = null,
        var rearer_contact:String? = null,
        var rearer_birthday:String?   = null,
        var reg_date:String?   = null,
        var mod_date:String?   = null,
        var ward_name:String?   = null,
        var cam_id:Int? = null,
        var device_id:Int? = null,
        var del_yn:Int? = null
){

    data class RealTimeData(
            val baby_id:Int,
            val temp:Float?,
            val heart:Float?,
            val breath:Long?,
            val spo2:Float?,
            val weight:Float?
    )

    data class Growth(
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
        var id:Int?=null
    }
}
